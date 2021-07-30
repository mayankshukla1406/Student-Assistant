package com.example.studentasisstant.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.studentasisstant.Modal.FacultyModal
import com.example.studentasisstant.R
import com.squareup.picasso.Picasso
import io.armcha.elasticview.ElasticView

class FacultyRecyclerAdapter(val context : Context?, private val itemList : ArrayList<FacultyModal>)
    : RecyclerView.Adapter<FacultyRecyclerAdapter.FacultyViewHolder>()
{
    private lateinit var recyclerAnimation : Animation
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FacultyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_faculty,parent,false)
        recyclerAnimation = AnimationUtils.loadAnimation(context,R.anim.recyclerview)
        return FacultyViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
        val faculty = itemList[position]
        holder.facultyContent.animation = recyclerAnimation
        holder.facultyName.text         = faculty.facultyName
        holder.facultyEmail.text          = faculty.facultyEmail
        holder.facultyEmail.setOnClickListener{
            Toast.makeText(context,"Functionality Working",Toast.LENGTH_LONG).show()
        }
        holder.facultyQualification.text     = faculty.facultyQualification
        Picasso.get().load(faculty.facultyImage).error(R.drawable.ic_launcher_background).into(holder.facultyProfile)
        holder.facultyContent.setOnClickListener{
            Toast.makeText(context,"You Clicked on ${faculty.facultyName}'s Profile",Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    class FacultyViewHolder(view : View):RecyclerView.ViewHolder(view) {
        var facultyContent         : ElasticView = view.findViewById(R.id.facultyContent)
        var facultyProfile         : ImageView   = view.findViewById(R.id.imgFacultyImage)
        var facultyName            : TextView    = view.findViewById(R.id.txtFacultyName)
        var facultyEmail             : TextView    = view.findViewById(R.id.txtFacultyEmail)
        var facultyQualification   : TextView    = view.findViewById(R.id.txtFacultyQualification)
    }
}