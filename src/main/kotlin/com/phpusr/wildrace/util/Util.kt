package com.phpusr.wildrace.util

import java.math.BigInteger
import java.security.MessageDigest

object Util {
    /** Вычисляет MD5-хэш строки */
    fun MD5(text: String): String {
        val digest = MessageDigest.getInstance("MD5")
        return BigInteger(1,digest.digest(text.toByteArray())).toString(16).padStart(32, '0')
    }
}