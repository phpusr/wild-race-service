package com.phpusr.wildrace.domain.data

import org.hibernate.validator.constraints.Length
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
        val groupId: Long,

        /** Название группы в VK (в URL) */
        @field:Length(max = 100, message = "group_short_link_too_long")
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