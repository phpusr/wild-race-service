package com.phpusr.wildrace.domain.vk

import com.phpusr.wildrace.enum.PostParserStatus
import org.hibernate.validator.constraints.Length
import java.util.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Класс для хранения записей со стены группы
 */
class Post {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    /** Порядковый номер */
    var number: Int? = null

    /** Статус обработки поста */
    var status: PostParserStatus? = null

    /** Автор записи */
    var from: Profile? = null

    /** Дата и время публикации записи */
    var date: Date? = null

    /** Текст записи */
    @field:Length(max = 1000, message = "text_too_long")
    var text: String? = null

    /** Hash текста (MD5) */
    var textHash: String? = null

    /** Дистанция пробежки */
    var distance: Int? = null

    /** Сумма дистанций пробежек */
    var sumDistance: Int? = null

    /** Причина редактирования */
    @field:Length(max = 255, message = "edit_reason_too_long")
    var editReason: String? = null

    /** Дата последнего редактирования */
    var lastUpdate: Date? = null
}