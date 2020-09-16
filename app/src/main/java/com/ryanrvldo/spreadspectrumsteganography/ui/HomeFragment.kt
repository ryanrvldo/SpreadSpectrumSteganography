package com.ryanrvldo.spreadspectrumsteganography.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ryanrvldo.spreadspectrumsteganography.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_begin.setOnClickListener {
            it.findNavController().navigate(R.id.action_home_to_embedding)
        }

        btn_help.setOnClickListener {
            it.findNavController().navigate(R.id.action_home_to_help)
        }
    }
}