package com.ryanrvldo.spreadspectrumsteganography.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ryanrvldo.spreadspectrumsteganography.R
import com.ryanrvldo.spreadspectrumsteganography.slider.HelpSliderAdapter
import com.ryanrvldo.spreadspectrumsteganography.util.HelpData
import kotlinx.android.synthetic.main.fragment_help.*

class HelpFragment : Fragment() {

    private lateinit var embedHelpSliderAdapter: HelpSliderAdapter
    private lateinit var extractHelpSliderAdapter: HelpSliderAdapter

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
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        embedHelpSliderAdapter.setupIndicators(
            requireContext(),
            embed_indicators_container
        )
        embedHelpSliderAdapter.setCurrentIndicator(
            requireContext(),
            0,
            embed_indicators_container
        )
        extractHelpSliderAdapter.setupIndicators(
            requireContext(),
            extract_indicators_container
        )
        extractHelpSliderAdapter.setCurrentIndicator(
            requireContext(),
            0,
            extract_indicators_container
        )

        viewpager_embed.adapter = embedHelpSliderAdapter
        viewpager_extract.adapter = extractHelpSliderAdapter

        viewpager_embed.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                embedHelpSliderAdapter.setCurrentIndicator(
                    requireContext(),
                    position,
                    embed_indicators_container
                )
            }
        })

        viewpager_extract.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                extractHelpSliderAdapter.setCurrentIndicator(
                    requireContext(),
                    position,
                    extract_indicators_container
                )
            }
        })
    }
}