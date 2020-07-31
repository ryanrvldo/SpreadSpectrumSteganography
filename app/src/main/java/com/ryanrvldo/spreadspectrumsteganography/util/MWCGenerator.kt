package com.ryanrvldo.spreadspectrumsteganography.util

import java.math.BigInteger

class MWCGenerator(
    val a: BigInteger,
    val b: BigInteger,
    val c0: BigInteger,
    val x0: BigInteger
) {

    fun generateRandomIndex(length: Int): IntArray {
        val xnValues = IntArray(length)
        val tempXN = HashMap<BigInteger, Boolean>()

        var xn1 = x0
        var cn1 = c0
        for (i in 0 until length) {
            val temp = xn1.multiply(a).add(cn1)
            xn1 = temp.mod(b)
            cn1 = temp / b

            while (tempXN.containsKey(xn1)) {
                xn1 += BigInteger.ONE
                if (xn1 == b) xn1 = BigInteger.ZERO
            }
            tempXN[xn1] = true
            xnValues[i] = xn1.toInt()
        }
        return xnValues
    }

    fun generatePseudoNoiseSignal(length: Int): IntArray {
        val xnValues = IntArray(length)
        var xn1 = x0.toInt()
        var cn1 = 3
        val a = 2
        val b = 256
        for (i in 0 until length) {
            xn1 = (a * xn1 + cn1) % b
            cn1 = Math.floorDiv((a * xn1 + cn1), b)
            if (xn1 > 255) xn1 = 0
            xnValues[i] = xn1
        }
        return xnValues
    }
}