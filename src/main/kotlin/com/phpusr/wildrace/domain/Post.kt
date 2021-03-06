package com.phpusr.wildrace.domain

import org.hibernate.validator.constraints.Length
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import java.util.*
import javax.persistence.*

/**
 * Класс для хранения записей со стены группы
 */
@Entity
@NamedEntityGraph(name = "Post.detail", attributeNodes = [NamedAttributeNode("from")])
class Post(
        @field:Id
        val id: Long,

        /** Статус обработки поста */
        @field:Column(name = "status")
        var statusId: Int,

        /** Автор записи */
        @field:ManyToOne(fetch = FetchType.LAZY)
        @field:JoinColumn(name = "from_id")
        val from: Profile,

        /** Дата и время публикации записи */
        val date: Date
) {
    //TODO remove in Postgres
    private val version: Long = 0L

    /** Порядковый номер */
    var number: Int? = null

    /** Текст записи */
    @field:Length(max = 10000, message = "text_too_long")
    var text: String = ""

    /** Hash текста (MD5) */
    @field:Length(max = 32, message = "text_hash_too_long")
    var textHash: String = ""

    val startSum: Long?
        get() {
            val sum = sumDistance
            val dst = distance
            return if (sum != null && dst != null) sum - dst else null
        }

    /** Дистанция пробежки */
    var distance: Short? = null

    /** Сумма дистанций пробежек */
    var sumDistance: Long? = null

    /** Причина редактирования */
    @field:Length(max = 255, message = "edit_reason_too_long")
    var editReason: String? = null

    /** Дата последнего редактирования */
    var lastUpdate: Date? = null

    override fun toString(): String {
        return "Post(id: ${id}, number: ${number}, text: \"${if (text.length > 50) text.substring(0, 50) + "..." else text}\")"
    }
}

interface PostRepo : PagingAndSortingRepository<Post, Long> {

    @EntityGraph(value = "Post.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("from Post p " +
            "where (:statusId is null OR p.statusId = :statusId) AND (:manualEditing is null OR p.lastUpdate is not null)")
    fun findAll(pageable: Pageable, @Param("statusId") statusId: Int?, @Param("manualEditing") manualEditing: Boolean?): Page<Post>

    @EntityGraph(value = "Post.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("from Post p where number is not null AND " +
            "(cast(:sDate as date) is null OR date >= :sDate) AND (cast(:eDate as date) is null OR date <= :eDate) AND " +
            "(:sDst is null OR p.sumDistance - distance >= :sDst) AND (:eDst is null OR p.sumDistance - distance < :eDst)"
    )
    fun findRunningPage(
            pageable: Pageable,
            @Param("sDate") startDate: Date? = null,
            @Param("eDate") endDate: Date? = null,
            @Param("sDst") startDistance: Long? = null,
            @Param("eDst") endDistance: Long? = null
    ): List<Post>

    @Query("select pr, count(p.id), sum(p.distance) as s " +
            "from Post p left join p.from pr " +
            "where p.number is not null AND " +
            "(cast(:sDate as date) is null OR p.date >= :sDate) AND (cast(:eDate as date) is null OR p.date <= :eDate) " +
            "group by pr order by s desc"
    )
    fun calcSumDistanceForRunners(
            @Param("sDate") startDate: Date? = null,
            @Param("eDate") endDate: Date? = null
    ): List<*>

}