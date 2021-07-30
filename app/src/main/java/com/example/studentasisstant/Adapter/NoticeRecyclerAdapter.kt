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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.studentasisstant.Activity.UploadAndView
import com.example.studentasisstant.Modal.NoticeModal
import com.example.studentasisstant.R

class NoticeRecyclerAdapter(val context: Context?, private val noticeList: ArrayList<NoticeModal> ) :
    RecyclerView.Adapter<NoticeRecyclerAdapter.NoticeViewHolder>() {
    private lateinit var recyclerAnimation: Animation
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoticeViewHolder {
        val view1 = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_notice, parent, false)
        recyclerAnimation = AnimationUtils.loadAnimation(context,R.anim.recyclerview)
        return NoticeViewHolder(view1)
    }
    override fun getItemCount(): Int {
        return noticeList.size
    }
    override fun onBindViewHolder(p0: NoticeViewHolder, p1: Int) {
        val list = noticeList[p1]
        p0.noticeContent.animation = recyclerAnimation
        p0.noticeImage.setImageResource(R.drawable.pdf)
        p0.noticeTitle.text = list.noticeTitle
        p0.noticeDate.text = list.noticeDate
        val link = list.noticeLink
        p0.noticeContent.setOnClickListener {
            val intent = Intent(context,UploadAndView::class.java)
            intent.putExtra("aim","open")
            intent.putExtra("link",link)
            context?.startActivity(intent)
        }
    }
    class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var noticeImage: ImageView = view.findViewById(R.id.imgNoticeImage)
        var noticeTitle: TextView = view.findViewById(R.id.txtNoticeTitle)
        var noticeDate: TextView = view.findViewById(R.id.txtUploadDate)
        var noticeContent: LinearLayout = view.findViewById(R.id.noticeContent)
    }
}