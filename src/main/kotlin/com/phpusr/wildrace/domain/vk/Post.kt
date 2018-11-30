package com.phpusr.wildrace.domain.vk

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import org.hibernate.validator.constraints.Length
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import java.util.*
import javax.persistence.*

/**
 * Класс для хранения записей со стены группы
 */
@Entity
class Post {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:JsonView(Views.PostREST::class)
    var id: Long? = null

    /** Порядковый номер */
    var number: Int? = null

    /** Статус обработки поста */
    @field:Column(name = "status")
    var statusId: Int? = null

    /** Автор записи */
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "from_id")
    @field:JsonView(Views.PostREST::class)
    var from: Profile? = null

    /** Дата и время публикации записи */
    @field:JsonView(Views.PostREST::class)
    var date: Date? = null

    /** Текст записи */
    @field:Length(max = 1000, message = "text_too_long")
    @field:JsonView(Views.PostREST::class)
    var text: String? = null

    /** Hash текста (MD5) */
    var textHash: String? = null

    /** Дистанция пробежки */
    @field:JsonView(Views.PostREST::class)
    var distance: Int? = null

    /** Сумма дистанций пробежек */
    @field:JsonView(Views.PostREST::class)
    var sumDistance: Int? = null

    /** Причина редактирования */
    @field:Length(max = 255, message = "edit_reason_too_long")
    var editReason: String? = null

    /** Дата последнего редактирования */
    var lastUpdate: Date? = null
}

interface PostRepo : PagingAndSortingRepository<Post, Long> {
    @Query("from Post p " +
            "where (:statusId is null OR p.statusId = :statusId) AND (:manualEditing is null OR p.lastUpdate is not null)")
    fun findAll(pageable: Pageable, @Param("statusId") statusId: Int?, @Param("manualEditing") manualEditing: Boolean?): Page<Post>

    @Query("from Post " +
            "where (:status is null OR status = :status) AND (:manualEditing is null OR lastUpdate is not null)")
    fun count(@Param("status") status: Int?, @Param("manualEditing") manualEditing: Boolean?): Long

    @Query("from Post " +
            "where number is not null AND distance is not null AND sumDistance is not null " +
            "AND date = (select max(date) from Post)")
    fun findLastPost(): Post
}