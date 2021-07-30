package com.example.studentasisstant.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentasisstant.Activity.UploadAndView
import com.example.studentasisstant.Modal.AssignmentModal
import com.example.studentasisstant.R

class AssignmentsRecyclerAdapter(val context : Context?,private val assignmentList:ArrayList<AssignmentModal>)
    : RecyclerView.Adapter<AssignmentsRecyclerAdapter.AssignmentViewHolder>() {
    private lateinit var recyclerAnimation    : Animation
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AssignmentViewHolder {
        val view1 = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_assignments, parent, false)
        recyclerAnimation = AnimationUtils.loadAnimation(context, R.anim.recyclerview)
        return AssignmentViewHolder(view1)
    }

    override fun onBindViewHolder(
        holder: AssignmentViewHolder,
        position: Int
    ) {
        val list = assignmentList[position]
        holder.assignmentContent.animation = recyclerAnimation
        holder.assignmentTitle.text = list.assignmentTitle
        holder.assignmentDueDate.text = list.assignmentDueDate
        holder.assignmentImage.setImageResource(R.drawable.pdf)
        val url = list.assignmentLink
        holder.assignmentContent.setOnClickListener{
            val intent = Intent(context,UploadAndView::class.java)
            intent.putExtra("aim","open")
            intent.putExtra("link",url)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return assignmentList.size
    }
    class AssignmentViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        var assignmentTitle    : TextView = view.findViewById(R.id.txtAssignmentTitle)
        var assignmentImage    : ImageView = view.findViewById(R.id.imgAssignmentImage)
        var assignmentDueDate  : TextView = view.findViewById(R.id.txtDueDate)
        var assignmentContent  : LinearLayout = view.findViewById(R.id.assignmentContent)
    }

}