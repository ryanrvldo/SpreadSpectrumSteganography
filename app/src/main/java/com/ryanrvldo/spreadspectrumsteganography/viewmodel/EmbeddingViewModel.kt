package com.ryanrvldo.spreadspectrumsteganography.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanrvldo.spreadspectrumsteganography.util.MWCGenerator
import com.ryanrvldo.spreadspectrumsteganography.util.SpreadHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.math.BigInteger
import kotlin.math.abs
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

    fun setMessage(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            val bytesMessage = inputStream.readBytes()
            val stringBuilder = StringBuilder()
            bytesMessage.forEach {
                stringBuilder.append(it.toChar())
            }
            _message.postValue(stringBuilder.toString())
        }
    }

    fun setInitBytes(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            _initBytes.postValue(inputStream.readBytes())
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
            val runningTime = measureTimeMillis {
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
            }
            Log.d(TAG, "RunningTime: ${runningTime.toDouble() / 1_000} seconds")
        }
        return results
    }

    companion object {
        private const val TAG = "EMBEDDING"
    }
}