package com.ryanrvldo.spreadspectrumsteganography.util

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MWCGeneratorTest {

    private lateinit var generator: MWCGenerator

    @Before
    fun setUp() {
        generator =
            MWCGenerator(2.toBigInteger(), 64.toBigInteger(), 3.toBigInteger(), 1.toBigInteger())
    }

    @Test
    fun generateRandomIndex() {
        val expectedValue = intArrayOf(
            5, 10, 20, 40, 16, 33, 2, 6, 12, 24, 48, 32, 1, 3, 7, 14,
            28, 56, 49, 35, 8, 17, 34, 4, 9, 18, 36, 11, 23, 46, 29, 59
        )
        val actualValue = generator.generateRandomIndex(32)
        assertNotNull(actualValue)
        assertEquals(expectedValue.size, actualValue.size)
        assertArrayEquals(expectedValue, actualValue)
    }

    @Test
    fun generatePseudoNoiseSignal() {
        val expectedValue = intArrayOf(
            5, 10, 20, 40, 80, 160, 65, 130, 5, 10, 20, 40, 80, 160, 65, 130,
            5, 10, 20, 40, 80, 160, 65, 130, 5, 10, 20, 40, 80, 160, 65, 130
        )
        val actualValue = generator.generatePseudoNoiseSignal(32)
        assertNotNull(actualValue)
        assertEquals(expectedValue.size, actualValue.size)
        assertArrayEquals(expectedValue, actualValue)
    }
}