package com.example.studentasisstant.Fragments

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.media.Image
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.studentasisstant.R

class Result : Fragment() {
    private lateinit var appLogo  : ImageView
    private lateinit var textLogo : TextView
    private lateinit var examinationResult : TextView
    private lateinit var checkExaminationResult : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_result, container, false)
        appLogo = view.findViewById(R.id.imgappLogoResult)
        textLogo = view.findViewById(R.id.txtLogo)
        examinationResult = view.findViewById(R.id.txtExaminationResult)
        checkExaminationResult = view.findViewById(R.id.btCheckExamResult)
        checkExaminationResult.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW,Uri.parse("https://results.luonline.in/"))
            startActivity(intent)
        }
        return view
    }
}