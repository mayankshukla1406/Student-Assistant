package com.example.studentasisstant.Helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.studentasisstant.R
import kotlin.random.Random

class NotificationHelper(context:Context):ContextWrapper(context) {
    private val CHANNEL_NAME = "High Priority Channel"
    private val CHANNEL_ID   = "com.example.notification$CHANNEL_NAME"
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels()
    {
        val notificationChannel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.description = "This is the Description of The Channel"
        notificationChannel.lightColor = Color.RED
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
    }

    fun sendHighPriorityNotification(title : String,body : String,activityName : Class<*>)
    {
        val intent = Intent(this,activityName)
        val pendintIntent = PendingIntent.getActivity(this,267,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle().setSummaryText("summary").setBigContentTitle(title).bigText(body))
                .setContentIntent(pendintIntent)
                .setAutoCancel(true)
                .build()
        NotificationManagerCompat.from(this).notify(Random.nextInt(),notification)
    }
   companion object{
       private const val TAG = "Notification Helper"
   }
    init {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createChannels()
        }
    }
}