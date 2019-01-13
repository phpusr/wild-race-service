package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.data.ConfigRepo
import com.phpusr.wildrace.domain.data.TempDataRepo
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.PostRepo
import com.phpusr.wildrace.domain.vk.Profile
import com.phpusr.wildrace.domain.vk.ProfileRepo
import com.phpusr.wildrace.enum.PostParserStatus
import com.phpusr.wildrace.parser.MessageParser
import com.phpusr.wildrace.util.Util
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class SyncService(
        private val configRepo: ConfigRepo,
        private val postRepo: PostRepo,
        private val tempDataRepo: TempDataRepo,
        private val profileRepo: ProfileRepo,
        private val vkApiService: VKApiService
) {

    fun syncPosts() {
        if (!configRepo.get().syncPosts) {
            return
        }

        val DownloadCountPosts = 100
        var needSync = true
        while(needSync) {
            val countPosts = getCountPosts()
            val alreadyDownloadCount = postRepo.count()
            println(">> Download: ${alreadyDownloadCount}/${countPosts}")
            if (needSync) {
                syncBlockPosts(countPosts, alreadyDownloadCount, DownloadCountPosts)
                needSync = alreadyDownloadCount < countPosts
                Thread.sleep(1000)
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
        removeDeletedPosts(vkPosts)

        val lastDbPosts = postRepo.findRunningPage(PageRequest.of(0, 200, Sort(Sort.Direction.DESC, "date"))).toMutableList()
        val vkProfiles = response["profiles"] as List<*>

        vkPosts.forEach {
            val vkPost = it as Map<*, *>
            val postId = (vkPost["id"] as Int).toLong()
            val text = vkPost["text"] as String
            val textHash = Util.MD5(text)
            val dbPostOption = postRepo.findById(postId)
            val dbPost: Post
            var isUpdate = false

            if (dbPostOption.isPresent) {
                dbPost = dbPostOption.get()
                if (textHash == dbPost.textHash || dbPost.lastUpdate != null) {
                    return@forEach
                }

                isUpdate = true
                println(">> Post change: ${dbPost}")
            } else {
                val postDate = Date((vkPost["date"] as Int).toLong() * 1000)
                // Поиск или создание профиля пользователя
                val dbProfile = findOrCreateProfile(vkPost, dbProfiles, vkProfiles, postDate)
                dbPost = Post(postId, PostParserStatus.Success.id, dbProfile, postDate)
            }

            val parserOut = analyzePostText(text, textHash, dbPost, lastDbPosts)

            // Добавление нового поста в список последних постов и сортировка постов по времени
            if (!isUpdate && parserOut) {
                lastDbPosts.add(dbPost)
                lastDbPosts.sortBy { it.date.time * -1 }
            }
        }
    }

    /** Удаление из БД удаленных постов */
    private fun removeDeletedPosts(vkPosts: List<Any?>) {
        val startDate = Date()
        val endDate = Date()
        val pageable = PageRequest.of(0, 1000, Sort(Sort.Direction.DESC, "date"))
        val todayPosts = postRepo.findRunningPage(pageable, startDate, endDate)
        val deletedPosts = todayPosts.filter { post ->
            vkPosts.find {
                val vkPost = it as Map<*, *>
                vkPost["id"] as Long == post.id
            } != null
        }

        if (deletedPosts.isEmpty()) {
            return
        }

        println(">> Delete vkPosts: ${deletedPosts}")
        deletedPosts.forEach { it.distance = null }
        updateNextPosts(deletedPosts.last())
        deletedPosts.forEach { postRepo.delete(it) }
    }

    private fun updateNextPosts(updatePost: Post) {
        val startPost = if (updatePost.number != null && updatePost.distance != null && updatePost.sumDistance != null) {
            updatePost
        } else {
            val pageable = PageRequest.of(0, 1, Sort(Sort.Direction.DESC, "date"))
            postRepo.findRunningPage(pageable, null, updatePost.date).firstOrNull()
        }
        println("startPost: ${startPost}")

        if (startPost == null) {
            return
        }

        var currentNumber = startPost.number!!
        var currentSumDistance = startPost.sumDistance!!
        val pageable = PageRequest.of(0, 1000, Sort(Sort.Direction.ASC, "date"))
        postRepo.findRunningPage(pageable, startPost.date).forEach { post ->
            if (post.id == startPost.id) {
                return@forEach
            }

            val number = ++currentNumber
            val newSumDistance = currentSumDistance + post.distance!!

            // Обновление статуса
            val status: PostParserStatus
            val parserOut = MessageParser(post.text).run()
            if (parserOut != null) {
                if (parserOut.startSumNumber == currentSumDistance) {
                    if (parserOut.endSumNumber == newSumDistance) {
                        status = PostParserStatus.Success
                    } else {
                        status = PostParserStatus.ErrorSum
                    }
                } else {
                    status = PostParserStatus.ErrorStartSum
                }
            } else {
                status = PostParserStatus.ErrorParse
            }

            // Проверка: поменялось-ли выражение суммы в тексте
            if (post.number != number || post.sumDistance != newSumDistance || post.statusId != status.id) {
                post.number = number
                post.sumDistance = newSumDistance
                post.statusId = status.id

                // Комментарий статуса обработки поста
                val commentText = createCommentText(post, currentSumDistance, newSumDistance)
                addStatusComment(post.id, commentText)
            }

            currentSumDistance = newSumDistance
        }
    }

    private fun findOrCreateProfile(postMap: Map<*, *>, dbProfiles: MutableIterable<Profile>, vkProfiles: List<*>, postDate: Date): Profile {
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
            dbProfile = Profile()
            dbProfile.id = profileId
            dbProfile.joinDate = postDate
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
    private fun analyzePostText(text: String, textHash: String, post: Post, lastPosts: List<Post>): Boolean {
        val parserOut = MessageParser(text).run()
        val status: PostParserStatus
        val number: Int?
        val distance: Int?
        val lastSumDistance: Int?
        val newSumDistance: Int?

        post.text = Util.removeBadChars(text) ?: ""
        post.textHash = textHash
        if (parserOut != null) {
            val lastPost = lastPosts.find{ it.id != post.id && it.date <= post.date }
            lastSumDistance = lastPost?.sumDistance ?: 0
            val lastPostNumber = lastPost?.number ?: 0

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
            lastSumDistance = null
            newSumDistance = null
        }

        // Проверка: поменялось-ли выражение суммы в тексте
        if (post.number != number || post.distance != distance || post.sumDistance != newSumDistance || post.statusId != status.id) {
            post.number = number
            post.distance = distance
            post.sumDistance = newSumDistance
            post.statusId = status.id

            postRepo.save(post)
            println(">> Save post: ${post}")

            // Комментарий статуса обработки поста
            val commentText = createCommentText(post, lastSumDistance, newSumDistance)
            addStatusComment(post.id, commentText)
            updateNextPosts(post)
        }

        return parserOut != null
    }

    private fun createCommentText(post: Post, startSumNumber: Int?, endSumNumber: Int?): String {
        val commentText = StringBuilder()

        // Обращение
        if (post.statusId != PostParserStatus.Success.id) {
            commentText.append("[id${post.from.id}|${post.from.firstName}], ")
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
        if (!configRepo.get().commenting) {
            return
        }

        // Задержка перед добавлением комментария, чтобы не заблокировали пользователя
        Thread.sleep(300)

        vkApiService.wallAddComment(postId, commentText)
    }

    private fun updateLastSyncDate() {
        val tempData = tempDataRepo.get()
        tempDataRepo.save(tempData.copy(lastSyncDate = Date()))
    }

}