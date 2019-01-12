package com.phpusr.wildrace.parser

/**
 * Выходные данные анализатора сообщений от поста
 */
class MessageParserOut(
        /** Начальная сумма расстояний пробежек */
        val startSumNumber: Int?,

        /** Дистанция пробежки */
        val distance: List<Int>?,

        /** Конечная сумма расстояний пробежек */
        val endSumNumber: Int?
)