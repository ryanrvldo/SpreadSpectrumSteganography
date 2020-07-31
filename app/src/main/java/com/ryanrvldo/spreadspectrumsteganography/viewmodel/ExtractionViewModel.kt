package com.ryanrvldo.spreadspectrumsteganography.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanrvldo.spreadspectrumsteganography.util.MWCGenerator
import com.ryanrvldo.spreadspectrumsteganography.util.SpreadHelper.deSpreadMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.math.BigInteger
import java.util.*
import kotlin.math.abs
import kotlin.system.measureTimeMillis


class ExtractionViewModel : ViewModel() {

    private val _initBytes = MutableLiveData<ByteArray>()
    val initBytes: LiveData<ByteArray>
        get() = _initBytes

    private val _generator = MutableLiveData<MWCGenerator>()
    val generator: LiveData<MWCGenerator>
        get() = _generator
    private var length: Int = 0

    private val _resultMessage = MutableLiveData<String>()
    val resultMessage: LiveData<String>
        get() = _resultMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun setInitBytes(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            _initBytes.postValue(inputStream.readBytes())
        }
    }

    fun setKey(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            val byteMessage: ByteArray = inputStream.readBytes()
            val builder = StringBuilder()
            for (msgByte in byteMessage) {
                builder.append(msgByte.toChar())
            }
            val tokenizer = StringTokenizer(builder.toString(), ",")
            val key = IntArray(5)
            for (i in 0..4) {
                key[i] = Integer.parseInt(tokenizer.nextToken())
            }
            val generator = MWCGenerator(
                BigInteger.valueOf(key[0].toLong()),
                BigInteger.valueOf(key[1].toLong()),
                BigInteger.valueOf(key[2].toLong()),
                BigInteger.valueOf(key[3].toLong())
            )
            length = key[4]
            _generator.postValue(generator)
        }
    }

    fun extractMessage(init: ByteArray, mwcGenerator: MWCGenerator) {
        viewModelScope.launch(Dispatchers.Default) {
            _isLoading.postValue(true)
            val runningTime = measureTimeMillis {
                val xn = mwcGenerator.generateRandomIndex(length)
                val builder = StringBuilder()
                for (i in 0 until length) {
                    if (abs(init[xn[i]].rem(2)) == 0) {
                        builder.append('0')
                    } else if (abs(init[xn[i]].rem(2)) == 1) {
                        builder.append('1')
                    }
                }
                val extractedMessage = deSpreadMessage(builder.toString(), mwcGenerator)
                _resultMessage.postValue(extractedMessage)
            }
            _isLoading.postValue(false)
            Log.d(TAG, "RunningTime: ${runningTime.toDouble() / 1_000} seconds")
        }
    }

    companion object {
        private const val TAG = "EXTRACTION"
    }
}