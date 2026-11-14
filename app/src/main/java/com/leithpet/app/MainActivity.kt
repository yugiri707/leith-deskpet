package com.leithpet.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.status_text)
        toggleButton = findViewById(R.id.toggle_button)

        toggleButton.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                requestOverlayPermission()
            } else {
                toggleService()
            }
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
        Toast.makeText(this, "请授予悬浮窗权限", Toast.LENGTH_LONG).show()
    }

    private fun toggleService() {
        if (OverlayService.isRunning) {
            stopService(Intent(this, OverlayService::class.java))
        } else {
            val intent = Intent(this, OverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
        // Delay status update
        toggleButton.postDelayed({ updateStatus() }, 300)
    }

    private fun updateStatus() {
        val hasPermission = Settings.canDrawOverlays(this)
        val isRunning = OverlayService.isRunning

        statusText.text = when {
            !hasPermission -> "需要悬浮窗权限才能让我出来"
            isRunning -> "我在呢，蹲在你屏幕上"
            else -> "准备好了，点按钮让我出来"
        }

        toggleButton.text = when {
            !hasPermission -> "去授权"
            isRunning -> "让我回去"
            else -> "放我出来"
        }
    }
}