package com.ryanrvldo.spreadspectrumsteganography.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ryanrvldo.spreadspectrumsteganography.R
import com.ryanrvldo.spreadspectrumsteganography.util.CustomDialog
import com.ryanrvldo.spreadspectrumsteganography.util.MWCGenerator
import com.ryanrvldo.spreadspectrumsteganography.viewmodel.EmbeddingViewModel
import kotlinx.android.synthetic.main.fragment_embedding.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmbeddingFragment : Fragment(),
    View.OnClickListener {

    private val viewModel: EmbeddingViewModel by viewModels()

    private lateinit var fileName: String
    private lateinit var message: String
    private lateinit var initBytes: ByteArray
    private lateinit var resultBytes: ByteArray

    private lateinit var customDialog: CustomDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        customDialog = CustomDialog(requireContext())
        return layoutInflater.inflate(R.layout.fragment_embedding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_choose_message.setOnClickListener(this)
        btn_choose_audio.setOnClickListener(this)
        btn_random_key.setOnClickListener(this)
        btn_embed.setOnClickListener(this)

        viewModel.message.observe(viewLifecycleOwner, Observer { value ->
            value?.let { message ->
                et_message.setText(message)
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
            R.id.btn_choose_message -> selectMessageResultLauncher.launch("text/*")
            R.id.btn_choose_audio -> {
                if (!et_message.text.isNullOrEmpty()) {
                    selectAudioResultLauncher.launch("audio/*")
                    if (!this::message.isInitialized) {
                        message = et_message.text.toString()
                    }
                } else {
                    toast(getString(R.string.please_input_message))
                }
            }
            R.id.btn_random_key -> {
                if (!this::initBytes.isInitialized) {
                    toast(getString(R.string.please_init_cover))
                    return
                }
                randomKey()
            }
            R.id.btn_embed -> {
                when {
                    !this::message.isInitialized && !this::initBytes.isInitialized -> {
                        toast(getString(R.string.please_init_cover_message))
                    }
                    !this::initBytes.isInitialized -> toast(getString(R.string.please_init_cover))
                    !isValidKey() -> toast(getString(R.string.please_input_random_key))
                    et_message.text.isNullOrEmpty() ||
                            et_message.text.isNullOrBlank() -> {
                        toast(getString(R.string.please_input_message))
                    }
                    else -> {
                        message = et_message.text.toString()
                        embedMessage()
                    }
                }
            }
        }
    }

    private fun embedMessage() {
        lifecycleScope.launch(Dispatchers.Default) {
            resultBytes = viewModel.embedMessage(message, initBytes, getKeys())
            val name = fileName.substringBeforeLast(".")
            val ext = fileName.substringAfterLast(".")
            withContext(Dispatchers.Main) {
                saveAudioResultLauncher.launch("$name[1].$ext")
                saveKeyResultLauncher.launch("$name.key")
            }
        }
    }

    private fun randomKey() {
        val generator = viewModel.getRandomKey(initBytes.size.toLong())
        et_key_a.setText(String.format(generator.a.toString()))
        et_key_b.setText(String.format(generator.b.toString()))
        et_key_c0.setText(String.format(generator.c0.toString()))
        et_key_x0.setText(String.format(generator.x0.toString()))
    }

    private fun isValidKey(): Boolean {
        return !et_key_a.text.isNullOrEmpty() &&
                !et_key_b.text.isNullOrEmpty() &&
                !et_key_c0.text.isNullOrEmpty() &&
                !et_key_x0.text.isNullOrEmpty()
    }

    private fun getKeys(): MWCGenerator {
        return MWCGenerator(
            et_key_a.text.toString().toBigInteger(),
            et_key_b.text.toString().toBigInteger(),
            et_key_c0.text.toString().toBigInteger(),
            et_key_x0.text.toString().toBigInteger()
        )
    }

    private val selectMessageResultLauncher =
        registerForActivityResult(GetContent()) { result ->
            result?.let { uri ->
                requireContext().contentResolver.openInputStream(uri)?.let { inputStream ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.setMessage(inputStream.readBytes())
                    }
                }
            }
        }

    private val selectAudioResultLauncher =
        registerForActivityResult(GetContent()) { result ->
            result?.let { uri ->
                requireContext().contentResolver.openInputStream(uri)?.let {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.setInitBytes(it.readBytes())
                    }
                }
                uri.path?.let { path ->
                    fileName = path.substringAfterLast("/")
                    et_file_path.setText(fileName)
                }
            }
        }

    private val saveAudioResultLauncher =
        registerForActivityResult(CreateDocument()) { uri ->
            uri?.also {
                viewModel.saveStegoObject(
                    resultBytes,
                    requireContext().contentResolver.openOutputStream(it)
                )
            }
        }

    private val saveKeyResultLauncher =
        registerForActivityResult(CreateDocument()) { uri ->
            uri?.also {
                viewModel.saveKey(
                    getKeys(),
                    message.toCharArray().size * 32,
                    requireContext().contentResolver.openOutputStream(it)
                )
                customDialog.showSuccessDialog(
                    "The result of embedding process:\n" +
                            "Running Time: ${EmbeddingViewModel.runningTime} seconds.\n" +
                            "MSE: ${String.format("%.6f", EmbeddingViewModel.MSE)}\n" +
                            "PSNR: ${String.format("%.6f dB.", EmbeddingViewModel.PSNR)}"
                )
            }
        }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}