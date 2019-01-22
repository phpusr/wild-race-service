package com.phpusr.wildrace.parser

import org.junit.Assert
import org.junit.Test

class MessageParserTest {

    private fun assertNullResult(message: String) {
        Assert.assertNull(MessageParser(message).run())
    }

    private fun assertResult(message: String, startSumNumber: Long, distanceList: List<Short>, distance: Short, endSumNumber: Long) {
        val result = MessageParser(message).run()

        Assert.assertEquals(startSumNumber, result!!.startSumNumber)
        Assert.assertEquals(distanceList, result.distanceList)
        Assert.assertEquals(distance, result.distance)
        Assert.assertEquals(endSumNumber, result.endSumNumber)
    }

    @Test
    fun test1() {
        assertNullResult("321 + 12.8 = 500")
        assertResult("321.2 + 12 = 500.12", 2, listOf(12), 12, 500)
    }

    @Test
    fun test2() {
        assertResult("2345+34=15", 2345, listOf(34), 34, 15)
        assertResult("2345 +34=15", 2345, listOf(34), 34, 15)
        assertResult("2345 + 34=15", 2345, listOf(34), 34, 15)
        assertResult("2345 + 34 =15", 2345, listOf(34), 34, 15)
        assertResult("2345 + 34 = 15", 2345, listOf(34), 34, 15)
        assertResult("2345 + 34+200 = 15", 2345, listOf(34, 200), 234, 15)
        assertNullResult("2345 + 34+200k = 15")
    }

    @Test
    fun test3() {
        assertResult("5145+8=5153\n#дикийзабег", 5145, listOf(8), 8, 5153)
    }

    @Test
    fun test4() {
        assertResult("5127+6+12=5145", 5127, listOf(6, 12), 18, 5145)
    }

    @Test
    fun test5() {
        assertResult("5106 + 6 + 15 = 5127", 5106, listOf(6, 15), 21, 5127)
    }

    @Test
    fun test6() {
        assertResult("5091+4=5095 км\n\n#дикийзабег", 5091, listOf(4), 4, 5095)
    }

    @Test
    fun test7() {
        assertResult("5080+6=5086\n" +
                "Друзья, кто с Уфы заходите на огонёк в следующее воскресенье! 😉😊\n" +
                "Сегодня отлично пробежались! 👍\n" +
                "Правда трекер опять заглючило, в этот раз не в мою пользу 😂😁 3 км/ч\n" +
                "#клуббегаСпарта #клуббегаСпартаУфа #Уфа", 5080, listOf(6), 6, 5086)
    }

    @Test
    fun test8() {
        assertNullResult("СТАТИСТИКА\n" +
                "Ура! Несмотря на холода и снег 5000 км позади! Мы - молодцы!!\n" +
                "Из новичков в этот раз все приветственные лавры получает Яна Ишмаева - Стерлитамак! \n\\n" +
                "Наши итоги в цифрах: \n" +
                "1. Количество дней бега:\n" +
                "- всего - 149 дн.\n" +
                "- отрезок 4000-5000 - 42 дн.\n" +
                "2. Километраж:\n" +
                "- средний в день - 33,5 км/д\n" +
                "- максимум от одного человека - 832 км.\n" +
                "3. Тренировки: \n" +
                "- всего - 727 тр.\n" +
                "- среднее в день - 4,8 тр./д\n" +
                "- максимум от одного человека - 76 тр.\n" +
                "4. Бегуны:\n" +
                "- всего отметилось - 59 чел.\n" +
                "- отметилось на 4000-5000 - 21 чел.\n" +
                "- новых на отрезке 4000-5000 - 1 чел. \n\n" +
                "Пост со статистикой на 4000 км - http://vk.cc/4HAk6E \n" +
                "Следующий отчет на 6000 км.\n" +
                "Всем отличного бега!")
    }

    @Test(expected = NumberFormatException::class)
    fun test9() {
        assertResult("0 + 32767 = 32767", 0, listOf(32767), 32767, 32767)
        MessageParser("0 + 32768 = 32768").run()
    }

    @Test
    fun test10() {
        assertResult("999999900 + 100 = 1000000000", 999_999_900, listOf(100), 100, 1_000_000_000)
    }

}