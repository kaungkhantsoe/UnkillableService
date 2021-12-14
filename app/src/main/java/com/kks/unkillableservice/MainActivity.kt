package com.kks.unkillableservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.kks.unkillableservice.databinding.ActivityMainBinding

/**
 * Can survive killing app by swipe from recent apps
 * Cannot survive killing app by click on close all from recent apps for MI devices.
 * Since it kill applications and services like deep clean.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var mServiceIntent: Intent
    private lateinit var mService: MyService

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mService = MyService()
        mServiceIntent = getServiceIntent(this)

        binding.btnStart.setOnClickListener {
            if (!isMyServiceRunning(MyService::class.java))
                startService(mServiceIntent)
        }

        binding.btnStop.setOnClickListener {
            this@MainActivity.stopService(getServiceIntent(this@MainActivity))
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .cancelAll()
        }

    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

//    override fun onDestroy() {
//        //stopService(mServiceIntent);
//        val broadcastIntent = Intent()
//        broadcastIntent.action = "restartservice"
//        broadcastIntent.setClass(this, Restarter::class.java)
//        this.sendBroadcast(broadcastIntent)
//        super.onDestroy()
//    }
}