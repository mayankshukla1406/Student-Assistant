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
import com.example.studentasisstant.Modal.ClassModal
import com.example.studentasisstant.R
import com.squareup.picasso.Picasso
import io.armcha.elasticview.ElasticView

class ClassRecyclerAdapter(val context : Context?, private val classList : ArrayList<ClassModal>)
    :RecyclerView.Adapter<ClassRecyclerAdapter.ClassViewHolder>() {
    private lateinit var recyclerAnimation : Animation


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_class,parent,false)
        recyclerAnimation = AnimationUtils.loadAnimation(context,R.anim.recyclerview)
        return ClassViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val schedule = classList[position]
        holder.classCard.animation = recyclerAnimation
        holder.className.text = schedule.className
        holder.classTime.text = schedule.classTime
        holder.classLink.text  = "Link : ${schedule.classLink}"
        Picasso.get().load(R.drawable.online).into(holder.classImage)
        holder.classLink.setOnClickListener{
            val intent = Intent(context,UploadAndView::class.java)
            intent.putExtra("aim","open")
            intent.putExtra("link",holder.classLink.toString())
            intent.putExtra("class","yes")
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return classList.size
    }
    class ClassViewHolder(view : View) : RecyclerView.ViewHolder(view) {
         val classCard   : ElasticView = view.findViewById(R.id.classContent)
         val className   : TextView    = view.findViewById(R.id.txtClassName)
         val classImage  : ImageView   = view.findViewById(R.id.imgClassImage)
         val classTime   : TextView    = view.findViewById(R.id.txtClassTime)
         val classLink   : TextView    = view.findViewById(R.id.txtClassLink)
    }
}