package com.ryanrvldo.spreadspectrumsteganography.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ryanrvldo.spreadspectrumsteganography.databinding.ActivityLauncherBinding

class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBegin.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finishAfterTransition()
            }
        }
    }
}