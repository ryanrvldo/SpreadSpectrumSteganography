package com.ryanrvldo.spreadspectrumsteganography.util

import org.junit.Assert.assertEquals
import org.junit.Test

class BinaryConverterTest {

    @Test
    fun stringToBinary() {
        val actualBinary = BinaryConverter.stringToBinary("Rian")
        val expected = "01010010011010010110000101101110"
        assertEquals(expected.length, actualBinary.length)
        assertEquals(expected, actualBinary)
    }

    @Test
    fun binaryToString() {
        val actualString = BinaryConverter.binaryToString("01010010011010010110000101101110")
        val expected = "Rian"
        assertEquals(expected.length, actualString.length)
        assertEquals(expected, actualString)
    }
}