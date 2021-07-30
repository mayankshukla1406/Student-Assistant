package com.example.studentasisstant.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.studentasisstant.R

class About : Fragment() {
   private lateinit var email : ImageView
    private lateinit var linkedin : ImageView
    private lateinit var facebook : ImageView
    private lateinit var twitter : ImageView
    private lateinit var github : ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        email = view.findViewById(R.id.mail_us)
        linkedin = view.findViewById(R.id.linkedin)
        facebook = view.findViewById(R.id.facebook)
        twitter = view.findViewById(R.id.twitter)
        github  =  view.findViewById(R.id.github)
        email.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gmail.com"))
            startActivity(intent)
        }
        twitter.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/?lang=en"))
            startActivity(intent)
        }
        facebook.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"))
            startActivity(intent)
        }
        github.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/"))
            startActivity(intent)
        }
        linkedin.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://in.linkedin.com/"))
            startActivity(intent)
        }
        return view
    }
}