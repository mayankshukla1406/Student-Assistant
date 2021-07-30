package com.example.studentasisstant.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import android.widget.Toast
import com.example.studentasisstant.Activity.MainActivity
import com.example.studentasisstant.Activity.MapsActivity
import com.example.studentasisstant.FirebaseCloudMessaging.FirebaseNotification
import com.example.studentasisstant.Helper.NotificationHelper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

abstract class GeofenceBroadcastReceiver: BroadcastReceiver() {
    abstract var firebaseHelper : FirebaseNotification
    private lateinit var geofencelist: List<Geofence>
    private lateinit var audioManager: AudioManager
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        }
        val notificationHelper = NotificationHelper(context!!)
        firebaseHelper     = FirebaseNotification(context)
        val geofenceEvent = GeofencingEvent.fromIntent(intent!!)
        if (geofenceEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofenceEvent.errorCode)
            Log.d("error", errorMessage)
            Log.d("onReceive", "Geofence Event has got an Error")
        }
        val location = geofenceEvent.triggeringLocation
        val transitionTypes = geofenceEvent.geofenceTransition

        geofencelist = geofenceEvent.triggeringGeofences
        when (transitionTypes) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Toast.makeText(context, "You Entered the geofence", Toast.LENGTH_LONG).show()
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                onCurrentToken("GeofenceTransition","You Entered the geofence")
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Toast.makeText(context, "You Dwell in the geofence", Toast.LENGTH_LONG).show()
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                onCurrentToken("GeofenceTransition","You Dwell in the geofence")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Toast.makeText(context, "You Exited the geofence", Toast.LENGTH_LONG).show()
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                onCurrentToken("GeofenceTrigger","You Exited the geofence")
            }
        }
    }
    fun onCurrentToken(title : String,message:String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val tokenCode = task.result
            firebaseHelper.sendFCMPush(tokenCode,title,message)
        })
    }
}