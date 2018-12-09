package com.phpusr.wildrace.domain.dto

import com.phpusr.wildrace.domain.vk.Profile
import java.util.*

/**
 * Класс для хранения статистики
 */
class StatDto(
        /** Начальная дистанция для отчетности */
        val startDistance: Int?,

        /** Конечная дистанция для отчетности */
        val endDistance: Int?,

        /** Начальная дата для отчетности */
        val startDate: Date,

        /** Конечная дата для отчетности */
        val endDate: Date,

        /** Кол-во дней бега - всего */
        val daysCountAll: Int,

        /** Кол-во дней бега - на отрезке */
        val daysCountInterval: Int,

        /** Километраж - средний в день */
        val distancePerDayAvg: Float,

        /** Километраж - средняя длина одной пробежки */
        val distancePerTrainingAvg: Float,

        /** Тренировки - всего */
        val trainingCountAll: Long,

        /** Кол-во тренировок на каждого участника */
        val countTraining: List<Map<Profile, Int>>,

        /** Бегуны - всего отметилось */
        val runnersCountAll: Int,

        /** Бегуны - отметилось на отрезке */
        val runnersCountInterval: Int,

        /** Бегуны - новых на отрезке */
        val newRunners: List<Profile>,

        /** Топ бегунов за все время */
        val topAllRunners: List<Map<Profile, Int>>,

        /** Топ бегунов на отрезке */
        val topIntervalRunners: List<Map<Profile, Int>>
) {
        /** Тренировки среднее в день */
        val trainingCountPerDayAvgFunction
        get() = if (daysCountAll == 0) 0 else trainingCountAll / daysCountAll

        /** Кол-во новых бегунов на отрезке */
        val countNewRunners
        get() = newRunners.size
}