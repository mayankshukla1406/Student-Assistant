package com.example.studentasisstant.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentasisstant.Activity.UploadAndView
import com.example.studentasisstant.Adapter.ClassRecyclerAdapter
import com.example.studentasisstant.Modal.ClassModal
import com.example.studentasisstant.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ClassSchedules : Fragment() {
    private lateinit var classfstore          : FirebaseFirestore
    private lateinit var loadingBar           : SpinKitView
    private lateinit var doubleBounce         : Sprite
    private lateinit var uploadSchedule       : Button

    private lateinit var classLayoutManager : RecyclerView.LayoutManager
    private lateinit var classRecyclerView  : RecyclerView
    private lateinit var classAdapter       : ClassRecyclerAdapter
    private val classInfoList = arrayListOf<ClassModal>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_class_schedules, container, false)
        uploadSchedule  = view.findViewById(R.id.btUploadschedule)
        uploadSchedule.visibility = View.GONE
        classfstore  = FirebaseFirestore.getInstance()
        loadingBar   = view.findViewById(R.id.classBar)
        doubleBounce = DoubleBounce()
        loadingBar.setIndeterminateDrawable(doubleBounce)
        loadingBar.visibility = View.GONE
        classRecyclerView   = view.findViewById(R.id.recyclerClass)
        classLayoutManager  = LinearLayoutManager(context)
        updateRecyclerView()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("error", "Error in loading Designation")
                }
                if (value?.getString("Position") == "Professor") {
                    uploadSchedule.visibility = View.VISIBLE
                    uploadSchedule.setOnClickListener {
                        val intent = Intent(context, UploadAndView::class.java)
                        intent.putExtra("aim", "upload")
                        intent.putExtra("value", "classes")
                        startActivity(intent)
                    }
                }
            }
        return  view
    }
    private fun updateRecyclerView() {
        loadingBar.visibility   = View.VISIBLE
        classfstore.collection("ClassSchedules").get()
            .addOnSuccessListener { querySnapshot ->
                loadingBar.visibility = View.GONE
                if (!querySnapshot.isEmpty) {

                    val classesList = querySnapshot.documents
                    for(i in classesList)
                    {
                        val classObject = ClassModal(
                            i.get("className").toString(),
                            i.get("classTime").toString(),
                            i.get("classLink").toString()
                        )
                        classInfoList.add(classObject)
                        classAdapter = ClassRecyclerAdapter(context as? Activity,classInfoList)
                        classAdapter.notifyDataSetChanged()
                        classRecyclerView.adapter = classAdapter
                        classRecyclerView.layoutManager = classLayoutManager
                        classRecyclerView.addItemDecoration(
                            DividerItemDecoration(
                                classRecyclerView.context,
                                (classLayoutManager as LinearLayoutManager).orientation
                            )
                        )
                    }
                }
                else
                {
                    Toast.makeText(context,"No Data Available to Laod", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(context,"Fail to Load Data", Toast.LENGTH_LONG).show()
            }
    }
}