package com.aura.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class DailyCheckInWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val channelId = "aura_daily_reminders"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the NotificationChannel for Android 8.0+
        val channel = NotificationChannel(
            channelId,
            "Aura Daily Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminds you to check in and maintain your streak"
        }
        notificationManager.createNotificationChannel(channel)

        // Build the push notification
        val notification = NotificationCompat.Builder(context, channelId)
            // Note: In production, this should be an @drawable/ic_aura_logo
            .setSmallIcon(android.R.drawable.star_on) 
            .setContentTitle("Your Aura is waiting! ✨")
            .setContentText("Check in today to maintain your streak and evolve your Avatar.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)

        return Result.success()
    }
}
