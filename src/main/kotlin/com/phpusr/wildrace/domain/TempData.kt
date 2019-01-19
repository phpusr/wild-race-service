package com.phpusr.wildrace.domain

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

/**
 * Данные которые периодически меняются
 */
@Entity
data class TempData(
        @field:Id
        val id: Long,

        val lastSyncDate: Date
)

interface TempDataRepo : CrudRepository<TempData, Long> {

    @Query("from TempData where id = 1")
    fun get(): TempData

}