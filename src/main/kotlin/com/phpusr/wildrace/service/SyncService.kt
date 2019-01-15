package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.data.TempDataRepo
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.PostRepo
import com.phpusr.wildrace.domain.vk.Profile
import com.phpusr.wildrace.domain.vk.ProfileRepo
import com.phpusr.wildrace.dto.EventType
import com.phpusr.wildrace.dto.PostDto
import com.phpusr.wildrace.dto.PostDtoObject
import com.phpusr.wildrace.enum.PostParserStatus
import com.phpusr.wildrace.parser.MessageParser
import com.phpusr.wildrace.util.Util
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.function.BiConsumer

@Service
class SyncService(
        private val configService: ConfigService,
        private val postRepo: PostRepo,
        private val tempDataRepo: TempDataRepo,
        private val profileRepo: ProfileRepo,
        private val vkApiService: VKApiService,
        private val postSender: BiConsumer<EventType, PostDto>
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Кол-во постов для скачивания за 1 раз
     * Изменения в постах фиксируются только в последних 100
     */
    private val downloadPostsCount = 100
    /**
     * Кол-во последних постов для нахождения последних данных
     * Предполагается, что последние 100 могут измениться
     */
    private val lastPostsCount = downloadPostsCount + 1
    /** Интервал между запросами к VK */
    private val syncBlockInterval = 1000L
    /** Интервал между публикациями комментариев */
    private val publishingCommentInterval = 300L

    fun syncPosts() {
        logger.debug("-------- Start sync --------")

        if (!configService.get().syncPosts) {
            return
        }

        var needSync = true
        while(needSync) {
            val countPosts = getCountPosts()
            val alreadyDownloadCount = postRepo.count()
            logger.debug(">> Download: $alreadyDownloadCount/$countPosts")
            if (needSync) {
                syncBlockPosts(countPosts, alreadyDownloadCount, downloadPostsCount)
                needSync = alreadyDownloadCount < countPosts
                Thread.sleep(syncBlockInterval)
            }
        }

        updateLastSyncDate()
    }

    private fun getCountPosts(): Long {
        val result = vkApiService.wallGet(0, 1, false)

        if (result == null) {
            throw Exception("Response is null")
        }

        return ((result["response"] as Map<*, *>)["count"] as Int).toLong()
    }

    private fun syncBlockPosts(countPosts: Long, alreadyDownloadCount: Long, downloadCount: Int) {
        val offset = if (countPosts - alreadyDownloadCount > downloadCount) {
            countPosts - alreadyDownloadCount - downloadCount
        } else 0

        val dbProfiles = profileRepo.findAll()

        val result = vkApiService.wallGet(offset, downloadCount, true)
        val response = result!!["response"] as Map<*, *>

        if ((response["count"] as Int).toLong() != countPosts) {
            return
        }

        val vkPosts = (response["items"] as List<*>).reversed()
        val vkProfiles = response["profiles"] as List<*>

        val lastDbPosts = getLastPosts()
        removeDeletedPosts(vkPosts, lastDbPosts)

        vkPosts.forEach {
            val vkPost = it as Map<*, *>
            val postId = (vkPost["id"] as Int).toLong()
            val text = Util.removeBadChars(vkPost["text"] as String) ?: ""
            val textHash = Util.MD5(text)
            val dbPost = lastDbPosts.find { it.id == postId }
            val postDate = Date((vkPost["date"] as Int).toLong() * 1000)
            val lastPost = lastDbPosts.find{ it.number != null && it.id != postId && it.date <= postDate }
            val lastSumDistance = lastPost?.sumDistance ?: 0
            val lastPostNumber = lastPost?.number ?: 0

            if (dbPost != null) {
                // Если пост уже есть в базе и (он не менялся или было ручное ред-е), то переход к следующему
                if (textHash == dbPost.textHash && dbPost.startSum == lastSumDistance || dbPost.lastUpdate != null) {
                    return@forEach
                }

                analyzePostText(text, textHash, lastSumDistance, lastPostNumber, dbPost, EventType.Update)
                return@forEach
            }

            // Поиск или создание профиля пользователя
            val dbProfile = findOrCreateProfile(vkPost, dbProfiles, vkProfiles, postDate)
            val newPost = Post(postId, PostParserStatus.Success.id, dbProfile, postDate)

            val parserOut = analyzePostText(text, textHash, lastSumDistance, lastPostNumber, newPost, EventType.Create)

            // Добавление нового поста в список последних постов и сортировка постов по времени
            if (parserOut) {
                lastDbPosts.add(newPost)
                lastDbPosts.sortBy { it.date.time * -1 }
            }
        }
    }

    private fun getLastPosts(): MutableList<Post> {
        val pageable = PageRequest.of(0, lastPostsCount, Sort(Sort.Direction.DESC, "date"))
        return postRepo.findAll(pageable, null, null).toMutableList()
    }

    /** Удаление из БД удаленных постов */
    private fun removeDeletedPosts(vkPosts: List<Any?>, lastDbPosts: MutableList<Post>) {
        val startDate = Date.from(ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDate = Date(startDate.time + 24 * 3600 * 1000 - 1)
        val todayPosts = lastDbPosts.filter{ it.date >= startDate && it.date <= endDate }
        val deletedPosts = todayPosts.filter { post ->
            vkPosts.find {
                val vkPost = it as Map<*, *>
                (vkPost["id"] as Int).toLong() == post.id
            } == null
        }

        if (deletedPosts.isEmpty()) {
            return
        }

        logger.debug(">> Delete vkPosts: ${deletedPosts}")
        deletedPosts.forEach {
            logger.debug(" -- Delete: ${it}")
            it.number = null
            postRepo.delete(it)
            postSender.accept(EventType.Remove, PostDtoObject.create(it))
            lastDbPosts.remove(it)
        }
    }

    private fun findOrCreateProfile(postMap: Map<*, *>, dbProfiles: MutableIterable<Profile>,
                                    vkProfiles: List<*>, postDate: Date): Profile {
        val profileId = (postMap["from_id"] as Int).toLong()
        var dbProfile = dbProfiles.find { it.id == profileId }
        if (dbProfile == null) {
            val vkProfile = vkProfiles.find {
                val map = it as Map<*, *>
                (map["id"] as Int).toLong() == profileId
            }
            val profileMap = if (vkProfile == null) {
                mapOf("first_name" to "Unknown", "lastName" to "Unknown")
            } else {
                vkProfile as Map<*, *>
            }
            dbProfile = Profile(profileId, postDate)
            dbProfile.firstName = profileMap["first_name"] as String?
            dbProfile.lastName = profileMap["lastName"] as String?
            dbProfile.sex = profileMap["sex"] as Int?
            dbProfile.photo_50 = profileMap["photo_50"] as String?
            dbProfile.photo_100 = profileMap["photo_100"] as String?
            profileRepo.save(dbProfile)
            dbProfiles.plus(dbProfile)
        }

        return dbProfile
    }

    /** Анализ сообщения и вытаскивание дистанции */
    private fun analyzePostText(text: String, textHash: String, lastSumDistance: Int, lastPostNumber: Int, post: Post,
                                eventType: EventType): Boolean {
        val parserOut = MessageParser(text).run()
        val status: PostParserStatus
        val number: Int?
        val distance: Int?
        val newSumDistance: Int?

        post.text = text
        post.textHash = textHash
        if (parserOut != null) {
            // Проверка суммы
            distance = parserOut.distance!!.sum()
            newSumDistance = lastSumDistance + distance
            if (parserOut.startSumNumber == lastSumDistance) {
                if (newSumDistance == parserOut.endSumNumber) {
                    status = PostParserStatus.Success
                } else {
                    status = PostParserStatus.ErrorSum
                }
            } else {
                status = PostParserStatus.ErrorStartSum
            }

            number = lastPostNumber + 1
        } else {
            status = PostParserStatus.ErrorParse
            number = null
            distance = null
            newSumDistance = null
        }

        // Проверка: поменялось-ли выражение суммы в тексте
        if (post.number != number || post.distance != distance || post.sumDistance != newSumDistance || post.statusId != status.id) {
            post.number = number
            post.distance = distance
            post.sumDistance = newSumDistance
            post.statusId = status.id

            postRepo.save(post)
            logger.debug(" -- ${eventType.name} post after analyze: ${post}")
            postSender.accept(eventType, PostDtoObject.create(post, configService.get()))

            // Комментарий статуса обработки поста
            val commentText = createCommentText(post, lastSumDistance, newSumDistance)
            addStatusComment(post.id, commentText)
        }

        return parserOut != null
    }

    private fun createCommentText(post: Post, startSumNumber: Int?, endSumNumber: Int?): String {
        val commentText = StringBuilder()

        // Обращение
        if (post.statusId != PostParserStatus.Success.id) {
            commentText.append(if (post.from.id > 0) {
                "@id${post.from.id} (${post.from.firstName}), "
            } else {
                "@club${post.from.id * -1} (${post.from.firstName}), "
            })
        }

        // № пробежки
        if (post.statusId != PostParserStatus.ErrorParse.id) {
            commentText.append("#${post.number} пробежка: ")
        }

        commentText.append(when(post.statusId) {
            PostParserStatus.Success.id -> "Пост успешно обработан"
            PostParserStatus.ErrorSum.id -> "Ошибка при сложении, должно быть: ${endSumNumber}"
            PostParserStatus.ErrorParse.id -> "Ошибка в формате записи, пост не распознан"
            PostParserStatus.ErrorStartSum.id -> "Ошибка в стартовой сумме, должна быть: ${startSumNumber}"
            else -> "Ошибка: Не предусмотренный статус, напишите администратору"
        })

        return commentText.toString()
    }

    private fun addStatusComment(postId: Long, commentText: String) {
        if (!configService.get().commenting) {
            return
        }

        // Задержка перед добавлением комментария, чтобы не заблокировали пользователя
        Thread.sleep(publishingCommentInterval)

        vkApiService.wallAddComment(postId, commentText)
    }

    private fun updateLastSyncDate() {
        val tempData = tempDataRepo.get()
        tempDataRepo.save(tempData.copy(lastSyncDate = Date()))
    }

    fun updateNextPosts(updatePost: Post) {
        val startPost = if (updatePost.number != null) {
            updatePost
        } else {
            val pageable = PageRequest.of(0, 1, Sort(Sort.Direction.DESC, "date"))
            postRepo.findRunningPage(pageable, null, updatePost.date).getOrNull(0)
        }
        logger.debug("> Update next, start: ${startPost}")

        var currentPostNumber = startPost?.number ?: 0
        var currentSumDistance = startPost?.sumDistance ?: 0

        val pageable = PageRequest.of(0, Int.MAX_VALUE, Sort(Sort.Direction.ASC, "date"))
        val nextPosts = postRepo.findRunningPage(pageable, updatePost.date).filter {
            startPost == null || it.id != startPost.id && it.date >= startPost.date
        }

        nextPosts.forEach { post ->
            analyzePostText(post.text, post.textHash, currentSumDistance, currentPostNumber, post, EventType.Update)
            currentSumDistance = post.sumDistance!!
            currentPostNumber = post.number!!
        }
    }

}