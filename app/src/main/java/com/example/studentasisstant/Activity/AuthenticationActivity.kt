package com.example.studentasisstant.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.studentasisstant.Fragments.LoginFragment
import com.example.studentasisstant.Fragments.SignUpFragment
import com.example.studentasisstant.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class AuthenticationActivity : AppCompatActivity(),FirebaseAuth.AuthStateListener {
    lateinit var viewPager   : ViewPager
    lateinit var tabLayout   : TabLayout
    lateinit var fauth       : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        fauth         = FirebaseAuth.getInstance()
        viewPager     =  findViewById(R.id.viewPager)
        tabLayout     =  findViewById(R.id.tablayout)
        tabLayout.addTab(tabLayout.newTab().setText("Login"))
        tabLayout.addTab(tabLayout.newTab().setText("SignUp"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewpagerAdapter.addFragment(LoginFragment(),"Login")
        viewpagerAdapter.addFragment(SignUpFragment(),"SignUp")
        viewPager.adapter = viewpagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }
    class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT )
    {
        private val fragments  : ArrayList<Fragment> = ArrayList<Fragment>()
        private val titles     : ArrayList<String> = ArrayList<String>()
        override fun getCount(): Int {
            return fragments.size
        }
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }
        fun addFragment(fragment : Fragment , title : String)
        {
            fragments.add(fragment)
            titles.add(title)
        }
        override fun getPageTitle(i: Int): CharSequence {
            return titles[i]
        }
    }
    override fun onStart() {
        super.onStart()
        fauth.addAuthStateListener(this)
        if(fauth.currentUser!=null)
        {
            startMainActivity()
        }
    }

    override fun onStop() {
        super.onStop()
        fauth.removeAuthStateListener(this)
    }
    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if(fauth.currentUser!=null)
        {
            startMainActivity()
        }

    }
    private fun startMainActivity() {
        val intent = Intent(this@AuthenticationActivity, MainActivity::class.java)
        startActivityForResult(intent,100)
    }
}