package com.phpusr.wildrace.domain

import com.phpusr.wildrace.enum.StatType
import org.hibernate.validator.constraints.Length
import org.springframework.data.repository.CrudRepository
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class StatLog(
    @field:Id
    val id: Long,

    val publishDate: Date,

    val statType: StatType,

    @field:Length(max = 100, message = "start_value_too_long")
    val startValue: String,

    @field:Length(max = 100, message = "end_value_too_long")
    val endValue: String

) {
    //TODO remove in Postgres
    private val version = 0L
    private val postId = -1L
}

interface StatLogRepo : CrudRepository<StatLog, Long> {
    fun findFirstByOrderByPublishDateDesc(): StatLog?
    fun findFirstByStatTypeOrderByPublishDateDesc(statType: StatType): StatLog?
}