package com.ryanrvldo.spreadspectrumsteganography.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ryanrvldo.spreadspectrumsteganography.R
import com.ryanrvldo.spreadspectrumsteganography.databinding.FragmentExtractionBinding
import com.ryanrvldo.spreadspectrumsteganography.util.CustomDialog
import com.ryanrvldo.spreadspectrumsteganography.util.MWCGenerator
import com.ryanrvldo.spreadspectrumsteganography.viewmodel.ExtractionViewModel

class ExtractionFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentExtractionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExtractionViewModel by viewModels()

    private lateinit var fileName: String
    private lateinit var initBytes: ByteArray
    private lateinit var generator: MWCGenerator

    private lateinit var customDialog: CustomDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExtractionBinding.inflate(inflater, container, false)
        customDialog = CustomDialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnChooseAudio.setOnClickListener(this)
        binding.btnChooseKey.setOnClickListener(this)
        binding.btnProcess.setOnClickListener(this)

        viewModel.initBytes.observe(viewLifecycleOwner, Observer { value ->
            value?.let { bytes ->
                this.initBytes = bytes
            }
        })

        viewModel.generator.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                this.generator = it
                binding.editTextA.setText(String.format(generator.a.toString()))
                binding.editTextB.setText(String.format(generator.b.toString()))
                binding.editTextC0.setText(String.format(generator.c0.toString()))
                binding.editTxtX0Key.setText(String.format(generator.x0.toString()))
            }
        })

        viewModel.resultMessage.observe(viewLifecycleOwner, Observer { value ->
            value?.let { message ->
                binding.editTxtMessage.setText(message)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { value ->
            value?.let { status ->
                when (status) {
                    true -> customDialog.showLoadingDialog()
                    false -> {
                        if (customDialog.isLoadingDialogShowing()) {
                            customDialog.showSuccessDialog()
                        }
                        customDialog.closeLoadingDialog()
                    }
                }
            }
        })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_choose_audio -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    getAudioFile.launch("audio/*")
                } else {
                    getPermissions.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            R.id.btn_choose_key -> {
                if (!this::initBytes.isInitialized) {
                    toast("Please initialized your stego object first.")
                    return
                }
                getKeyFile.launch("*/*")
            }
            R.id.btn_process -> {
                if (!this::initBytes.isInitialized || !this::generator.isInitialized) {
                    toast("Please initialized stego object or stego key first.")
                    return
                }
                viewModel.extractMessage(this.initBytes, this.generator)
            }
        }
    }

    private val getAudioFile =
        registerForActivityResult(GetContent()) { uri ->
            requireContext().contentResolver.openInputStream(uri)?.let {
                viewModel.setInitBytes(it)
                uri.path?.let { path ->
                    fileName = path.substringAfterLast("/")
                    binding.editTextFilePath.setText(fileName)
                }
            }
        }

    private val getKeyFile =
        registerForActivityResult(GetContent()) { uri ->
            requireContext().contentResolver.openInputStream(uri)?.let {
                viewModel.setKey(it)
            }
        }

    private val getPermissions =
        registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getAudioFile.launch("audio/*")
            } else {
                toast("Permission denied, please allow permission first.")
            }
        }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}