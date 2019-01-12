package com.phpusr.wildrace.service

import com.phpusr.wildrace.consts.Consts
import com.phpusr.wildrace.domain.data.ConfigRepo
import org.springframework.stereotype.Service
import java.util.*

@Service
class VKApiService(
        private val configRepo: ConfigRepo,
        private val restService: RestService
) {

    fun wallGet(offset: Long, count: Int, extended: Boolean): HashMap<*, *>? {
        val config = configRepo.get()
        val params = mapOf(
                "owner_id" to  config.groupId,
                "offset" to offset,
                "count" to count,
                "filter" to "all",
                "v" to Consts.VK_API_Version,
                "extended" to if (extended) 1 else 0,
                "access_token" to config.commentAccessToken
        )
        val response = restService.get("https://api.vk.com/method/wall.get", params)

        return response
    }

}