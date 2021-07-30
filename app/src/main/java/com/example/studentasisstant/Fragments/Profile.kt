package com.example.studentasisstant.Fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.studentasisstant.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class Profile : Fragment() {
    private lateinit var profilePic        : ImageView
    private lateinit var profileName       : EditText
    private lateinit var profileNumber     : EditText
    private lateinit var profileEmail      : EditText
    private lateinit var profileSem        : EditText
    private lateinit var profileDep        : EditText
    private lateinit var profileRoll       : EditText
    private lateinit var profileNameText   : TextView
    private lateinit var profileNumberText : TextView
    private lateinit var profileEmailText  : TextView
    private lateinit var profileSemText    : TextView
    private lateinit var profileDepText    : TextView
    private lateinit var profileRollText   : TextView
    private lateinit var saveProfile       : Button
    private lateinit var db                : DocumentReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profileName        = view.findViewById(R.id.etProfileName)
        profileNumber      = view.findViewById(R.id.etProfileNumber)
        profileEmail       = view.findViewById(R.id.etEmail)
        profilePic         = view.findViewById(R.id.imgProfilePic)
        profileDep         = view.findViewById(R.id.etProfileDepartment)
        profileSem         = view.findViewById(R.id.etProfileSemester)
        profileSemText     = view.findViewById(R.id.txtProfileSemester)
        profileRoll        = view.findViewById(R.id.etProfileRollNumber)
        profileRollText    = view.findViewById(R.id.txtProfileRollNumber)
        profileDepText     = view.findViewById(R.id.txtProfileDepartment)
        profileEmailText   = view.findViewById(R.id.txtEmail)
        profileNumberText  = view.findViewById(R.id.txtProfileNumber)
        profileNameText    = view.findViewById(R.id.txtProfileName)
        saveProfile        = view.findViewById(R.id.btSaveProfile)
        val userid = FirebaseAuth.getInstance().currentUser?.uid
        db = FirebaseFirestore.getInstance().collection("userProfiles").document(userid!!)
        saveProfile.setOnClickListener {
            val user = mutableMapOf<String, String>()
            user["profileName"] = profileName.text.toString()
            user["profileNumber"] = profileNumber.text.toString()
            user["profileEmail"] = profileEmail.text.toString()
            user["profileRollNumber"] = profileRoll.text.toString()
            user["profileSemester"] = profileSem.text.toString()
            user["profileDepartment"] = profileDep.text.toString()
            db.set(user as Map<String, Any>).addOnSuccessListener {
                Log.d("onSuccess", "Success in loading data")
                Toast.makeText(context, "Successfully Updated The Data", Toast.LENGTH_LONG).show()
            }
        }
            db.addSnapshotListener{ value,error->
                if(error!=null)
                {
                    Log.d("valueFetch","error shows")
                }
                profileName.text = Editable.Factory.getInstance().newEditable(value?.getString("profileName"))
                profileNumber.text = Editable.Factory.getInstance().newEditable(value?.getString("profileNumber"))
                profileEmail.text = Editable.Factory.getInstance().newEditable(value?.getString("profileEmail"))
                profileRoll.text = Editable.Factory.getInstance().newEditable(value?.getString("profileRollNumber"))
                profileSem.text = Editable.Factory.getInstance().newEditable(value?.getString("profileSemester"))
                profileDep.text = Editable.Factory.getInstance().newEditable(value?.getString("profileDepartment"))
            }
        return view
    }
}