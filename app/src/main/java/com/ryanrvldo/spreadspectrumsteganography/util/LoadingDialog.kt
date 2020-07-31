package com.ryanrvldo.spreadspectrumsteganography.util

import android.content.Context
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ryanrvldo.spreadspectrumsteganography.R

class LoadingDialog(val context: Context) {
    private val dialog = MaterialAlertDialogBuilder(context)
        .setView(View.inflate(context, R.layout.custom_loading_dialog, null))
        .setCancelable(false)
        .create()

    fun show() {
        dialog.show()
    }

    fun close() {
        dialog.dismiss()
    }
}