package com.example.studentasisstant.Activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.example.studentasisstant.FirebaseCloudMessaging.FirebaseMessage
import com.example.studentasisstant.FirebaseCloudMessaging.FirebaseNotification
import com.example.studentasisstant.R
import com.github.barteksc.pdfviewer.PDFView
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.abt.FirebaseABTesting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class UploadAndView : AppCompatActivity() {
   lateinit var fcmHelper : FirebaseNotification
    private lateinit var pdfView : PDFView
    private lateinit var next: Button
    private lateinit var upload: Button
    private lateinit var buttonTime: Button
    private lateinit var enterTitle: TextInputEditText
    private lateinit var enterLayout: TextInputLayout
    private lateinit var yearLayout: TextInputLayout
    private lateinit var enterYear: TextInputEditText
    private lateinit var currentDate: TextView

    private lateinit var fstore: FirebaseFirestore
    private lateinit var db: DocumentReference
    private lateinit var loadingBar: SpinKitView
    private lateinit var doubleBounce: Sprite
    private lateinit var storageReference: StorageReference

    private lateinit var count: String
    private lateinit var pdfFile: Uri
    private lateinit var date: String
    private lateinit var time: String

    private var day = 0
    private var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_and_view)
        fcmHelper = FirebaseNotification(this)
        next = findViewById(R.id.btNext)
        upload = findViewById(R.id.btUpload)
        buttonTime = findViewById(R.id.btTime)
        currentDate = findViewById(R.id.txtDate)
        enterTitle = findViewById(R.id.etTitle)
        enterLayout = findViewById(R.id.EnterTitle)
        loadingBar = findViewById(R.id.LoadingBar)
        yearLayout = findViewById(R.id.EnterYear)
        enterYear = findViewById(R.id.etExamYear)
        doubleBounce = DoubleBounce()
        loadingBar.visibility = View.GONE
        loadingBar.setIndeterminateDrawable(doubleBounce)
        fstore = FirebaseFirestore.getInstance()
        db = fstore.collection("users").document("count")
        if (intent != null) {
            if (intent.getStringExtra("aim") == "open") {
                if (intent.getStringExtra(("class"))=="yes")
                {
                   val classUrl = intent.getStringExtra("link").toString()
                    val intent = Intent(Intent.ACTION_VIEW,Uri.parse(classUrl))
                    startActivity(intent)
                }
                else
                {
                    pdfView = findViewById(R.id.pdfView)
                    pdfView.visibility = View.VISIBLE
                    PRDownloader.initialize(this)
                    val pdfLink = intent.getStringExtra("link")
                    val directoryPath = getRootDirPath(this)
                    Log.d("path", directoryPath)
                    downloadPdfFromInternet(pdfLink!!, directoryPath, "assd.pdf")
                }
            }
            else {
                when (intent.getStringExtra("value")) {
                    "notice" -> {
                        uploadNotices()
                        enterLayout.visibility = View.VISIBLE
                        enterTitle.visibility = View.VISIBLE
                        enterLayout.hint = "Enter The Title of Notice :"
                        upload.visibility = View.VISIBLE
                        getCurrentDate()
                        pdfFile = Uri.parse(intent.getStringExtra("filepath"))
                        upload.setOnClickListener {
                            if (enterTitle.text.toString() != "" && currentDate.text != "") {
                                uploadingNotice()
                                loadingBar.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(
                                    this@UploadAndView,
                                    "Date And Title Both Needed to Upload the Notice",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    "assignment" -> {
                        uploadAssignment()
                        enterLayout.visibility = View.VISIBLE
                        enterTitle.visibility = View.VISIBLE
                        enterLayout.hint = "Enter the Title of Assignment"
                        pdfFile = Uri.parse(intent.getStringExtra("filepath"))
                        next.visibility = View.VISIBLE
                        upload.visibility = View.VISIBLE
                        next.text = "Select Due Date"
                        next.setOnClickListener {
                            if (enterTitle.text.toString() == "") {
                                Toast.makeText(
                                    this@UploadAndView,
                                    "Please Enter Title for Assignment First",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                val calendar: Calendar = Calendar.getInstance()
                                day = calendar.get(Calendar.DAY_OF_MONTH)
                                month = calendar.get(Calendar.MONTH)
                                year = calendar.get(Calendar.YEAR)
                                val datePickerDialog = DatePickerDialog(
                                    this,
                                    { view, year, month, day ->
                                        currentDate.visibility = View.VISIBLE
                                        currentDate.text = "$year-${month + 1}-$day"
                                    }, year, month, day
                                )
                                datePickerDialog.setMessage("Select The Due Date of Assignment")
                                datePickerDialog.show()
                            }
                        }
                        upload.setOnClickListener {
                            if (enterTitle.text.toString() != "" && currentDate.text != "") {
                                uploadingAssignment()
                                loadingBar.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(
                                    this@UploadAndView,
                                    "Date And Title Both Needed to Upload the Notice",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    }
                    "examPapers" -> {
                        uploadExamPaper()
                        enterLayout.visibility = View.VISIBLE
                        enterTitle.visibility = View.VISIBLE
                        enterLayout.hint = "Enter the Name of Exam :"
                        upload.visibility = View.VISIBLE
                        enterYear.visibility = View.VISIBLE
                        yearLayout.visibility = View.VISIBLE
                        yearLayout.hint = "Enter the Year of Exam :"
                        getCurrentDate()
                        pdfFile = Uri.parse(intent.getStringExtra("filepath"))
                        upload.setOnClickListener {
                            if (enterTitle.text.toString() != "" && currentDate.text != "" && enterYear.text.toString() != "") {
                                uploadingExamPaper()
                                loadingBar.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(
                                    this@UploadAndView,
                                    "Date And Title Both Needed to Upload the Notice",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    "classes" -> {
                        uploadClassSchedules()
                        enterLayout.visibility = View.VISIBLE
                        enterTitle.visibility = View.VISIBLE
                        enterLayout.hint = "Enter The Name of Class :"
                        yearLayout.visibility = View.VISIBLE
                        enterYear.visibility = View.VISIBLE
                        yearLayout.hint = "Enter The Link : "
                        currentDate.visibility = View.VISIBLE
                        upload.visibility = View.VISIBLE
                        next.visibility = View.VISIBLE
                        buttonTime.visibility = View.VISIBLE
                        next.text = "Select The Date"
                        buttonTime.text = "Select The Time"
                        val calendar: Calendar = Calendar.getInstance()
                        next.setOnClickListener {
                            day = calendar.get(Calendar.DAY_OF_MONTH)
                            month = calendar.get(Calendar.MONTH)
                            year = calendar.get(Calendar.YEAR)
                            val datePickerDialog = DatePickerDialog(
                                this@UploadAndView,
                                { _, year, month, day ->
                                    currentDate.text = "$year-${month + 1}-$day"
                                    date = currentDate.text.toString()
                                }, year, month, day
                            )
                            datePickerDialog.show()
                        }
                        buttonTime.setOnClickListener {
                            val time = date
                            hour = calendar.get((Calendar.HOUR))
                            minute = calendar.get(Calendar.MINUTE)
                            val timePicker = TimePickerDialog(
                                this@UploadAndView,
                                { _, hour, minute ->
                                    currentDate.text = "$time::$hour-$minute"
                                },
                                hour,
                                minute,
                                true
                            )
                            timePicker.show()
                        }
                        upload.setOnClickListener {
                            if (enterTitle.text.toString() != "" && enterYear.text.toString() != "" && currentDate.text.toString() != "")
                                uploadingClassSchedules()
                            loadingBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
    private fun uploadExamPaper() {
        db.addSnapshotListener { task, error ->
            if (error != null) {
                Log.d("FirestoreError", "Fetching Data from firebase firestore is unsuccessful")
            }
            if (task != null) {
                count = task.getString("examValue").toString()
            }
        }
    }
    private fun uploadClassSchedules() {
        db.addSnapshotListener { task, error ->
            if (error != null) {
                Log.d("FirestoreError", "Fetching Data from firebase firestore is unsuccessful")
            }
            if (task != null) {
                count = task.getString("classesValue").toString()
            }
        }
    }

    private fun uploadingClassSchedules() {
        val obj = mutableMapOf<String,String>()
        obj["className"] = enterTitle.text.toString()
        obj["classLink"] = enterYear.text.toString()
        obj["classTime"] = currentDate.text.toString()
        fstore.collection("ClassSchedules").document().set(obj).addOnSuccessListener {
            Log.d("onSuccess","Class Schedule Successfully Created")
            fcmHelper.sendToAll("New class scheduled",enterTitle.text.toString()+" is Uploaded")
            loadingBar.visibility = View.GONE
            val intent = Intent(this@UploadAndView,OptionsActivity::class.java)
            intent.putExtra("FragmentName","3")
            startActivity(intent)
            finish()
        }
    }

    private fun uploadAssignment()
    {
        db.addSnapshotListener { task, error ->
            if (error != null) {
                Log.d("FirestoreError", "Fetching Data from firebase firestore is unsuccessful")
            }
            if (task != null) {
                count = task.getString("assignmentValue").toString()
            }
        }
    }
    private fun uploadNotices() {
        db.addSnapshotListener { task, error ->
            if (error != null) {
                Log.d("FirestoreError", "Fetching Data from firebase firestore is unsuccessful")
            }
            if (task != null) {
                count = task.getString("noticeValue").toString()
            }
        }
    }
    private fun uploadingNotice() {
        storageReference =
            FirebaseStorage.getInstance().reference.child("Notices/notice$count.pdf")
        storageReference.putFile(pdfFile).addOnSuccessListener {
            Log.d("UploadStatus","Successfully Upload Notice")
            storageReference.downloadUrl.addOnSuccessListener {
                val url = it
                val noticeValue = (count.toInt()+1).toString()
                val user = mutableMapOf<String,String>()
                user["noticeValue"] = noticeValue
                db.update(user as Map<String, Any>).addOnSuccessListener {
                    Log.d("DataSetStatus","Successfully Data Set")
                }
                val user1 = mutableMapOf<String,Any>()
                user1["noticeTitle"] = enterTitle.text.toString()
                user1["noticeLink"]  = url.toString()
                user1["noticeDate"]  = currentDate.text.toString()
                user1["noticeId"]    = noticeValue.toInt()
                user1["uploadedBy"]     = FirebaseAuth.getInstance().currentUser?.uid!!
                fstore.collection("Notices").document().set(user1)
                    .addOnSuccessListener {
                        Log.d("DataSetStatus","Successfully Data Set")
                        fcmHelper.sendToAll("New Notice Uploaded",enterTitle.text.toString()+" is Uploaded")
                        loadingBar.visibility = View.GONE
                        val intent = Intent(this@UploadAndView,OptionsActivity::class.java)
                        intent.putExtra("FragmentName","1")
                        startActivity(intent)
                        finish()
                    }
            }
        }
    }
    private fun uploadingExamPaper() {
        storageReference =
            FirebaseStorage.getInstance().reference.child("PreviousYearPapers/paper$count.pdf")
        storageReference.putFile(pdfFile).addOnSuccessListener {
            Log.d("UploadStatus","Successfully Upload Exam Paper")
            storageReference.downloadUrl.addOnSuccessListener {
                val url = it
                val examPaperValue = (count.toInt()+1).toString()
                val user = mutableMapOf<String,String>()
                user["examValue"] = examPaperValue
                db.update(user as Map<String, Any>).addOnSuccessListener {
                    Log.d("DataSetStatus","Successfully Data Set")
                }
                val user1 = mutableMapOf<String,Any>()
                user1["examName"]   = enterTitle.text.toString()
                user1["examLink"]    = url.toString()
                user1["examYear"]         = enterYear.text.toString()
                user1["examPaperId"]     = examPaperValue.toInt()
                user1["examUploadDate"] = currentDate.text.toString()
                user1["uploadedBy"]     = FirebaseAuth.getInstance().currentUser?.uid!!
                fstore.collection("PreviousExamPapers").document().set(user1)
                    .addOnSuccessListener {
                        Log.d("DataSetStatus","Successfully Data Set")
                        fcmHelper.sendToAll("Previous Year Exam Paper",enterTitle.text.toString()+" is Uploaded")
                        loadingBar.visibility = View.GONE
                       val intent = Intent(this@UploadAndView,OptionsActivity::class.java)
                        intent.putExtra("FragmentName","6")
                        startActivity(intent)
                        finish()
                    }
            }
        }
    }

    private fun uploadingAssignment() {
        storageReference =
            FirebaseStorage.getInstance().reference.child("Assignment/assignment$count.pdf")
        storageReference.putFile(pdfFile).addOnSuccessListener {
            Log.d("UploadStatus","Successfully Upload Notice")
            storageReference.downloadUrl.addOnSuccessListener {
                val url = it
                val assignmentValue = (count.toInt()+1).toString()
                val user = mutableMapOf<String,String>()
                user["assignmentValue"] = assignmentValue
                db.update(user as Map<String, Any>).addOnSuccessListener {
                    Log.d("DataSetStatus","Successfully Data Set")
                }
                val user1 = mutableMapOf<String,Any>()
                user1["assignmentTitle"] = enterTitle.text.toString()
                user1["assignmentLink"]  = url.toString()
                user1["assignmentDueDate"]  = currentDate.text.toString()
                user1["assignmentId"]    = assignmentValue.toInt()
                fstore.collection("Assignments").document().set(user1)
                    .addOnSuccessListener {
                        Log.d("DataSetStatus","Successfully Data Set")
                        fcmHelper.sendToAll("New Assignment",enterTitle.text.toString() + "is Uploaded")
                        loadingBar.visibility = View.GONE
                        val intent = Intent(this@UploadAndView,OptionsActivity::class.java)
                        intent.putExtra("FragmentName","4")
                        startActivity(intent)
                        finish()
                    }
            }
        }
    }
    private fun getCurrentDate() {
        currentDate.visibility = View.VISIBLE
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z", Locale.getDefault())
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        currentDate.text  = currentDateAndTime
    }
    private fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            )[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }
    private fun downloadPdfFromInternet(url: String, dirPath: String, fileName: String) {
        PRDownloader.download(
            url,
            dirPath,
            fileName
        ).build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Toast.makeText(this@UploadAndView, "downloadComplete", Toast.LENGTH_LONG)
                        .show()
                    val downloadedFile = File(dirPath, fileName)
                    Log.d("filepath",downloadedFile.toString())
                    showPdfFromFile(downloadedFile)
                }

                override fun onError(error: Error?) {
                    Toast.makeText(
                        this@UploadAndView,
                        "Error in downloading file : $error",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            })
    }
    private fun showPdfFromFile(file: File) {
        pdfView.fromFile(file)
            .password(null)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onPageError { page, _ ->
                Toast.makeText(
                    this@UploadAndView,
                    "Error at page: $page", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }
}

