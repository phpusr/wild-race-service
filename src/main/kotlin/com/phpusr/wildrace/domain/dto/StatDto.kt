package com.phpusr.wildrace.domain.dto

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.domain.vk.Profile
import java.util.*

/**
 * Класс для хранения статистики
 */
@JsonView(Views.StatDtoREST::class)
class StatDto {
    /** Начальная дистанция для отчетности */
    var startDistance: Int? = null

    /** Конечная дистанция для отчетности */
    var endDistance: Int? = null

    /** Начальная дата для отчетности */
    var startDate: Date? = null

    /** Конечная дата для отчетности */
    var endDate: Date? = null


    /** Кол-во дней бега - всего */
    var daysCountAll: Int = 0

    /** Кол-во дней бега - на отрезке */
    var daysCountInterval: Int = 0


    /** Километраж - общий  */
    var distanceAll: Int = 0

    /** Километраж - средний в день */
    val distancePerDayAvg: Float
        get() = if (daysCountAll == 0) 0F else distanceAll.toFloat() / daysCountAll

    /** Километраж - средняя длина одной пробежки */
    val distancePerTrainingAvg: Float
        get() = if (trainingCountAll == 0) 0F else distanceAll.toFloat() / trainingCountAll


    /** Тренировки - всего */
    var trainingCountAll: Int = 0

    /** Тренировки среднее в день */
    val trainingCountPerDayAvgFunction
        get() = if (daysCountAll == 0) 0 else trainingCountAll / daysCountAll

    /** Кол-во тренировок на каждого участника */
    lateinit var countTraining: List<Map<Profile, Int>>


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

}