package com.phpusr.wildrace.util

import org.junit.Assert
import org.junit.Test

/**
 *
 */
class UtilTest {

    fun test(message: String, result: String) {
        Assert.assertEquals(Util.MD5(message), result.toLowerCase())
    }

    /**
     * src: https://passwordsgenerator.net/md5-hash-generator/
     */
    @Test
    fun MD5Test() {
        test("test", "098f6bcd4621d373cade4e832627b4f6")
        test("5 + 10 = 15", "B10D8CBFE98FC91ED3F2E184FACD677B")
    }

}