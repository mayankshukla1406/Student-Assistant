package com.example.studentasisstant.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studentasisstant.Activity.UploadAndView
import com.example.studentasisstant.Adapter.AssignmentsRecyclerAdapter
import com.example.studentasisstant.Modal.AssignmentModal
import com.example.studentasisstant.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Assignments : Fragment() {
    private val assignmentRequestCode = 1000
    private lateinit var assignmentfstore          : FirebaseFirestore
    private lateinit var loadingBar                : SpinKitView
    private lateinit var doubleBounce              : Sprite
    private lateinit var swipeRefreshLayout        : SwipeRefreshLayout
    private lateinit var uploadAssignment          : Button
    private lateinit var assignmentsPath           : Uri
    private lateinit var assignmentLayoutManager   : RecyclerView.LayoutManager
    private lateinit var assignmentAdapter         : AssignmentsRecyclerAdapter
    private lateinit var assignmentRecyclerView    : RecyclerView
    private lateinit var assignmentLogo            : ImageView
    private var assignmentInfoList = arrayListOf<AssignmentModal>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_assignments, container, false)
        assignmentRecyclerView   = view.findViewById(R.id.recyclerAssignment)
        assignmentLayoutManager  = LinearLayoutManager(context as Activity)
        loadingBar = view.findViewById(R.id.LoadingBarAssignment)
        swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshAssignment)
        uploadAssignment = view.findViewById(R.id.btUploadAssignment)
        uploadAssignment.visibility = View.GONE
        assignmentLogo = view.findViewById(R.id.imgAssignmentLogo)
        assignmentfstore = FirebaseFirestore.getInstance()
        doubleBounce = DoubleBounce()
        loadingBar.visibility = View.GONE
        loadingBar.setIndeterminateDrawable(doubleBounce)
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("error", "Error in loading Designation")
                }
                if (value?.getString("Position") == "Professor") {
                    uploadAssignment.visibility = View.VISIBLE
                    uploadAssignment.setOnClickListener {
                        pickAssignment()
                    }
                }
            }
        updatingRecyclerView()
        return view
    }
    private fun pickAssignment() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "application/pdf"
        startActivityForResult(intent, assignmentRequestCode)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == assignmentRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            assignmentsPath = data.data!!
            val intent = Intent(context, UploadAndView::class.java)
            intent.putExtra("aim","upload")
            intent.putExtra("value", "assignment")
            intent.putExtra("filepath", assignmentsPath.toString())
            startActivity(intent)
            activity?.supportFragmentManager?.beginTransaction()?.detach(Assignments())
        }
    }
    private fun updatingRecyclerView() {
        loadingBar.visibility = View.VISIBLE
        assignmentfstore.collection("Assignments").get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                loadingBar.visibility = View.GONE
                if (!queryDocumentSnapshots.isEmpty) {

                    val list = queryDocumentSnapshots.documents
                    for (d in list) {
                        // after getting this list we are passing
                        // that list to our object class.
                        val obj = AssignmentModal(
                            d.get("assignmentTitle").toString(),
                            d.get("assignmentDueDate").toString(),
                            d.get("assignmentLink").toString()
                        )
                        assignmentInfoList.add(obj)
                        assignmentAdapter = AssignmentsRecyclerAdapter(context as? Activity , assignmentInfoList)
                        assignmentAdapter.notifyDataSetChanged()
                        assignmentRecyclerView.adapter = assignmentAdapter
                        assignmentRecyclerView.layoutManager = assignmentLayoutManager
                        assignmentRecyclerView.addItemDecoration(
                            DividerItemDecoration(
                                assignmentRecyclerView.context,
                                (assignmentLayoutManager as LinearLayoutManager).orientation
                            )
                        )
                    }
                } else {
                    Toast.makeText(context, "No data found in Database", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {

                Toast.makeText(context, "Fail to get the data.", Toast.LENGTH_SHORT).show()
            }
        swipeRefreshLayout.setOnRefreshListener {
            assignmentAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}