package com.example.studentasisstant.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.example.studentasisstant.Adapter.ExamRecyclerAdapter
import com.example.studentasisstant.Modal.ExamModal
import com.example.studentasisstant.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.firestore.FirebaseFirestore

class PreviousYearsExamPapers : Fragment() {
    private lateinit var buttonExamPaper      : Button
    private lateinit var examfstore           : FirebaseFirestore
    private lateinit var loadingBar           : SpinKitView
    private lateinit var doubleBounce         : Sprite

    private lateinit var examLayoutManager : RecyclerView.LayoutManager
    private lateinit var examRecyclerView  : RecyclerView
    private lateinit var examAdapter       : ExamRecyclerAdapter
    private lateinit var filePath          : Uri
    private val examInfoList = arrayListOf<ExamModal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_previous_years_exam_papers, container, false)
        buttonExamPaper = view.findViewById(R.id.btUploadExamPaper)
        examfstore  = FirebaseFirestore.getInstance()
        loadingBar  = view.findViewById(R.id.examBar)
        doubleBounce = DoubleBounce()
        loadingBar.setIndeterminateDrawable(doubleBounce)
        loadingBar.visibility = View.GONE
        examRecyclerView = view.findViewById(R.id.recyclerExam)
        examLayoutManager = LinearLayoutManager(context)
        updateRecyclerView()
        buttonExamPaper.setOnClickListener{
            pickExamPaper()
        }
        return view
    }

    private fun updateRecyclerView() {
        loadingBar.visibility   = View.VISIBLE
        examfstore.collection("PreviousExamPapers").get()
            .addOnSuccessListener { querySnapshot ->
                loadingBar.visibility = View.GONE
                if (!querySnapshot.isEmpty) {

                    val examsList = querySnapshot.documents
                    for(i in examsList)
                    {
                        val examObject = ExamModal(
                            i.get("examName").toString(),
                            i.get("examYear").toString(),
                            i.get("examLink").toString(),
                            i.get("examUploadDate").toString()
                        )
                        examInfoList.add(examObject)
                        examAdapter = ExamRecyclerAdapter(context as Activity,examInfoList)
                        examAdapter.notifyDataSetChanged()
                        examRecyclerView.adapter = examAdapter
                        examRecyclerView.layoutManager = examLayoutManager
                        examRecyclerView.addItemDecoration(
                            DividerItemDecoration(
                                examRecyclerView.context,
                                (examLayoutManager as LinearLayoutManager).orientation
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
    private fun pickExamPaper() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "application/pdf"
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            filePath = data.data!!
            val intent = Intent(context, UploadAndView::class.java)
            intent.putExtra("aim","upload")
            intent.putExtra("value", "examPapers")
            intent.putExtra("filepath", filePath.toString())
            startActivity(intent)
        }
    }
}