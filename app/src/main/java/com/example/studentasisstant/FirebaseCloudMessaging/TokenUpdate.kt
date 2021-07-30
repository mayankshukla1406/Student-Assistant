package com.example.studentasisstant.FirebaseCloudMessaging

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import com.example.studentasisstant.Activity.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class TokenUpdate(context: Context): ContextWrapper(context) {
    fun token()
    {
        FirebaseAuth.getInstance().getAccessToken(true)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val tokenCode = task.result
            Log.d("token",tokenCode.toString())
            val user = FirebaseAuth.getInstance().currentUser?.uid
            val userToken = mutableMapOf<String,Any>()
            userToken["token"] = tokenCode.toString()
            FirebaseFirestore.getInstance().collection("Token").document(user.toString())
                .set(userToken).addOnSuccessListener{
                    Log.d("ontokenSucess","Token Saved Successfully")
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
        })
    }
}