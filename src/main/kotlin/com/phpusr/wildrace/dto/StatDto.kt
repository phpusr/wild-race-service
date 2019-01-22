package com.phpusr.wildrace.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.consts.Consts
import com.phpusr.wildrace.domain.Profile
import com.phpusr.wildrace.domain.StatLog
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.enum.StatType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Класс для хранения статистики
 */
@JsonIgnoreProperties("type")
@JsonView(Views.StatDtoREST::class)
class StatDto {
    /** Начальная дистанция для отчетности */
    var startDistance: Long? = null

    /** Конечная дистанция для отчетности */
    var endDistance: Long? = null

    /** Начальная дата для отчетности */
    var startDate: Date? = null

    /** Конечная дата для отчетности */
    var endDate: Date? = null


    /** Кол-во дней бега - всего */
    var daysCountAll: Int = 0

    /** Кол-во дней бега - на отрезке */
    var daysCountInterval: Int = 0


    /** Километраж - общий  */
    var distanceAll: Long = 0

    /** Километраж - средний в день */
    val distancePerDayAvg: Float
        get() = if (daysCountAll == 0) 0f else (distanceAll / daysCountAll.toDouble()).toFloat()

    /** Километраж - средняя длина одной пробежки */
    val distancePerTrainingAvg: Float
        get() = if (trainingCountAll == 0) 0f else (distanceAll / trainingCountAll.toDouble()).toFloat()

    /** Километраж - максимум от 1-го человека */
    lateinit var distanceMaxOneMan: RunnerDto


    /** Тренировки - всего */
    var trainingCountAll: Int = 0

    /** Тренировки - среднее в день */
    val trainingCountPerDayAvg
        get() = if (daysCountAll == 0) 0f else (trainingCountAll / daysCountAll.toDouble()).toFloat()

    /** Тренировки - максимум от 1-го человека */
    lateinit var trainingMaxOneMan: RunnerDto


    /** Бегуны - всего отметилось */
    var runnersCountAll: Int = 0

    /** Бегуны - отметилось на отрезке */
    var runnersCountInterval: Int = 0

    /** Бегуны - новых на отрезке */
    lateinit var newRunners: List<Profile>

    /** Кол-во новых бегунов на отрезке */
    var countNewRunners: Int = 0

    /** Топ бегунов за все время */
    lateinit var topAllRunners: List<RunnerDto>

    /** Топ бегунов на отрезке */
    lateinit var topIntervalRunners: List<RunnerDto>

    val type: StatType
        get(): StatType {
            return if (startDistance != null && endDistance != null) StatType.Distance else StatType.Date
        }

    fun createStatLog(postId: Int): StatLog {
        val df = SimpleDateFormat(Consts.JSDateFormat)
        val startValue = if (type == StatType.Distance) startDistance!!.toString() else df.format(startDate)
        val endValue = if (type == StatType.Distance) endDistance!!.toString() else df.format(endDate)
        return StatLog(postId.toLong(), Date(), type, startValue, endValue)
    }

}