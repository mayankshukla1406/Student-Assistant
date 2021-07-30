package com.example.studentasisstant.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.studentasisstant.R

class SplashActivity : AppCompatActivity() {
    private val splashScreenTime = 4000
    lateinit var image      : ImageView
    lateinit var topAnim    : Animation
    lateinit var bottomAnim : Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        image = findViewById(R.id.gif)
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        image.animation = topAnim
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        },splashScreenTime.toLong())
    }
}