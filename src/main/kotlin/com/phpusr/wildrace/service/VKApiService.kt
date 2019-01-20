package com.phpusr.wildrace.service

import com.phpusr.wildrace.consts.Consts
import com.phpusr.wildrace.dto.vk.WallPost
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.groups.GroupFull
import com.vk.api.sdk.objects.users.UserXtrCounters
import com.vk.api.sdk.objects.wall.responses.GetResponse
import com.vk.api.sdk.queries.users.UserField
import org.springframework.stereotype.Service

@Service
class VKApiService(
        private val configService: ConfigService,
        private val restService: RestService
) {

    private val url = "https://api.vk.com/method"
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

    fun wallAddComment(postId: Long, text: String) {
        val config = configService.get()
        val params = mapOf(
                "owner_id" to  config.groupId,
                "post_id" to postId,
                "text" to text,
                "v" to Consts.VK_API_Version,
                "access_token" to config.commentAccessToken,
                "from_group" to if (config.commentFromGroup) 1 else 0
        )
        restService.get("$url/wall.addComment", params)
    }

    fun wallPost(message: String): WallPost {
        val config = configService.get()
        val params = mapOf(
                "owner_id" to  config.groupId,
                "message" to message,
                "signed" to 1,
                "v" to Consts.VK_API_Version,
                "access_token" to config.commentAccessToken,
                "from_group" to if (config.commentFromGroup) 1 else 0
        )
        return restService.post("$url/wall.post", params, WallPost::class)
    }

}