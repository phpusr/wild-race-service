package com.phpusr.wildrace.domain.vk

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.consts.Consts
import com.phpusr.wildrace.domain.Views
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range
import org.springframework.data.repository.CrudRepository
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

/**
 * Профиль в VK
 */
@Entity
class Profile {
    @field:Id
    @field:JsonView(Views.ProfileREST::class)
    var id: Long? = null

    /** Дата первой пробежки (дата присоединения) */
    var joinDate: Date? = null

    /** Дата последней синхронизации с VK */
    var lastSync: Date? = null

    @field:Length(max = 100, message = "first_name_too_long")
    @field:JsonView(Views.ProfileREST::class)
    var firstName: String? = null

    @field:Length(max = 100, message = "last_name_too_long")
    @field:JsonView(Views.ProfileREST::class)
    var lastName: String? = null

    /**
     * пол пользователя. Возможные значения:
     * 1 — женский;
     * 2 — мужской;
     * 0 — пол не указан.
     */
    @field:Range(min = 0, max = 2, message = "sex_wrong_value")
    var sex: Int? = null

    /**
     * Дата рождения. Возвращается в формате DD.MM.YYYY или DD.MM (если год рождения скрыт). Если дата рождения скрыта целиком, поле отсутствует в ответе
     */
    var birthDate: String? = null

    @field:Length(max = 100, message = "city_too_long")
    var city: String? = null

    @field:Length(max = 100, message = "country_too_long")
    var country: String? = null

    var hasPhoto: Boolean? = null

    /**
     * url квадратной фотографии пользователя, имеющей ширину 50 пикселей. В случае отсутствия у пользователя фотографии возвращается http://vk.com/images/camera_c.gif
     */
    @field:Length(max = 255, message = "photo_50_too_long")
    @field:JsonView(Views.ProfileREST::class)
    var photo_50: String? = null

    /**
     * url квадратной фотографии пользователя, имеющей ширину 100 пикселей. В случае отсутствия у пользователя фотографии возвращается http://vk.com/images/camera_b.gif
     */
    @field:Length(max = 255, message = "photo_100_too_long")
    var photo_100: String? = null

    /**
     * url фотографии пользователя, имеющей ширину 200 пикселей. В случае отсутствия у пользователя фотографии возвращается http://vk.com/images/camera_a.gif
     */
    @field:Length(max = 255, message = "photo_200_orig_too_long")
    var photo_200_orig: String? = null

    /**
     * url квадратной фотографии пользователя, имеющей ширину 200 пикселей. Если фотография была загружена давно, изображения с такими размерами может не быть, в этом случае ответ не будет содержать этого поля
     */
    @field:Length(max = 255, message = "photo_200_too_long")
    var photo_200: String? = null

    /**
     * url фотографии пользователя, имеющей ширину 400 пикселей. Если у пользователя отсутствует фотография такого размера, ответ не будет содержать этого поля
     */
    @field:Length(max = 255, message = "photo_400_orig_too_long")
    var photo_400_orig: String? = null

    /**
     * url квадратной фотографии пользователя с максимальной шириной. Может быть возвращена фотография, имеющая ширину как 200, так и 100 пикселей. В случае отсутствия у пользователя фотографии возвращается http://vk.com/images/camera_b.gif
     */
    @field:Length(max = 255, message = "photo_max_too_long")
    var photo_max: String? = null

    /**
     * url фотографии пользователя максимального размера. Может быть возвращена фотография, имеющая ширину как 400, так и 200 пикселей. В случае отсутствия у пользователя фотографии возвращается http://vk.com/images/camera_a.gif
     */
    @field:Length(max = 255, message = "photo_max_orig_too_long")
    var photo_max_orig: String? = null

    /**
     * Короткий адрес страницы. Возвращается строка, содержащая короткий адрес страницы (возвращается только сам поддомен, например, andrew). Если он не назначен, возвращается "id"+uid, например, id35828305
     */
    @field:Length(max = 100, message = "domain_too_long")
    var domain: String? = null

    val firstAndLastName
        get() = "${firstName} ${lastName}"

    @get:JsonView(Views.ProfileREST::class)
    val vkLink
        get() = "${Consts.VKLink}/id${id}"

}

interface ProfileRepo : CrudRepository<Profile, Long>