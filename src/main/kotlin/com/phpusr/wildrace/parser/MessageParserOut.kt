package com.phpusr.wildrace.parser

/**
 * Выходные данные анализатора сообщений от поста
 */
class MessageParserOut(
        /** Начальная сумма расстояний пробежек */
        val startSumNumber: Long?,

        /** Дистанция пробежки */
        val distance: List<Short>?,

        /** Конечная сумма расстояний пробежек */
        val endSumNumber: Long?
)