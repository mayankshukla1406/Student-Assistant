package com.example.studentasisstant.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studentasisstant.Activity.UploadAndView
import com.example.studentasisstant.Adapter.NoticeRecyclerAdapter
import com.example.studentasisstant.Modal.NoticeModal
import com.example.studentasisstant.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.firestore.FirebaseFirestore


class Notices : Fragment() {
    private val noticeRequestCode = 1000
    private lateinit var noticefstore: FirebaseFirestore
    private lateinit var loadingBar: SpinKitView
    private lateinit var doubleBounce: Sprite
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var uploadNotice: Button
    private lateinit var filePath: Uri
    private lateinit var noticeLayoutManager: RecyclerView.LayoutManager
    private lateinit var noticeAdapter: NoticeRecyclerAdapter
    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var noticeImageLogo : ImageView
    var noticeInfoList = arrayListOf<NoticeModal>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notices, container, false)
        noticeRecyclerView = view.findViewById(R.id.recyclerNotice)
        noticeLayoutManager = LinearLayoutManager(context as Activity)
        loadingBar = view.findViewById(R.id.LoadingBar)
        swipeRefreshLayout = view.findViewById(R.id.SwipeRefresh)
        uploadNotice = view.findViewById(R.id.btUploadNotice)
        noticeImageLogo = view.findViewById(R.id.imgNoticeLogo)
        noticefstore = FirebaseFirestore.getInstance()
        doubleBounce = DoubleBounce()
        loadingBar.visibility = View.GONE
        loadingBar.setIndeterminateDrawable(doubleBounce)
        uploadNotice.setOnClickListener {
            pickNotice()
        }
        updatingRecyclerView()
        return view
    }
    private fun pickNotice() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "application/pdf"
        startActivityForResult(intent, noticeRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == noticeRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            filePath = data.data!!
            val intent = Intent(context, UploadAndView::class.java)
            intent.putExtra("aim","upload")
            intent.putExtra("value", "notice")
            intent.putExtra("filepath", filePath.toString())
            startActivity(intent)
        }
    }
    private fun updatingRecyclerView() {
        loadingBar.visibility = View.VISIBLE
        noticefstore.collection("Notices").get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                loadingBar.visibility = View.GONE
                if (!queryDocumentSnapshots.isEmpty) {

                    val list = queryDocumentSnapshots.documents
                    for (d in list) {
                        // after getting this list we are passing
                        // that list to our object class.
                        val obj = NoticeModal(
                            d.get("noticeTitle").toString(),
                            d.get("noticeLink").toString(),
                            d.get("noticeDate").toString()
                        )
                        noticeInfoList.add(obj)
                        noticeAdapter = NoticeRecyclerAdapter(activity as? Context , noticeInfoList)
                        noticeAdapter.notifyDataSetChanged()
                        noticeRecyclerView.adapter = noticeAdapter
                        noticeRecyclerView.layoutManager = noticeLayoutManager
                        noticeRecyclerView.addItemDecoration(
                            DividerItemDecoration(
                                noticeRecyclerView.context,
                                (noticeLayoutManager as LinearLayoutManager).orientation
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
            noticeAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}



