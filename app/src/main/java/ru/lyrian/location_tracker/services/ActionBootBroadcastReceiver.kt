package ru.lyrian.location_tracker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class ActionBootBroadcastReceiver : BroadcastReceiver() {
    private val actionQuickBootPowerRun = "android.intent.action.QUICKBOOT_POWERON"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED || intent?.action == actionQuickBootPowerRun) {
            val serviceStarterIntent = Intent(context, LocationService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(serviceStarterIntent)
            } else {
                context?.startService(serviceStarterIntent)
            }
        }
    }
}