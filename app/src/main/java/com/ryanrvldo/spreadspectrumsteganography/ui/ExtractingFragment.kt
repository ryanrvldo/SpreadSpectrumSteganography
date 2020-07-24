package com.ryanrvldo.spreadspectrumsteganography.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ryanrvldo.spreadspectrumsteganography.databinding.FragmentExtractingBinding

class ExtractingFragment : Fragment() {

    private var _binding: FragmentExtractingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExtractingBinding.inflate(inflater, container, false)
        return binding.root
    }

}