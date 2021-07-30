package com.example.studentasisstant.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studentasisstant.Adapter.OptionsRecyclerAdapter
import com.example.studentasisstant.Modal.options
import com.example.studentasisstant.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


class Home : Fragment() {
    private lateinit var recyclerAnimation                : Animation
    private lateinit var appLogo                          : ImageView
    private lateinit var fstore                           : FirebaseFirestore
    private lateinit var swipeRefreshLayout               : SwipeRefreshLayout
    private lateinit var progressBar                      : ProgressBar
    private lateinit var progressLayout                   : RelativeLayout
    private lateinit var recyclerDashboard                : RecyclerView
    private lateinit var recylerAdapter                   : OptionsRecyclerAdapter
    private lateinit var layoutManager                    : RecyclerView.LayoutManager
    var optionInfoList = arrayListOf<options>()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        swipeRefreshLayout        =  view.findViewById(R.id.swipeRefresh)
        progressBar               =  view.findViewById(R.id.progressBar)
        progressLayout            =  view.findViewById(R.id.progressLayout)
        appLogo                   =  view.findViewById(R.id.imgappLogo)
        recyclerAnimation         =  AnimationUtils.loadAnimation(context, R.anim.recyclerview)
        fstore                    =  FirebaseFirestore.getInstance()
        recyclerDashboard         =  view.findViewById(R.id.recyclerOptions)
        layoutManager             =  GridLayoutManager(context, 2)
        progressLayout.visibility = View.VISIBLE
        fstore.collection("Options").get()
                .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    // after getting the data we are calling on success method
                    // and inside this method we are checking if the received
                    // query snapshot is empty or not.
                    if (!queryDocumentSnapshots.isEmpty) {
                        // if the snapshot is not empty we are
                        // hiding our progress bar and adding
                        // our data in a list.
                        progressLayout.visibility = View.GONE
                        val list = queryDocumentSnapshots.documents
                        for (d in list) {
                            // after getting this list we are passing
                            // that list to our object class.
                            val obj = options(
                                    d.get("optionName").toString(),
                                    d.get("optionImage").toString(),
                                    d.get("optionValue").toString()
                            )
                            optionInfoList.add(obj)
                            recylerAdapter = OptionsRecyclerAdapter(activity as? Context,optionInfoList)
                            recylerAdapter.notifyDataSetChanged()
                            recyclerDashboard.adapter = recylerAdapter
                            recyclerDashboard.layoutManager = layoutManager
                            recyclerDashboard.addItemDecoration(
                                    DividerItemDecoration(
                                            recyclerDashboard.context,
                                            (layoutManager as GridLayoutManager).orientation
                                    )
                            )
                        }
                    }
                     // after adding the data to recycler view.
                        // we are calling recycler view notifuDataSetChanged
                        // method to notify that data has been changed in recycler view
                    else {
                        // if the snapshot is empty we are displaying a toast message.
                        Toast.makeText(context, "No data found in Database", Toast.LENGTH_SHORT).show()
                    }
                }).addOnFailureListener(OnFailureListener { // if we do not get any data or any error we are displaying
                    // a toast message that we do not get any data
                    Toast.makeText(context, "Fail to get the data.", Toast.LENGTH_SHORT).show()
                })
        swipeRefreshLayout.setOnRefreshListener {
                    recylerAdapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
            }
        return view
    }

}