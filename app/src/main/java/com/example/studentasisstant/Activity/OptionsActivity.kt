package com.example.studentasisstant.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.studentasisstant.Fragments.*
import com.example.studentasisstant.R
import java.util.*

class OptionsActivity : AppCompatActivity() {
    private lateinit var coordinatorLayout       : CoordinatorLayout
    private lateinit var toolbar                 : androidx.appcompat.widget.Toolbar
    private lateinit var frameLayout             : FrameLayout
    private lateinit var fragmentValue           : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        toolbar           =  findViewById(R.id.toolbarOptions)
        frameLayout       =  findViewById(R.id.frameLayoutOptions)
        coordinatorLayout =  findViewById(R.id.coordinatorLayoutOptions)
        setupFragmentToolbar()
        if(intent!=null)
        {
            fragmentValue   =  intent.getStringExtra("FragmentName").toString().toLowerCase(Locale.ROOT)
            when (fragmentValue.trim()) {
                "1" -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayoutOptions,Notices()).commit()
                    supportActionBar?.title = "Notices"
                }
                "2" -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayoutOptions,TimeTable()).commit()
                    supportActionBar?.title = "Time Table"
                }
                "3" -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayoutOptions,ClassSchedules()).commit()
                    supportActionBar?.title = "Class Schedules"
                }
                "4" -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayoutOptions,
                        Assignments()
                    ).commit()
                    supportActionBar?.title = "Assignments"
                }
                "5" ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayoutOptions,FacultyMembers()).commit()
                    supportActionBar?.title = "Faculty Members"
                }
                "6" ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayoutOptions,PreviousYearsExamPapers()).commit()
                    supportActionBar?.title = "Previous Years Exam Papers"
                }
                "7" ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayoutOptions,Result()).commit()
                    supportActionBar?.title = "ExaminationResult"
                }
            }
        }
        else
        {
            Toast.makeText(this@OptionsActivity,"Did not Receive Fragments Name", Toast.LENGTH_LONG).show()
            Log.d("OptionsActivity","Failed to get FragmentName")
        }

    }
    private fun setupFragmentToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "ToolbarTitle"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@OptionsActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}