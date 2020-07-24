package com.ryanrvldo.spreadspectrumsteganography.slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.ryanrvldo.spreadspectrumsteganography.R

class HelpSliderAdapter(private val helpSlides: List<HelpSlide>) :
    RecyclerView.Adapter<HelpSliderAdapter.HelpSliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpSliderViewHolder {
        return HelpSliderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.slide_item_container, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return helpSlides.size
    }

    override fun onBindViewHolder(holder: HelpSliderViewHolder, position: Int) {
        holder.bind(helpSlides[position])
    }

    fun setupIndicators(context: Context, container: ViewGroup) {
        val indicators = arrayOfNulls<ImageView>(helpSlides.size)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(0, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(context)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            container.addView(indicators[i])
        }
    }

    fun setCurrentIndicator(context: Context, index: Int, viewGroup: ViewGroup) {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val indicator = viewGroup[i] as ImageView
            if (i == index) {
                indicator.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.indicator_active
                    )
                )
            } else {
                indicator.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

    inner class HelpSliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgIllustration = view.findViewById<ImageView>(R.id.img_item_banner)
        private val tvDescription = view.findViewById<TextView>(R.id.tv_item_description)

        fun bind(helpSlide: HelpSlide) {
            tvDescription.text = helpSlide.description
            imgIllustration.setImageResource(helpSlide.imgBanner)
        }
    }
}