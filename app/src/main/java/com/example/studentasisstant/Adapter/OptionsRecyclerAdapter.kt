package com.example.studentasisstant.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentasisstant.Activity.OptionsActivity
import com.example.studentasisstant.Modal.options
import com.example.studentasisstant.R
import com.squareup.picasso.Picasso

class OptionsRecyclerAdapter(val context: Context?, private val itemList : ArrayList<options> ) :
        RecyclerView.Adapter<OptionsRecyclerAdapter.OptionsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_options,parent,false)
        return OptionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        val list = itemList[position]
        holder.optionName.text       = list.optionName
        Picasso.get().load(list.optionImage).error(R.drawable.ic_launcher_background).into(holder.optionImage)
        holder.content.setOnClickListener{
            val intent = Intent(context, OptionsActivity::class.java)
            Log.d("OptionsActivity",list.optionValue)
            intent.putExtra("FragmentName",list.optionValue)
            context?.startActivity(intent)
        }
    }
    class OptionsViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val optionName         : TextView = view.findViewById(R.id.txtOptionName)
        val optionImage        : ImageView = view.findViewById(R.id.imgOptionImage)
        val content            : LinearLayout = view.findViewById(R.id.optionsLinearLayout)
    }
}