package com.example.studentasisstant.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.studentasisstant.FirebaseCloudMessaging.FirebaseNotification
import com.example.studentasisstant.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException


class TimeTable : Fragment() {
    private lateinit var storage : StorageReference
    private lateinit var loadingBar: SpinKitView
    private lateinit var doubleBounce: Sprite
    private lateinit var filePath: Uri
    private lateinit var bitmap: Bitmap
    private lateinit var imageTimeTable: ImageView
    private lateinit var uploadImage: Button
    private lateinit var db: DocumentReference
    private lateinit var image: ByteArray
    private lateinit var save : Button
    private lateinit var url : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_table, container, false)
        imageTimeTable = view.findViewById(R.id.imgTimeTable)
        uploadImage = view.findViewById(R.id.btUploadTimeTable)
        storage = FirebaseStorage.getInstance().reference.child("TimeTable/timetable.jpg")
        loadingBar  = view.findViewById(R.id.timetableBar)
        save = view.findViewById(R.id.btSave)
        doubleBounce = DoubleBounce()
        loadingBar.setIndeterminateDrawable(doubleBounce)
        loadingBar.visibility = View.GONE
        db = FirebaseFirestore.getInstance().collection("TimeTable").document("image")
        db.addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("Error in load", "Image Data not loaded")
            }
            if (value != null) {
                url = value.getString("timetableImage").toString()
                Picasso.get().load(url).into(imageTimeTable)
            }
        }
        save.setOnClickListener{

        }
        uploadImage.setOnClickListener {
            loadingBar.visibility = View.VISIBLE
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 100)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data?.data != null) {
            filePath = data.data!!
            try {
                if (Build.VERSION.SDK_INT < 29) {
                  bitmap = MediaStore.Images.Media.getBitmap(
                      context?.contentResolver,filePath
                  )
                    byteArray()
                } else {
                    //val source: ImageDecoder.Source =
                    bitmap = context?.contentResolver?.let {
                        ImageDecoder.createSource(
                            it,
                            filePath
                        )
                    }?.let {
                        ImageDecoder.decodeBitmap(
                            it
                        )
                    }!!
                    byteArray()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun byteArray() {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        image = baos.toByteArray()
        uploadFile()
    }

    private fun uploadFile() {
        storage.putBytes(image).addOnSuccessListener {
            storage.downloadUrl.addOnSuccessListener { it ->
                val url = it
                val obj = mutableMapOf<String,String>()
                obj["timetableImage"] = url.toString()
                obj["uploadedBy"]  = FirebaseAuth.getInstance().currentUser?.uid!!
                db.set(obj).addOnSuccessListener {
                    Log.d("success","Successfully Added Time Table")
                    context?.let { it1 -> FirebaseNotification(it1).sendToAll("New Time Table ","Check The New Table") }
                    loadingBar.visibility = View.GONE
                }
            }
        }
    }
}
