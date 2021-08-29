package com.example.android_ktor_server

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ServerService : Service() {
    companion object {
        const val INTENT_EXTRA_STATUS = "INTENT_EXTRA_STATUS"
        private const val INTENT_EXTRA_RESPONSE = "INTENT_EXTRA_RESPONSE"
        private const val NOTIFICATION_ID = 1

        fun getIntent(context: Context, status: Boolean, response: String) =
            Intent(context, ServerService::class.java).apply {
                putExtra(INTENT_EXTRA_STATUS, status)
                putExtra(INTENT_EXTRA_RESPONSE, response)
            }
    }

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var server: NettyApplicationEngine? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra(INTENT_EXTRA_STATUS, false) == false) {
            startServer(intent.getStringExtra(INTENT_EXTRA_RESPONSE) ?: "")
        } else {
            stopServer(startId)
        }
        return START_STICKY
    }

    private fun startServer(response: String) {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java)
                .apply { putExtra(INTENT_EXTRA_STATUS, true) }.let { notificationIntent ->
                    PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
        val notification = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        scope.launch {
            server = getServer(response)
            server?.start(wait = true)
        }
    }

    private fun stopServer(startId: Int) {
        stopForeground(true)
        stopSelf(startId)
        server?.stop(100, 100)
    }

    private suspend fun getServer(response: String): NettyApplicationEngine {
        return embeddedServer(Netty, 8080) {
            install(ContentNegotiation) {
                gson {}
            }
            routing {
                get("/") {
                    call.respond(response)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}