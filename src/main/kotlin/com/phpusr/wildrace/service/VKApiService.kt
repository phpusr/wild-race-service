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

    val authorizeUrl: String
        get() {
            //TODO change for production
            val redirectUri = if (true) "https://oauth.vk.com/blank.html" else "http://localhost:8080/authorize"
            val params = mapOf(
                    "client_id" to Consts.VKAppId,
                    "display" to "page",
                    "redirect_uri" to redirectUri,
                    "scope" to "wall,offline",
                    "response_type" to "token",
                    "v" to client.version
            )
            val paramsString = params.toList().joinToString("&") { "${it.first}=${it.second}" }
            return "${client.oAuthEndpoint}authorize?$paramsString"
        }

    val getPostLink = { id: Long ->
        val config = configService.get()
        "${Consts.VKLink}/club${config.groupId}?w=wall${config.groupIdNegative}_${id}"
    }

    /**
     * Возвращает посты со стены
     * @param offset смещение относительно последнего поста
     * @param count кол-во постов для скачивания
     */
    fun wallGet(offset: Int, count: Int): GetResponse {
        val config = configService.get()
        return client.wall()
                .get(user)
                .ownerId(config.groupIdNegative)
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
                .ownerId(config.groupIdNegative)
                .message(message)
                .fromGroup(if (config.commentFromGroup) config.groupId.toInt() else 0)
                .execute()!!
    }

    fun wallPost(message: String): PostResponse {
        val config = configService.get()
        return client.wall().post(user)
                .ownerId(config.groupIdNegative)
                .message(message)
                .signed(true)
                .fromGroup(config.commentFromGroup)
                .execute()!!
    }

}