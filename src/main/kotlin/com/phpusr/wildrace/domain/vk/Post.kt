package com.phpusr.wildrace.domain.vk

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
data class Post(
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
    /** Порядковый номер */
    var number: Int? = null

    /** Текст записи */
    @field:Length(max = 1000, message = "text_too_long")
    var text: String = ""

    /** Hash текста (MD5) */
    @field:Length(max = 32, message = "text_hash_too_long")
    var textHash: String = ""

    val startSum: Int?
        get() {
            val sum = sumDistance
            val dst = distance
            return if (sum != null && dst != null) sum - dst else null
        }

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

interface PostRepo : PagingAndSortingRepository<Post, Long> {

    @EntityGraph(value = "Post.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("from Post p " +
            "where (:statusId is null OR p.statusId = :statusId) AND (:manualEditing is null OR p.lastUpdate is not null)")
    fun findAll(pageable: Pageable, @Param("statusId") statusId: Int?, @Param("manualEditing") manualEditing: Boolean?): Page<Post>

    @EntityGraph(value = "Post.detail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("from Post p where number is not null AND distance is not null AND p.sumDistance is not null AND " +
            "(:sDate is null OR date >= :sDate) AND (:eDate is null OR date <= :eDate) AND " +
            "(:sDst is null OR p.sumDistance - distance >= :sDst) AND (:eDst is null OR p.sumDistance - distance <= :eDst)"
    )
    fun findRunningPage(
            pageable: Pageable,
            @Param("sDate") startDate: Date? = null,
            @Param("eDate") endDate: Date? = null,
            @Param("sDst") startDistance: Int? = null,
            @Param("eDst") endDistance: Int? = null
    ): List<Post>

    @Query("select pr, count(p.id), sum(p.distance) as s " +
            "from Post p left join p.from pr " +
            "where p.number is not null AND p.distance is not null AND p.sumDistance is not null AND " +
            "(:sDate is null OR date >= :sDate) AND (:eDate is null OR date <= :eDate) " +
            "group by pr order by s desc"
    )
    fun calcSumDistanceForRunners(
            @Param("sDate") startDate: Date? = null,
            @Param("eDate") endDate: Date? = null
    ): List<*>

}