package com.example.studentasisstant.Activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentasisstant.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class ForgetPassword : AppCompatActivity() {
    private lateinit var appLogo    : ImageView
    private lateinit var email      : TextInputEditText
    private lateinit var information: TextView
    private lateinit var sendLink   : Button
    private lateinit var auth      : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        appLogo     =  findViewById(R.id.imgappLogo)
        email       =  findViewById(R.id.etEmailAuthentication)
        information =  findViewById(R.id.txtAuthenticationInformation)
        sendLink    =  findViewById(R.id.btAuthenticateUser)
        information.visibility = View.GONE
        auth       = FirebaseAuth.getInstance()
        sendLink.setOnClickListener{
            information.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(email.text.toString())
                .addOnSuccessListener {
                    OnCompleteListener<Any?> { p0 ->
                        if(p0.isSuccessful) {
                            Toast.makeText(this@ForgetPassword,"Email Sended to your Email. Follow the Process To change your Password.",Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(this@ForgetPassword,"Technical Problem Try After Sometime",Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
        }
    }
}