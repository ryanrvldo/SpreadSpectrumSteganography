package com.ryanrvldo.spreadspectrumsteganography.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.ryanrvldo.spreadspectrumsteganography.databinding.ActivityLauncherBinding

class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding

    companion object {
        private const val SHARED_PREFERENCE_NAME = "launcher_pref"
        private const val LAUNCHER_PREFERENCE_NAME = "show_launcher"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

        if (!sharedPreferences.getBoolean(LAUNCHER_PREFERENCE_NAME, true)) {
            toMainActivity()
        }

        binding.btnBegin.setOnClickListener {
            toMainActivity()
            sharedPreferences.edit {
                putBoolean(LAUNCHER_PREFERENCE_NAME, false)
            }
        }
    }

    private fun toMainActivity() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finishAfterTransition()
        }
    }
}