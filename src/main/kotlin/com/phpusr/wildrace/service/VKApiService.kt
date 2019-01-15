package com.phpusr.wildrace.service

import com.phpusr.wildrace.consts.Consts
import org.springframework.stereotype.Service
import java.util.*

@Service
class VKApiService(
        private val configService: ConfigService,
        private val restService: RestService
) {

    private val Url = "https://api.vk.com/method"

    fun wallGet(offset: Long, count: Int, extended: Boolean): HashMap<*, *>? {
        val config = configService.get()
        val params = mapOf(
                "owner_id" to  config.groupId,
                "offset" to offset,
                "count" to count,
                "filter" to "all",
                "v" to Consts.VK_API_Version,
                "extended" to if (extended) 1 else 0,
                "access_token" to config.commentAccessToken
        )
        val response = restService.get("${Url}/wall.get", params)

        return response
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
        val response = restService.get("${Url}/wall.addComment", params)
    }

}