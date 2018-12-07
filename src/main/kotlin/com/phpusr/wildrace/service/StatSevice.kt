package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.vk.PostRepo
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class StatService(private val postRepo: PostRepo) {

    fun getStartDate(startDistance: Int): Date? {
        return postRepo.findStartDateList(startDistance, PageRequest.of(0, 1)).firstOrNull()
    }

    fun getEndDate(endDistance: Int): Date? {
        return postRepo.findEndDateList(endDistance, PageRequest.of(0, 1)).firstOrNull()
    }

    fun calcStat(sDate: Date?, eDate: Date?, startDistance: Int?, endDistance: Int?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}