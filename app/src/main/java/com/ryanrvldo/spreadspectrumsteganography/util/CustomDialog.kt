package com.ryanrvldo.spreadspectrumsteganography.util

import android.content.Context
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ryanrvldo.spreadspectrumsteganography.R

class CustomDialog(val context: Context) {
    private val loadingDialog = MaterialAlertDialogBuilder(context)
        .setView(View.inflate(context, R.layout.custom_loading_dialog, null))
        .setCancelable(false)
        .create()

    fun showLoadingDialog() = loadingDialog.show()

    fun closeLoadingDialog() = loadingDialog.dismiss()

    fun isLoadingDialogShowing() = loadingDialog.isShowing

    private val successDialog = MaterialAlertDialogBuilder(context)
        .setView(View.inflate(context, R.layout.custom_success_dialog, null))
        .setTitle("Process is finished.")
        .setPositiveButton("Okay") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        .setCancelable(true)
        .create()

    fun showSuccessDialog() = successDialog.show()
}