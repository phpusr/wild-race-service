package com.phpusr.wildrace.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RestService {

    private val restTemplate = RestTemplate()

    fun get(uri: String, params: Map<String, Any>): java.util.HashMap<*, *>? {
        val query = params.map{ "${it.key}={${it.key}}" }.joinToString(separator = "&")
        val responseEntity = restTemplate.getForEntity("${uri}?${query}", String::class.java, params)
        val map = ObjectMapper().readValue(responseEntity.body, HashMap::class.java)

        return map
    }

}