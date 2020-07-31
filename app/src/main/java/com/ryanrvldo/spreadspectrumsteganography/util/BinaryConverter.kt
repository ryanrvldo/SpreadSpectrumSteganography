package com.ryanrvldo.spreadspectrumsteganography.util

import java.lang.StringBuilder

object BinaryConverter {

    fun stringToBinary(str: String): String {
        val strBuilder = StringBuilder()
        for (char in str.toCharArray()) {
            val charBinary = char.toInt().toString(2)
            strBuilder.append(String.format("%08d", Integer.parseInt(charBinary)))
        }
        return strBuilder.toString()
    }

    fun binaryToString(binary: String): String {
        val chars = CharArray(binary.length / 8)
        for (i in binary.indices step 8) {
            val str = binary.substring(i, i + 8)
            val num = Integer.parseInt(str, 2)
            chars[i/8] = num.toChar()
        }
        return String(chars)
    }
}