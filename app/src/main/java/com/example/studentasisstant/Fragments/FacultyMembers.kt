package com.example.studentasisstant.Fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studentasisstant.Adapter.FacultyRecyclerAdapter
import com.example.studentasisstant.Modal.FacultyModal
import com.example.studentasisstant.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot


class FacultyMembers : Fragment() {
    private lateinit var facultyfstore        : FirebaseFirestore
    private lateinit var loadingBar           : SpinKitView
    private lateinit var doubleBounce         : Sprite


    private lateinit var facultyLayoutManager : RecyclerView.LayoutManager
    private lateinit var facultyRecyclerView  : RecyclerView
    private lateinit var facultyAdapter       : FacultyRecyclerAdapter
    private val facultyInfoList = arrayListOf<FacultyModal>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view  = inflater.inflate(R.layout.fragment_faculty_members, container, false)
        facultyfstore  = FirebaseFirestore.getInstance()
        facultyRecyclerView  = view.findViewById(R.id.recyclerFaculty)
        facultyLayoutManager = LinearLayoutManager(context)
        loadingBar           = view.findViewById(R.id.facultyBar)
        doubleBounce         = DoubleBounce()
        loadingBar.setIndeterminateDrawable(doubleBounce)
        loadingBar.visibility  = View.GONE
        updateRecyclerView()
        return view
    }

    private fun updateRecyclerView() {
        loadingBar.visibility   = View.VISIBLE
        facultyfstore.collection("Faculty").get()
            .addOnSuccessListener { querySnapshot ->
                loadingBar.visibility = View.GONE
                if (!querySnapshot.isEmpty) {

                    val facultyList = querySnapshot.documents
                    for(i in facultyList)
                    {
                      val facultyObject = FacultyModal(
                          i.get("facultyName").toString(),
                          i.get("facultyEmail").toString(),
                          i.get("facultyQualification").toString(),
                          i.get("facultyImage").toString(),
                      )
                        facultyInfoList.add(facultyObject)
                        facultyAdapter = FacultyRecyclerAdapter(context as? Activity,facultyInfoList)
                        facultyAdapter.notifyDataSetChanged()
                        facultyRecyclerView.adapter = facultyAdapter
                        facultyRecyclerView.layoutManager = facultyLayoutManager
                        facultyRecyclerView.addItemDecoration(
                            DividerItemDecoration(
                                facultyRecyclerView.context,
                                (facultyLayoutManager as LinearLayoutManager).orientation
                        ))
                    }
                }
                    else
                {
                    Toast.makeText(context,"No Data Available to Laod",Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(context,"Fail to Load Data",Toast.LENGTH_LONG).show()
            }
    }
}