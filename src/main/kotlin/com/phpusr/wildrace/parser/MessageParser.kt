package com.phpusr.wildrace.parser

class MessageParser(private val message: String) {
    /** RexExp цифры */
    private val ExpDigit = "\\d".toRegex()
    /** Выражение разделитель */
    private val ExpSplit = " "
    /** Знак сложения */
    private val ExpPlus = "+"
    /** Знак равно */
    private val ExpEqual = "="

    /** Текущий анализируемый символ */
    private var currentSymbol = ""
    /** Индекс текущего анализируемого символа */
    private var currentIndex = -1

    /** Текущее число, собираемое посимвольно */
    private var currentNumber: String? = null
    /** Начальная сумма расстояний пробежек */
    private var startSumNumber: Long? = null
    /** Дистанции пробежек */
    private lateinit var distanceList: MutableList<Short>
    /** Конечная сумма расстояний пробежек */
    private var endSumNumber: Long? = null

    /** Закончилась-ли обработка выражения */
    private var endParsing = false

    private fun debug(message: String) {
        if (false) println(message)
    }

    fun run(): MessageParserOut? {
        debug(">> run")

        reset()

        var notEnd = nextSymbol()

        while(notEnd && !endParsing) {
            debug("start: '${currentSymbol}'")

            val findDigit = ExpDigit matches currentSymbol
            if (findDigit) {
                currentNumber = ""
                step1()
            } else {
                notEnd = nextSymbol()
            }
        }

        if (endParsing) {
            return MessageParserOut(startSumNumber, distanceList, endSumNumber)
        }

        return null
    }

    /** Сброс автомата */
    private fun reset() {
        debug("reset: '${currentSymbol}'")

        startSumNumber = null
        distanceList = mutableListOf()
        endSumNumber = null
    }

    /** Передает следующий символ из сообщения в переменную currentSymbol */
    private fun nextSymbol(): Boolean {
        debug(">> nextSymbol")

        if (currentIndex + 1 == message.length) {
            currentSymbol = ""
            return false
        }

        currentIndex++
        currentSymbol = message[currentIndex].toString()
        return true
    }

    /** Обработка числа */
    private fun step1() {
        debug("step1: '${currentSymbol}'")

        currentNumber += currentSymbol

        nextSymbol()
        if (ExpDigit matches currentSymbol) {
            step1()
        } else if (currentSymbol == ExpSplit) {
            step2()
        } else if (currentSymbol == ExpPlus) {
            step3()
        } else if (currentSymbol == ExpEqual) {
            step4()
        } else {
            reset()
        }
    }

    /** Обработка пробела после числа */
    private fun step2() {
        debug("step2: '${currentSymbol}'")

        nextSymbol()
        if (currentSymbol == ExpSplit) {
            step2()
        } else if (currentSymbol == ExpPlus) {
            step3()
        } else if (currentSymbol == ExpEqual) {
            step4()
        } else {
            reset()
        }
    }

    /** Обработка знака сложения */
    private fun step3() {
        debug("step3: '${currentSymbol}'")

        if (startSumNumber == null) {
            startSumNumber = currentNumber?.toLong()
        } else {
            distanceList.add(currentNumber!!.toShort())
        }
        currentNumber = ""

        nextSymbol()
        if (ExpDigit matches currentSymbol) {
            step1()
        } else if (currentSymbol == ExpSplit) {
            step2_2()
        } else {
            reset()
        }
    }

    /** Обработка пробела после плюса */
    private fun step2_2() {
        debug("step2_2: '${currentSymbol}'")

        nextSymbol()
        if (ExpDigit matches currentSymbol) {
            step1()
        } else if (currentSymbol == ExpSplit) {
            step2_2()
        } else {
            reset()
        }
    }

    /** Обработка знака равно */
    private fun step4() {
        debug("step4: '${currentSymbol}'")

        if (startSumNumber == null) {
            reset()
            return
        }

        distanceList.add(currentNumber!!.toShort())
        currentNumber = ""

        nextSymbol()
        if (ExpDigit matches currentSymbol) {
            step5()
        } else if (currentSymbol == ExpSplit) {
            step6()
        } else {
            reset()
        }
    }

    /** Обработка пробела после знака равно */
    private fun step5() {
        debug("step5: '${currentSymbol}'")

        currentNumber += currentSymbol

        nextSymbol()
        if (ExpDigit matches currentSymbol) {
            step5()
        } else {
            finish()
        }
    }

    /** Обработка числа после знака равно */
    private fun step6() {
        debug("step6: '${currentSymbol}'")

        nextSymbol()
        if (ExpDigit matches currentSymbol) {
            step5()
        } else if (currentSymbol == ExpSplit) {
            step6()
        } else {
            reset()
        }
    }

    /** Окончание обработки выражения */
    private fun finish() {
        debug("finish: '${currentNumber}'")

        endParsing = true
        endSumNumber = currentNumber!!.toLong()

        debug("startSumNumber: $startSumNumber")
        debug("distanceList: $distanceList")
        debug("endSumNumber: $endSumNumber")
    }

}