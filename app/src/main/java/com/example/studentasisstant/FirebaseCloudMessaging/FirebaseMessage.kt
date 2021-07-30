package com.example.studentasisstant.FirebaseCloudMessaging

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.studentasisstant.Activity.MainActivity
import com.example.studentasisstant.Activity.UploadAndView
import com.example.studentasisstant.Helper.NotificationHelper
import com.example.studentasisstant.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessage : FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(this, MainActivity::class.java)
        val notificationID = Random.nextInt()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent : PendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val cid = "fcm_default_channel"
        val dsounduri : Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val nbuilder = NotificationCompat.Builder(this,cid)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setContentIntent(pendingIntent)
            .setSound(dsounduri)
            .setPriority(Notification.BADGE_ICON_LARGE)
        val rManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
       val oChannel = NotificationChannel(cid,"Customer",NotificationManager.IMPORTANCE_HIGH)
        oChannel.description = "Student Assistant"
        oChannel.enableLights(true)
        oChannel.lightColor = Color.GREEN
        rManager.createNotificationChannel(oChannel)
        rManager.notify(notificationID,nbuilder.build())
    }
}