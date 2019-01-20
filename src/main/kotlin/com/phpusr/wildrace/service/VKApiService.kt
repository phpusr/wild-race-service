package com.phpusr.wildrace.service

import com.phpusr.wildrace.consts.Consts
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.groups.GroupFull
import com.vk.api.sdk.objects.users.UserXtrCounters
import com.vk.api.sdk.objects.wall.responses.CreateCommentResponse
import com.vk.api.sdk.objects.wall.responses.GetResponse
import com.vk.api.sdk.objects.wall.responses.PostResponse
import com.vk.api.sdk.queries.users.UserField
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class VKApiService(private val configService: ConfigService) {

    private val client = VkApiClient(HttpTransportClient.getInstance())
    private val user: UserActor
        get() = UserActor(Consts.VKAppId, configService.get().commentAccessToken)

    /**
     * Возвращает посты со стены
     * @param offset смещение относительно последнего поста
     * @param count кол-во постов для скачивания
     */
    fun wallGet(offset: Int, count: Int): GetResponse {
        val config = configService.get()
        return client.wall()
                .get(user)
                .ownerId(config.groupId.toInt())
                .offset(offset)
                .count(count)
                .execute()!!
    }

    fun usersGetById(userId: Int): UserXtrCounters? {
        return client.users()
                .get(user)
                .userIds(userId.toString())
                .fields(UserField.SEX, UserField.PHOTO_50, UserField.PHOTO_100)
                .execute().first()
    }

    fun groupsGetById(groupId: Int): GroupFull? {
        return client.groups()
                .getById(user)
                .groupId(groupId.toString())
                .execute().first()
    }

    fun wallAddComment(postId: Int, message: String): CreateCommentResponse? {
        val config = configService.get()
        return client.wall().createComment(user, postId)
                .ownerId(config.groupId.toInt())
                .message(message)
                .fromGroup(if (config.commentFromGroup) 1 else 0)
                .execute()!!
    }

    fun wallPost(message: String): PostResponse {
        val config = configService.get()
        return client.wall().post(user)
                .ownerId(config.groupId.toInt())
                .message(message)
                .signed(true)
                .fromGroup(config.commentFromGroup)
                .execute()!!
    }

}