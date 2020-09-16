package com.ryanrvldo.spreadspectrumsteganography.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanrvldo.spreadspectrumsteganography.util.MWCGenerator
import com.ryanrvldo.spreadspectrumsteganography.util.SpreadHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class EmbeddingViewModel : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private val _initBytes = MutableLiveData<ByteArray>()
    val initBytes: LiveData<ByteArray>
        get() = _initBytes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun setMessage(bytes: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            val stringBuilder = StringBuilder()
            bytes.forEach {
                stringBuilder.append(it.toChar())
            }
            _message.postValue(stringBuilder.toString())
        }
    }

    fun setInitBytes(bytes: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            _initBytes.postValue(bytes)
        }
    }

    fun getRandomKey(bound: Long): MWCGenerator {
        return MWCGenerator(
            BigInteger.valueOf(Random.nextLong(100)),
            BigInteger.valueOf(Random.nextLong(bound)),
            BigInteger.valueOf(Random.nextLong(100)),
            BigInteger.valueOf(Random.nextLong(100))
        )
    }

    suspend fun embedMessage(msg: String, init: ByteArray, generator: MWCGenerator): ByteArray {
        val results = init.clone()
        withContext(Dispatchers.Default) {
            _isLoading.postValue(true)
            runningTime = measureTimeMillis {
                val binaryMsg = SpreadHelper.spreadMessage(msg, generator).toCharArray()
                val length = binaryMsg.size
                val xn = generator.generateRandomIndex(length)

                for (i in 0 until length) {
                    if (binaryMsg[i] == '1' && (abs(init[xn[i]].rem(2)) == 0)) {
                        results[xn[i]] = init[xn[i]].plus(1).toByte()
                    } else if (binaryMsg[i] == '0' && (abs(init[xn[i]].rem(2)) == 1)) {
                        results[xn[i]] = init[xn[i]].minus(1).toByte()
                    }
                }
                _isLoading.postValue(false)
            }.toDouble() / 1000
        }
        withContext(Dispatchers.Default) {
            calcStegoQuality(init, results)
        }
        return results
    }

    private fun calcStegoQuality(init: ByteArray, result: ByteArray) {
        val length = if (init.size == result.size) init.size else result.size
        var temp = 0.0
        for (i in 0 until length) {
            temp += abs(result[i] - init[i]).toDouble().pow(2)
        }
        MSE = temp / length
        PSNR = 10 * log10((256.0).pow(2) / MSE)
    }

    fun saveStegoObject(result: ByteArray, outputStream: OutputStream?) {
        viewModelScope.launch(Dispatchers.IO) {
            outputStream?.apply {
                write(result)
                close()
            }
        }
    }

    fun saveKey(generator: MWCGenerator, msgBinSize: Int, outputStream: OutputStream?) {
        viewModelScope.launch(Dispatchers.IO) {
            outputStream?.let {
                val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
                bufferedWriter.write("${generator.a},")
                bufferedWriter.write("${generator.b},")
                bufferedWriter.write("${generator.c0},")
                bufferedWriter.write("${generator.x0},")
                bufferedWriter.write("${msgBinSize},")
                bufferedWriter.flush()
                bufferedWriter.close()
            }
        }
    }

    companion object {
        var runningTime by Delegates.notNull<Double>()
        var MSE by Delegates.notNull<Double>()
        var PSNR by Delegates.notNull<Double>()
    }
}