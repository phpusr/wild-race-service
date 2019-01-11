package com.phpusr.wildrace.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.phpusr.wildrace.consts.Consts
import com.phpusr.wildrace.domain.data.ConfigRepo
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SyncService(private val configRepo: ConfigRepo) {

    fun syncPosts(): java.util.HashMap<*, *>? {
        val r = RestTemplate()
        val config = configRepo.get()
        val params = mapOf(
                "owner_id" to  config.groupId.toString(),
                "offset" to 0.toString(),
                "count" to 1.toString(),
                "filter" to "all",
                "v" to Consts.VK_API_Version,
                "access_token" to config.commentAccessToken
        )
        val query = params.map{ "${it.key}={${it.key}}" }.joinToString(separator = "&")
        val result = r.getForObject("https://api.vk.com/method/wall.get?${query}", String::class.java, params)
        println(result)

        val map = ObjectMapper().readValue(result, HashMap::class.java)

        return map
    }

}