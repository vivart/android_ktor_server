package com.example.android_ktor_server

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.android_ktor_server.ServerService.Companion.INTENT_EXTRA_STATUS

class MainActivity : AppCompatActivity() {
    companion object {
        const val CHANNEL_ID = "1234"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        val status = intent.getBooleanExtra(INTENT_EXTRA_STATUS, false)
        setContent {
            MainScreen(status, this::toggleSever, this::launch)
        }
    }

    private fun toggleSever(status: Boolean, response: String) {
        startService(ServerService.getIntent(this, status, response))
    }

    private fun launch(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}