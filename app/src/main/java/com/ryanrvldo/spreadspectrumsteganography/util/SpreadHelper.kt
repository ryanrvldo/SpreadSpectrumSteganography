package com.ryanrvldo.spreadspectrumsteganography.util

import android.util.Log

object SpreadHelper {

    fun spreadMessage(message: String, mwcGenerator: MWCGenerator): String {
        val binMessage = BinaryConverter.stringToBinary(message)
        val builder = StringBuilder()
        for (c in binMessage) {
            builder.append(c.toString().repeat(4))
        }
        val spreadBinMessageList = mutableListOf<String>()
        for (i in builder.indices step 8) {
            spreadBinMessageList.add(builder.substring(i until i + 8))
        }

        val pseudoNoiseSignal = mwcGenerator.generatePseudoNoiseSignal(spreadBinMessageList.size)
        val modulationResult = spreadBinMessageList.mapIndexed { index, value ->
            value.toInt(2).xor(pseudoNoiseSignal[index])
        }

        val embedMsg = StringBuilder()
        modulationResult.forEach {
            embedMsg.append(String.format("%08d", Integer.parseInt(it.toString(2))))
        }
        return embedMsg.toString()
    }

    fun deSpreadMessage(modulatedStr: String, mwcGenerator: MWCGenerator): String {
        val modulatedStrList = mutableListOf<String>()
        for (i in modulatedStr.indices step 8) {
            modulatedStrList.add(modulatedStr.substring(i, i + 8))
        }
        val pseudoNoiseSignal = mwcGenerator.generatePseudoNoiseSignal(modulatedStrList.size)
        val demodulationResult = modulatedStrList.mapIndexed { index, value ->
            String.format(
                "%08d",
                Integer.parseInt(value.toInt(2).xor(pseudoNoiseSignal[index]).toString(2))
            )
        }
        val resultMessage = StringBuilder()
        for (i in demodulationResult.indices) {
            resultMessage.append(demodulationResult[i].substring(3, 5))
        }
        Log.d("deSpreadMessage", resultMessage.toString())
        return BinaryConverter.binaryToString(resultMessage.toString())
    }
}