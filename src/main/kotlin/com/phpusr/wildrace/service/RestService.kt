package com.phpusr.wildrace.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import kotlin.reflect.KClass

@Service
@Transactional(readOnly = true)
class RestService {

    private val restTemplate = RestTemplate()

    fun get(uri: String, params: Map<String, Any>) = get(uri, params, HashMap::class)

    fun <T : Any> get(uri: String, params: Map<String, Any>, responseClass: KClass<T>): T {
        val query = params.map{ "${it.key}={${it.key}}" }.joinToString(separator = "&")
        val responseEntity = restTemplate.getForEntity("$uri?$query", responseClass.java, params)

        return responseEntity.body ?: throw RuntimeException("Response body is null")
    }

    fun post(uri: String, params: Map<String, Any>) = post(uri, params, HashMap::class)

    fun <T : Any> post(uri: String, params: Map<String, Any>, responseClass: KClass<T>): T {
        val query = params.map{ "${it.key}={${it.key}}" }.joinToString(separator = "&")
        val responseEntity = restTemplate.postForEntity("$uri?$query", null, responseClass.java, params)

        return responseEntity.body ?: throw RuntimeException("Response body is null")
    }

}