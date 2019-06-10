package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.Post
import com.phpusr.wildrace.domain.PostRepo
import com.phpusr.wildrace.domain.Profile
import com.phpusr.wildrace.domain.ProfileRepo
import com.phpusr.wildrace.dto.EventType
import com.phpusr.wildrace.dto.PostDto
import com.phpusr.wildrace.dto.PostDtoObject
import com.phpusr.wildrace.enum.PostParserStatus
import com.phpusr.wildrace.parser.MessageParser
import com.phpusr.wildrace.util.Util
import com.vk.api.sdk.exceptions.ApiException
import com.vk.api.sdk.objects.wall.WallPostFull
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.function.BiConsumer

@Transactional(readOnly = true, rollbackFor = [ApiException::class])
@Service
class SyncService(
        private val configService: ConfigService,
        private val postRepo: PostRepo,
        private val statService: StatService,
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
    private val lastPostsCount = downloadPostsCount * 2
    /** Интервал между запросами к VK */
    private val syncBlockInterval = 1000L
    /** Интервал между получением пользователей */
    private val gettingUserInterval = 300L
    /** Интервал между публикациями комментариев */
    private val publishingCommentInterval = 300L

    @Transactional
    fun syncPosts() {
        logger.debug("-------- Start sync --------")

        var needSync = true
        while(needSync) {
            val vkPostsCount = vkApiService.wallGet(0, 1).count
            val dbPostsCount = syncBlockPosts(vkPostsCount, downloadPostsCount)
            logger.debug(">> Downloaded (after sync): $dbPostsCount/$vkPostsCount")

            if (dbPostsCount > vkPostsCount) {
                throw RuntimeException("Number of posts in DB ($dbPostsCount) > number of posts in VK ($vkPostsCount)")
            }

            needSync = dbPostsCount < vkPostsCount

            if (needSync) {
                Thread.sleep(syncBlockInterval)
            }
        }

        statService.updateStat()

        logger.debug("-------- End sync --------")
    }

    private fun syncBlockPosts(vkPostsCount: Int, downloadCount: Int): Int {
        val dbPostsCount = postRepo.count().toInt()
        logger.debug(">> Downloaded (before sync): $dbPostsCount/$vkPostsCount")

        val offset = if (vkPostsCount - dbPostsCount > downloadCount) {
            vkPostsCount - dbPostsCount - downloadCount
        } else 0

        val dbProfiles = profileRepo.findAll().toMutableList()

        val response = vkApiService.wallGet(offset, downloadCount)

        if (response.count != vkPostsCount) {
            logger.debug(" -- Number of posts in VK changed: $vkPostsCount -> ${response.count}")
            return dbPostsCount
        }

        val vkPosts = response.items.reversed()

        val lastDbPosts = getLastPosts(lastPostsCount)
        val deletedPosts = removeDeletedPosts(vkPosts, lastDbPosts)

        vkPosts.forEach { vkPost ->
            val postId = vkPost.id.toLong()
            val postText = Util.removeBadChars(vkPost.text) ?: ""
            val postDate = Date(vkPost.date.toLong() * 1000)
            val textHash = Util.MD5(postText)
            val dbPost = lastDbPosts.find { it.id == postId }
            val lastPost = lastDbPosts.find { it.number != null && it.id != postId && it.date <= postDate }
            val lastSumDistance = lastPost?.sumDistance ?: 0
            val lastPostNumber = lastPost?.number ?: 0

            if (dbPost != null) {
                // Если пост уже есть в базе и (он не менялся или было ручное ред-е), то переход к следующему
                if (textHash == dbPost.textHash && dbPost.startSum == lastSumDistance || dbPost.lastUpdate != null) {
                    return@forEach
                }

                analyzePostText(postText, textHash, lastSumDistance, lastPostNumber, dbPost, EventType.Update)
                return@forEach
            }

            // Поиск или создание профиля пользователя
            val profile = findOrCreateProfile(vkPost, postDate, dbProfiles)
            val newPost = Post(postId, PostParserStatus.Success.id, profile, postDate)

            val parserOut = analyzePostText(postText, textHash, lastSumDistance, lastPostNumber, newPost, EventType.Create)

            // Добавление нового поста в список последних постов и сортировка постов по времени
            if (parserOut) {
                lastDbPosts.add(newPost)
                lastDbPosts.sortBy { it.date.time * -1 }
            }
        }

        // Deleting posts from the client, after sync without exceptions
        deletedPosts.forEach { postSender.accept(EventType.Remove, PostDtoObject.create(it)) }

        return postRepo.count().toInt()
    }

    private fun getLastPosts(postsCount: Int): MutableList<Post> {
        val pageable = PageRequest.of(0, postsCount, Sort(Sort.Direction.DESC, "date"))
        return postRepo.findAll(pageable, null, null).toMutableList()
    }

    /** Удаление из БД удаленных постов */
    private fun removeDeletedPosts(vkPosts: List<WallPostFull>, lastDbPosts: MutableList<Post>): List<Post> {
        // Поиск постов за последние ${numberOfLastDays} дней
        val numberOfLastDays = 5
        val startDate = Date(Date().time - 24 * 3600 * 1000 * numberOfLastDays)
        val lastDayPosts = lastDbPosts.filter{ it.date >= startDate }
        val deletedPosts = lastDayPosts.filter { post ->
            vkPosts.find { it.id == post.id.toInt() } == null
        }

        if (deletedPosts.isEmpty()) {
            return deletedPosts
        }

        logger.debug(">> Delete vkPosts, number: ${deletedPosts.size}")
        deletedPosts.forEach { post ->
            logger.debug(" -- Delete: $post")
            postRepo.delete(post)
            lastDbPosts.remove(post)
        }

        return deletedPosts
    }

    private fun findOrCreateProfile(vkPost: WallPostFull, postDate: Date, dbProfiles: MutableList<Profile>): Profile {
        val profileId = vkPost.fromId.toLong()
        var dbProfile = dbProfiles.find { it.id == profileId }
        if (dbProfile != null) {
            return dbProfile
        }

        dbProfile = Profile(profileId, postDate).apply {
            firstName = "Unknown"
        }

        if (profileId >= 0) {
            // Задержка перед получением пользователя, чтобы не заблокировали пользователя
            Thread.sleep(gettingUserInterval)
            val vkUser = vkApiService.usersGetById(profileId.toInt())
            if (vkUser != null) {
                with(dbProfile) {
                    firstName = vkUser.firstName
                    lastName = vkUser.lastName
                    sex = vkUser.sex.value
                    photo_50 = vkUser.photo50
                    photo_100 = vkUser.photo100
                }
            }
        } else {
            val vkGroup = vkApiService.groupsGetById(profileId.toInt() * -1)
            if (vkGroup != null) {
                with(dbProfile) {
                    firstName = vkGroup.name
                    photo_50 = vkGroup.photo50
                    photo_100 = vkGroup.photo100
                    photo_200 = vkGroup.photo200
                }

            }
        }

        profileRepo.save(dbProfile)
        dbProfiles.add(dbProfile)

        return dbProfile
    }

    /** Анализ сообщения и вытаскивание дистанции */
    private fun analyzePostText(text: String, textHash: String, lastSumDistance: Long, lastPostNumber: Int, post: Post,
                                eventType: EventType): Boolean {
        val parserOut = MessageParser(text).run()
        val status: PostParserStatus
        val number: Int?
        val distance: Short?
        val newSumDistance: Long?

        post.text = text
        post.textHash = textHash
        if (parserOut != null) {
            // Проверка суммы
            distance = parserOut.distance!!
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
            logger.debug(" -- ${eventType.name} post after analyze: $post")

            // Комментарий статуса обработки поста
            val commentText = createCommentText(post, lastSumDistance, newSumDistance)
            addStatusComment(post.id, commentText)

            postSender.accept(eventType, PostDtoObject.create(post, vkApiService.getPostLink))
        }

        return parserOut != null
    }

    private fun createCommentText(post: Post, startSumDistance: Long, endSumDistance: Long?): String {
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
            PostParserStatus.ErrorSum.id -> "Ошибка при сложении, должно быть: $endSumDistance"
            PostParserStatus.ErrorParse.id -> "Ошибка в формате записи, пост не распознан"
            PostParserStatus.ErrorStartSum.id -> "Ошибка в стартовой сумме, должна быть: $startSumDistance"
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

        vkApiService.wallAddComment(postId.toInt(), commentText)
    }

    @Transactional
    fun updateNextPosts(updatePost: Post) {
        val startPost = if (updatePost.number != null) {
            updatePost
        } else {
            val pageable = PageRequest.of(0, 1, Sort(Sort.Direction.DESC, "date"))
            postRepo.findRunningPage(pageable, null, updatePost.date).getOrNull(0)
        }
        logger.debug("> Update next, start: $startPost")

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