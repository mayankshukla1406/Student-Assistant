package com.example.studentasisstant.Activity

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.*
import com.example.studentasisstant.FirebaseCloudMessaging.TokenUpdate
import com.example.studentasisstant.Fragments.About
import com.example.studentasisstant.Fragments.Ask_Feedback
import com.example.studentasisstant.Fragments.Home
import com.example.studentasisstant.Fragments.Profile
import com.example.studentasisstant.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout            : DrawerLayout
    private lateinit var fauth                   : FirebaseAuth
    private lateinit var coordinatorLayout               : CoordinatorLayout
    private lateinit var toolbar                         : androidx.appcompat.widget.Toolbar
    private lateinit var frameLayout                     : FrameLayout
    private lateinit var navigationView                  : NavigationView
    private lateinit var actionBarDrawerToggle           : ActionBarDrawerToggle
    private lateinit var notificationManager             : NotificationManager

    var previousMenuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(
                    Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
        toolbar           =  findViewById(R.id.toolbar)
        frameLayout       =  findViewById(R.id.frameLayout)
        drawerLayout      =  findViewById(R.id.drawerLayout)
        navigationView    =  findViewById(R.id.navigationView)
        coordinatorLayout =  findViewById(R.id.coordinatorLayout)
        fauth             =  FirebaseAuth.getInstance()
        FirebaseMessaging.getInstance().subscribeToTopic("notified")
        setupToolbar()
        openHome()
        actionBarDrawerToggle  = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when(it.itemId){
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()

                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            Profile()
                        )
                        .commit()

                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.ask_feedback -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            Ask_Feedback()
                        )
                        .commit()

                    supportActionBar?.title = "Ask/FeedBack"
                    drawerLayout.closeDrawers()
                }
                R.id.aboutApp -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            About()
                        )
                        .commit()

                    supportActionBar?.title = "About"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    logout()
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "ToolbarTitle"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true
        }
        val id = item.itemId
        if(id== R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    private fun openHome() {
        val fragment = Home()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
        supportActionBar?.title = "Home"
        navigationView.setCheckedItem(R.id.home)
    }
    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when (frag) {
            !is Home -> openHome()

            else -> super.onBackPressed()
        }
    }
    private fun logout() {
        fauth.signOut()
        startLoginActivity()
    }
    private fun startLoginActivity() {
        val intent = Intent(this@MainActivity, AuthenticationActivity::class.java)
        startActivity(intent)
        finish()
    }
}