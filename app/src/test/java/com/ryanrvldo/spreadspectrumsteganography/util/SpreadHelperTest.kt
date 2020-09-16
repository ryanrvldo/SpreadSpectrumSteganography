package com.ryanrvldo.spreadspectrumsteganography.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class SpreadHelperTest {

    private lateinit var generator: MWCGenerator

    @Before
    fun setUp() {
        generator =
            MWCGenerator(2.toBigInteger(), 64.toBigInteger(), 3.toBigInteger(), 1.toBigInteger())
    }

    @Test
    fun spreadMessage() {
        val expectedValue =
            "00001010000001010001010011011000010111110101000010110001100011010000101011111010000101000010011101011111010100001011111001110010"
        val actualValue = SpreadHelper.spreadMessage("Rian", generator)
        assertNotNull(actualValue)
        assertEquals(expectedValue.length, actualValue.length)
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun deSpreadMessage() {
        val expectedValue = "Rian"
        val actualValue = SpreadHelper.deSpreadMessage(
            "00001010000001010001010011011000010111110101000010110001100011010000101011111010000101000010011101011111010100001011111001110010",
            generator
        )
        assertNotNull(actualValue)
        assertEquals(expectedValue.length, actualValue.length)
        assertEquals(expectedValue, actualValue)
    }
}