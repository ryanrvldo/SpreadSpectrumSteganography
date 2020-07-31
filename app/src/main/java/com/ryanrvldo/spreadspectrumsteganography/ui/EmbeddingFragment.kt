package com.ryanrvldo.spreadspectrumsteganography.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ryanrvldo.spreadspectrumsteganography.R
import com.ryanrvldo.spreadspectrumsteganography.databinding.FragmentEmbeddingBinding
import com.ryanrvldo.spreadspectrumsteganography.util.CustomDialog
import com.ryanrvldo.spreadspectrumsteganography.util.MWCGenerator
import com.ryanrvldo.spreadspectrumsteganography.viewmodel.EmbeddingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

class EmbeddingFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentEmbeddingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmbeddingViewModel by viewModels()

    private lateinit var fileName: String
    private lateinit var initBytes: ByteArray
    private lateinit var message: String

    private lateinit var customDialog: CustomDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmbeddingBinding.inflate(layoutInflater, container, false)
        customDialog = CustomDialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnChooseMessage.setOnClickListener(this)
        binding.btnChooseAudio.setOnClickListener(this)
        binding.btnRandomKey.setOnClickListener(this)
        binding.btnProcess.setOnClickListener(this)

        viewModel.message.observe(viewLifecycleOwner, Observer { value ->
            value?.let { message ->
                binding.editTxtMessage.setText(message)
                this.message = message
            }
        })

        viewModel.initBytes.observe(viewLifecycleOwner, Observer { value ->
            value?.let { bytes ->
                this.initBytes = bytes
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { value ->
            value?.let { status ->
                if (status) {
                    customDialog.showLoadingDialog()
                } else {
                    customDialog.closeLoadingDialog()
                }
            }
        })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_choose_message -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    getMessageFile.launch("text/*")
                } else {
                    getPermissions.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            R.id.btn_choose_audio -> {
                if (!binding.editTxtMessage.text.isNullOrEmpty()) {
                    getAudioFile.launch("audio/*")
                    if (!this::message.isInitialized) {
                        message = binding.editTxtMessage.text.toString()
                    }
                } else {
                    toast("Please input your message first.")
                }
            }
            R.id.btn_random_key -> {
                if (!this::initBytes.isInitialized) {
                    toast("Please initialized your cover object first.")
                    return
                }
                randomKey()
            }
            R.id.btn_process -> {
                when {
                    !this::message.isInitialized && !this::initBytes.isInitialized ->
                        toast("Please input or initialized your cover object and message first.")
                    isValidKey() -> {
                        embedMessage()
                    }
                    else -> toast("Please input your key first.")
                }
            }
        }
    }

    private fun embedMessage() {
        lifecycleScope.launch(Dispatchers.Main) {
            val keys = getKeys()
            val resultBytes =
                withContext(Dispatchers.Default) {
                    viewModel.embedMessage(message, initBytes, keys)
                }
            saveStegoObject(resultBytes)
            saveKey(getKeys())
            withContext(Dispatchers.Main) {
                customDialog.showSuccessDialog()
            }
        }
    }

    private fun randomKey() {
        val generator = viewModel.getRandomKey(initBytes.size.toLong())
        binding.editTxtAKey.setText(String.format(generator.a.toString()))
        binding.editTxtBKey.setText(String.format(generator.b.toString()))
        binding.editTxtC0Key.setText(String.format(generator.c0.toString()))
        binding.editTxtX0Key.setText(String.format(generator.x0.toString()))
    }

    private fun isValidKey(): Boolean {
        return !binding.editTxtAKey.text.isNullOrEmpty() &&
                !binding.editTxtBKey.text.isNullOrEmpty() &&
                !binding.editTxtC0Key.text.isNullOrEmpty() &&
                !binding.editTxtX0Key.text.isNullOrEmpty()
    }

    private fun getKeys(): MWCGenerator {
        return MWCGenerator(
            binding.editTxtAKey.text.toString().toBigInteger(),
            binding.editTxtBKey.text.toString().toBigInteger(),
            binding.editTxtC0Key.text.toString().toBigInteger(),
            binding.editTxtX0Key.text.toString().toBigInteger()
        )
    }

    private fun saveStegoObject(resultBytes: ByteArray) {
        lifecycleScope.launch(Dispatchers.IO) {
            val file = File(
                requireContext().getExternalFilesDir(null),
                "${fileName.substringBeforeLast(".")}[1].${fileName.substringAfterLast(".")}"
            )
            val outputStream = FileOutputStream(file)
            outputStream.write(resultBytes)
            outputStream.close()

            withContext(Dispatchers.Main) {
                toast("Process success.\nStego Object and Stego Key is saved in${file.absolutePath}")
            }
        }
    }

    private fun saveKey(generator: MWCGenerator) {
        lifecycleScope.launch(Dispatchers.IO) {
            val file = File(
                requireContext().getExternalFilesDir(null),
                "${fileName.substringBeforeLast(".")}.key"
            )
            val bufferedWriter = BufferedWriter(FileWriter(file))
            bufferedWriter.write("${generator.a},")
            bufferedWriter.write("${generator.b},")
            bufferedWriter.write("${generator.c0},")
            bufferedWriter.write("${generator.x0},")
            bufferedWriter.write("${message.toCharArray().size * 8 * 4},")
            bufferedWriter.flush()
            bufferedWriter.close()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    private val getMessageFile =
        registerForActivityResult(GetContent()) { result ->
            result?.let { uri ->
                requireContext().contentResolver.openInputStream(uri)?.let { inputStream ->
                    viewModel.setMessage(inputStream)
                }
            }
        }

    private val getAudioFile =
        registerForActivityResult(GetContent()) { result ->
            result?.let { uri ->
                requireContext().contentResolver.openInputStream(uri)?.let {
                    viewModel.setInitBytes(it)
                }
                uri.path?.let { path ->
                    fileName = path.substringAfterLast("/")
                    binding.editTextFilePath.setText(fileName)
                }
            }
        }

    private val getPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getMessageFile.launch("text/*")
            } else {
                toast("Permission denied, please allow permission first.")
            }
        }
}