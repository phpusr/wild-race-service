package com.phpusr.wildrace.domain.vk

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.enum.PostParserStatus
import org.hibernate.validator.constraints.Length
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*
import javax.persistence.*

/**
 * Класс для хранения записей со стены группы
 */
@Entity
class Post {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    @field:JsonView(Views.Id::class)
    var id: Long? = null

    /** Порядковый номер */
    @field:JsonView(Views.FullPost::class)
    var number: Int? = null

    /** Статус обработки поста */
    @field:ManyToOne(fetch = FetchType.EAGER)
    @field:JoinColumn(name = "status")
    @field:JsonView(Views.FullPost::class)
    var status: PostParserStatus? = null

    /** Автор записи */
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "profile_id")
    @field:JsonView(Views.FullPost::class)
    var from: Profile? = null

    /** Дата и время публикации записи */
    @field:JsonView(Views.FullPost::class)
    var date: Date? = null

    /** Текст записи */
    @field:Length(max = 1000, message = "text_too_long")
    @field:JsonView(Views.FullPost::class)
    var text: String? = null

    /** Hash текста (MD5) */
    @field:JsonView(Views.FullPost::class)
    var textHash: String? = null

    /** Дистанция пробежки */
    @field:JsonView(Views.FullPost::class)
    var distance: Int? = null

    /** Сумма дистанций пробежек */
    @field:JsonView(Views.FullPost::class)
    var sumDistance: Int? = null

    /** Причина редактирования */
    @field:Length(max = 255, message = "edit_reason_too_long")
    @field:JsonView(Views.FullPost::class)
    var editReason: String? = null

    /** Дата последнего редактирования */
    @field:JsonView(Views.FullPost::class)
    var lastUpdate: Date? = null
}

interface PostRepo : PagingAndSortingRepository<Post, Long> {
    @Query("from Post p left join p.from" +
            "where (:status is null OR status = :status) AND (:manualEditing is null OR lastUpdate is not null)")
    fun findAll(pageable: Pageable, status: Int, manualEditing: Boolean): Page<Post>

    @Query("from Post p" +
            "where (:status is null OR status = :status) AND (:manualEditing is null OR lastUpdate is not null)")
    fun count(status: Int, manualEditing: Boolean): Page<Post>
}