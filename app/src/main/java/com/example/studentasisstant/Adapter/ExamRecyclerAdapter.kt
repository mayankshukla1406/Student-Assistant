package com.example.studentasisstant.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentasisstant.Activity.UploadAndView
import com.example.studentasisstant.Modal.ExamModal
import com.example.studentasisstant.R
import com.squareup.picasso.Picasso
import io.armcha.elasticview.ElasticView

class ExamRecyclerAdapter(val context : Context, private val examList : ArrayList<ExamModal>)
    :RecyclerView.Adapter<ExamRecyclerAdapter.ExamViewHolder>() {
    private lateinit var recyclerAnimation  : Animation
    class ExamViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val examContent    : ElasticView = view.findViewById(R.id.examContent)
        val examName       : TextView    = view.findViewById(R.id.txtExamName)
        val examImage      : ImageView   = view.findViewById(R.id.imgExamImage)
        val examUploadDate : TextView  = view.findViewById(R.id.txtUploadDate)
        val examYear       : TextView  = view.findViewById(R.id.txtExamYear)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_exam,parent,false)
        recyclerAnimation  = AnimationUtils.loadAnimation(context,R.anim.recyclerview)
        return ExamViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val exam = examList[position]
        holder.examContent.animation = recyclerAnimation
        holder.examName.text         = "ExamName : ${exam.examName}"
        holder.examUploadDate.text   = "Uploaded on : ${exam.examUploadDate}"
        holder.examYear.text         = "Year of Exam : ${exam.examYear}"
        val examLink = exam.examLink
        Picasso.get().load(R.drawable.pdf).into(holder.examImage)
        holder.examContent.setOnClickListener{
            val intent = Intent(context, UploadAndView::class.java)
            intent.putExtra("aim","open")
            intent.putExtra("link",examLink)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return examList.size
    }
}