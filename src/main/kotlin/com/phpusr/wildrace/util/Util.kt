package com.phpusr.wildrace.util

import java.math.BigInteger
import java.security.MessageDigest

object Util {
    /** Вычисляет MD5-хэш строки */
    fun MD5(text: String): String {
        val digest = MessageDigest.getInstance("MD5")
        return BigInteger(1,digest.digest(text.toByteArray())).toString(16).padStart(32, '0')
    }

    /**
     * Удаляет символы не поддерживаемые UTF-8
     * подробнее: http://stackoverflow.com/questions/13653712/java-sql-sqlexception-incorrect-string-value-xf0-x9f-x91-xbd-xf0-x9f
     */
    fun removeBadChars(s: String?): String? {
        if (s == null) {
            return null
        }

        val sb = StringBuilder()
        for (i in 0 until s.length) {
            if (Character.isHighSurrogate(s[i])) continue
            sb.append(s[i])
        }
        return sb.toString()
    }
}