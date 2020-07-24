package com.ryanrvldo.spreadspectrumsteganography.util

import android.content.Context
import com.ryanrvldo.spreadspectrumsteganography.R
import com.ryanrvldo.spreadspectrumsteganography.slider.HelpSlide

class HelpData {
    companion object {
        fun getEmbedHelpInstruction(context: Context): List<HelpSlide> {
            return listOf(
                HelpSlide(
                    R.drawable.undraw_text_files,
                    context.getString(R.string.embed_description_1)
                ),
                HelpSlide(
                    R.drawable.undraw_add_file,
                    context.getString(R.string.embed_instruction_2)
                ),
                HelpSlide(
                    R.drawable.undraw_in_progress,
                    context.getString(R.string.embed_instruction_3)
                )
            )
        }

        fun getExtractHelpInstruction(context: Context): List<HelpSlide> {
            return listOf(
                HelpSlide(
                    R.drawable.undraw_add_file,
                    context.getString(R.string.extract_instruction_1)
                ),
                HelpSlide(
                    R.drawable.undraw_unlock,
                    context.getString(R.string.extract_instruction_2)
                ),
                HelpSlide(
                    R.drawable.processing_bro,
                    context.getString(R.string.extract_instruction_3)
                )
            )
        }
    }
}