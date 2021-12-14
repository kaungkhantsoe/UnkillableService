package com.kks.unkillableservice

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi

import androidx.core.app.NotificationCompat

import android.content.Context
import android.graphics.Color
import android.util.Log
import java.util.*
import kotlin.concurrent.timer
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.widget.Toast
import android.content.Context.NOTIFICATION_SERVICE

import android.app.NotificationManager
import androidx.core.content.ContextCompat.getSystemService


/**
 * Created by kaungkhantsoe at 14/12/2021
 */

class MyService : Service() {

    var counter = 0

    companion object {
        const val Action = "RestartService"
        const val STOP_SERVICE_BROADCAST_KEY = "StopServiceBroadcastKey"
        const val RQS_STOP_SERVICE = 1
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            showCustomForegroundNotification()
        else startForeground(
            1,
            Notification().apply {
                contentIntent = getPendingIntent()
            }
        )
    }

    private fun getPendingIntent(): PendingIntent {

        val requestID = System.currentTimeMillis().toInt()

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)

        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        return PendingIntent.getActivity(
            this,
            requestID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCustomForegroundNotification() {
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val channelName = "Background Service"
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        notification.contentIntent = getPendingIntent()
        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stoptimertask()
        val broadcastIntent = Intent()
        broadcastIntent.action = Action
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    var timer: Timer? = null

    private fun startTimer() {
        timer  = timer(initialDelay = 1000, period = 1000) {
            Log.i("Count", "=========  " + counter++)
        }
    }

    private fun stoptimertask() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}