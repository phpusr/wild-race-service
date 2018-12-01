package com.phpusr.wildrace.domain.data

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import org.hibernate.validator.constraints.Length
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Настройки приложения
 */
@Entity
class Config(
        @field:Id
        @field:GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        /** Синхронизация постов */
        val syncPosts: Boolean,

        /** Кол-во секунд между запусками синхронизации постов */
        val syncSeconds: Int,

        /** ID группы в VK */
        @field:JsonView(Views.ConfigREST::class)
        val groupId: Long,

        /** Название группы в VK (в URL) */
        @field:Length(max = 100, message = "group_short_link_too_long")
        @field:JsonView(Views.ConfigREST::class)
        val groupShortLink: String,

        /** Комментирование статуса обработки постов */
        val commenting: Boolean,

        /** Токен доступа для комментирования */
        @field:Length(max = 100, message = "comment_access_token_too_long")
        val commentAccessToken: String,

        /** Оставлять-ли комментарий от имени группы */
        val commentFromGroup: Boolean,

        /** Публиковать-ли статистику в ВК */
        val publishStat: Boolean
)

interface ConfigRepo : CrudRepository<Config, Long> {

    @Query("from Config where id = 1")
    fun get(): Config

}