package com.ryanrvldo.spreadspectrumsteganography.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ryanrvldo.spreadspectrumsteganography.R
import com.ryanrvldo.spreadspectrumsteganography.util.CustomDialog
import com.ryanrvldo.spreadspectrumsteganography.util.MWCGenerator
import com.ryanrvldo.spreadspectrumsteganography.viewmodel.ExtractionViewModel
import kotlinx.android.synthetic.main.fragment_extraction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExtractionFragment : Fragment(), View.OnClickListener {

    private val viewModel: ExtractionViewModel by viewModels()

    private lateinit var fileName: String
    private lateinit var initBytes: ByteArray
    private lateinit var generator: MWCGenerator

    private lateinit var customDialog: CustomDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        customDialog = CustomDialog(requireContext())
        return inflater.inflate(R.layout.fragment_extraction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_choose_audio.setOnClickListener(this)
        btn_choose_key.setOnClickListener(this)
        btn_extract.setOnClickListener(this)

        viewModel.initBytes.observe(viewLifecycleOwner, Observer { value ->
            value?.let { bytes ->
                this.initBytes = bytes
            }
        })

        viewModel.generator.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                this.generator = it
                et_key_a.setText(String.format(generator.a.toString()))
                et_key_b.setText(String.format(generator.b.toString()))
                et_key_c0.setText(String.format(generator.c0.toString()))
                et_key_x0.setText(String.format(generator.x0.toString()))
            }
        })

        viewModel.resultMessage.observe(viewLifecycleOwner, Observer { value ->
            value?.let { message ->
                et_message.setText(message)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { value ->
            value?.let { status ->
                when (status) {
                    true -> customDialog.showLoadingDialog()
                    false -> {
                        if (customDialog.isLoadingDialogShowing()) {
                            customDialog.showSuccessDialog(
                                "The message has been extracted successfully with running time: ${ExtractionViewModel.runningTime} seconds."
                            )
                        }
                        customDialog.closeLoadingDialog()
                    }
                }
            }
        })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_choose_audio -> selectAudioResultLauncher.launch("audio/*")
            R.id.btn_choose_key -> {
                if (!this::initBytes.isInitialized) {
                    toast(getString(R.string.please_init_stego))
                    return
                }
                selectKeyResultLauncher.launch("*/*")
            }
            R.id.btn_extract -> when {
                !this::initBytes.isInitialized -> toast(getString(R.string.please_init_stego))
                !this::generator.isInitialized -> toast(getString(R.string.please_init_key))
                else -> viewModel.extractMessage(this.initBytes, this.generator)
            }
        }
    }

    private val selectAudioResultLauncher =
        registerForActivityResult(GetContent()) { uri ->
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

    private val selectKeyResultLauncher =
        registerForActivityResult(GetContent()) { uri ->
            requireContext().contentResolver.openInputStream(uri)?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.setKey(it.readBytes())
                }
            }
        }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}