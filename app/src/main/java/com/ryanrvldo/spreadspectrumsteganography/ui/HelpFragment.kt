package com.ryanrvldo.spreadspectrumsteganography.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ryanrvldo.spreadspectrumsteganography.databinding.FragmentHelpBinding
import com.ryanrvldo.spreadspectrumsteganography.slider.HelpSliderAdapter
import com.ryanrvldo.spreadspectrumsteganography.util.HelpData

class HelpFragment : Fragment() {

    private lateinit var embedHelpSliderAdapter: HelpSliderAdapter
    private lateinit var extractHelpSliderAdapter: HelpSliderAdapter
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        embedHelpSliderAdapter = HelpSliderAdapter(
            HelpData.getEmbedHelpInstruction(requireContext())
        )
        extractHelpSliderAdapter = HelpSliderAdapter(
            HelpData.getExtractHelpInstruction(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        embedHelpSliderAdapter.setupIndicators(
            requireContext(),
            binding.embedIndicatorsContainer
        )
        embedHelpSliderAdapter.setCurrentIndicator(
            requireContext(),
            0,
            binding.embedIndicatorsContainer
        )
        extractHelpSliderAdapter.setupIndicators(
            requireContext(),
            binding.extractIndicatorsContainer
        )
        extractHelpSliderAdapter.setCurrentIndicator(
            requireContext(),
            0,
            binding.extractIndicatorsContainer
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewpagerEmbed.adapter = embedHelpSliderAdapter
        binding.viewpagerExtract.adapter = extractHelpSliderAdapter

        binding.viewpagerEmbed.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                embedHelpSliderAdapter.setCurrentIndicator(
                    requireContext(),
                    position,
                    binding.embedIndicatorsContainer
                )
            }
        })

        binding.viewpagerExtract.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                extractHelpSliderAdapter.setCurrentIndicator(
                    requireContext(),
                    position,
                    binding.extractIndicatorsContainer
                )
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}