package com.example.studentasisstant.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.studentasisstant.Activity.ForgetPassword
import com.example.studentasisstant.Activity.MainActivity
import com.example.studentasisstant.FirebaseCloudMessaging.TokenUpdate
import com.example.studentasisstant.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize


class LoginFragment : Fragment() {
    lateinit var firebaseAuth         : FirebaseAuth
    lateinit var fstore               : FirebaseFirestore
    lateinit var db                   : DocumentReference
    lateinit var email                : TextInputEditText
    lateinit var password             : TextInputEditText
    lateinit var login                : Button
    lateinit var forgetPassword       : Button
    lateinit var googleSignInOptions  : GoogleSignInOptions
    private lateinit var animation    : Animation
    lateinit var signInButton         : ImageButton
    lateinit var mGoogleSignInClient  : GoogleSignInClient
    private val RC_SIGN_IN = 1011
    private val TAG        = "Google Sign In"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view      = inflater.inflate(R.layout.fragment_login, container, false)
        animation     = AnimationUtils.loadAnimation(context,R.anim.recyclerview)
        signInButton         = view.findViewById(R.id.imgbtGoogleSignIn)
        login                = view.findViewById(R.id.btLogin)
        forgetPassword       = view.findViewById(R.id.btforgetPassword)
        val app              = Firebase.initialize(requireContext())
        firebaseAuth         = FirebaseAuth.getInstance(app!!)
        fstore               = FirebaseFirestore.getInstance()
        email                = view.findViewById(R.id.login_editText_email)
        password             = view.findViewById(R.id.login_password_editText)
        email.animation = animation
        password.animation = animation
        signInButton.animation = animation
        login.animation = animation
        forgetPassword.animation = animation
        login.setOnClickListener {
            val Email = email.text.toString()
            val Password = password.text.toString()
            when {
                TextUtils.isEmpty(Email) -> {
                    email.error = "Email is Required."
                }
                TextUtils.isEmpty(Password) -> {
                    password.error = "Password is Required."
                }
                Password.length < 6 -> {
                    password.error = "Password Must be >= 6 Characters"
                }
                else -> {
                    firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(context, "LoginSuccessful", Toast.LENGTH_LONG).show()
                            tokenUpdate()
                        }
                    }
                }
            }
        }
        signInButton.setOnClickListener{
            createRequest()
        }
        forgetPassword.setOnClickListener{
            val intent = Intent(context, ForgetPassword::class.java)
            startActivity(intent)
        }
        return view
    }
    private fun createRequest()
    {
        googleSignInOptions   = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient   = context?.let { GoogleSignIn.getClient(it, googleSignInOptions) }!!
        signIn()
    }
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GmailID", "firebaseAuthWithGoogle:$account")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener{task ->
                if (task.isSuccessful) {
                    val acct = GoogleSignIn.getLastSignedInAccount(context)
                    if (acct != null) {
                        val personName = acct.displayName!!
                        val personGivenName = acct.givenName!!
                        val personFamilyName = acct.familyName!!
                        val personEmail = acct.email!!
                        val personId = acct.id!!
                        val personPhoto: Uri = acct.photoUrl!!
                        val UserID = firebaseAuth.currentUser?.uid
                        db = fstore.collection("users").document(UserID.toString())
                        val user = mutableMapOf<String, String>()
                        user["profileName"]   = personName
                        user["GivenName"]     = personGivenName
                        user["FamilyName"]    = personFamilyName
                        user["profileEmail"]  = personEmail
                        user["personID"]      = personId
                        user["profileImage"]  = personPhoto.toString()
                        user["cashback"]      = "0"
                        user["withdrawid"]    = "0"
                        user["UPIID"]         = ""
                        user["valueImages"]         = "0"
                        db.set(user).addOnSuccessListener {
                            Log.d("tagProfile", "onSuccess: user Profile is created for $UserID")
                        }
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        tokenUpdate()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // ...
                    Toast.makeText(context, "Google SIgn In Failed", Toast.LENGTH_LONG).show()
                }
            }
    }
    fun tokenUpdate() {
        context?.let { TokenUpdate(it).token() }
    }
}