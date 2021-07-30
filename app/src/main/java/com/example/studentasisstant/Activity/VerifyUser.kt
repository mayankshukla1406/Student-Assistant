package com.example.studentasisstant.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.example.studentasisstant.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

class VerifyUser : AppCompatActivity() {

    private lateinit var progressLayout: RelativeLayout
    private lateinit var appLogo: ImageView
    private lateinit var otpSend: TextView
    private lateinit var sendEMailLink: TextView
    private lateinit var sendEmail: Button
    private lateinit var otpEnter: EditText
    private lateinit var otpVerify: Button
    private lateinit var userid: String
    private lateinit var fauth: FirebaseAuth
    private lateinit var fstore: FirebaseFirestore
    private lateinit var email: String
    private lateinit var phonenumber: String
    private lateinit var pass: String
    private lateinit var name: String
    private lateinit var verificationID: String

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_user)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        appLogo = findViewById(R.id.imgappLogo)
        otpSend = findViewById(R.id.txtotpSend)
        otpEnter = findViewById(R.id.etEnterOtp)
        sendEMailLink = findViewById(R.id.txtEmailSendInfo)
        sendEmail = findViewById(R.id.btverifyEmail)
        sendEMailLink.visibility = View.GONE
        sendEmail.visibility = View.GONE
        otpVerify = findViewById(R.id.btverifyOTP)
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        if (intent != null) {
            name = intent.getStringExtra("Name")!!
            email = intent.getStringExtra("Email")!!
            pass = intent.getStringExtra("Password")!!
            phonenumber = intent.getStringExtra("Number")!!
            callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    val code = p0.smsCode
                    if (code != null) {
                        progressLayout.visibility = View.VISIBLE
                        verifycode(code)
                    }
                }
                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(this@VerifyUser, p0.message, Toast.LENGTH_LONG).show()
                }
                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    verificationID = p0
                    resendToken = p1
                }
            }
            otpverification()
        }
    }

    private fun otpverification() {
        sendverificationcodetosuer(phonenumber)
        // progressLayout.visibility = View.GONE
        otpVerify.setOnClickListener()
        {
            progressLayout.visibility = View.VISIBLE
            val code = otpEnter.text.toString()
            if (code.isEmpty() || code.length < 6) {
                progressLayout.visibility = View.GONE
                otpEnter.error = "Wrong OTP ..."
                otpEnter.requestFocus()
            } else {
                verifycode(code)
                progressLayout.visibility = View.GONE
            }
        }
    }

    private fun sendverificationcodetosuer(phonenumber: String) {
        val options = PhoneAuthOptions.newBuilder(fauth)
            .setPhoneNumber("+91$phonenumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun verifycode(codebyuser: String) {
        val credential = PhoneAuthProvider.getCredential(verificationID, codebyuser)
        otpEnter.visibility = View.GONE
        otpSend.visibility  = View.GONE
        otpVerify.visibility = View.GONE
        signintheuser(credential)
    }

    private fun signintheuser(credential: PhoneAuthCredential) {
        fauth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressLayout.visibility = View.VISIBLE
                fauth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userInfo = fauth.currentUser!!
                            userInfo.sendEmailVerification()
                                .addOnCompleteListener {
                                        task ->
                                    userid = fauth.currentUser!!.uid
                                    val db = fstore.collection("users").document(userid)
                                    val user = mutableMapOf<String, String>()
                                    user["profileName"] = name
                                    user["profileEmail"] = email
                                    user["profilePhoneNumber"] = phonenumber
                                    user["profilePassword"] = pass
                                    user["cashback"] = "0"
                                    user["withdrawid"] = "0"
                                    user["UPIID"] = ""
                                    user["valueImages"] = "0"
                                    db.set(user).addOnSuccessListener {
                                        Log.d(
                                            "SucceessfullyCreate",
                                            "onSuccess: user Profile is created for $userid"
                                        )
                                    }
                                }
                            // Re-enable Verify Email button
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Verification email sent to " + userInfo.email,
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressLayout.visibility = View.GONE
                                sendEMailLink.visibility = View.VISIBLE
                                sendEmail.visibility = View.VISIBLE
                                sendEmail.setOnClickListener {
                                    fauth.currentUser?.reload()
                                    if (userInfo.isEmailVerified) {
                                        Toast.makeText(
                                            this@VerifyUser,
                                            "Your Email Verification is Completed",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                        tokenGet()
                                        val intent = Intent(
                                            this@VerifyUser,
                                            MainActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@VerifyUser,
                                            "Press Again if You Already Verified Your Email otherwise Verify First then click",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            } else {
                                Log.e(
                                    "VerifyingUser",
                                    "sendEmailVerification failed!",
                                    task.exception
                                )
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@VerifyUser,
                                "Error ! " + (task.exception?.message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            else {
                Toast.makeText(this@VerifyUser, task.exception?.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun tokenGet() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val tokenCode = task.result
            val user = FirebaseAuth.getInstance().currentUser?.uid
            val userToken = mutableMapOf<String, Any>()
            userToken["token"] = tokenCode
            FirebaseFirestore.getInstance().collection("Token").document(user.toString())
                .update(userToken).addOnSuccessListener {
                    Log.d("onSucces", "Token Saved Successfully")
                }
        })
    }
}