package com.example.studentasisstant.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.studentasisstant.Activity.VerifyUser
import com.example.studentasisstant.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {
    private lateinit var profileName             : TextInputEditText
    private lateinit var profileEmail            : TextInputEditText
    private lateinit var profileNumber           : TextInputEditText
    private lateinit var profilePassword         : TextInputEditText
    private lateinit var profileConfirmPassword  : TextInputEditText
    private lateinit var signUp                  : Button
    private lateinit var progressLayout          : RelativeLayout
    private lateinit var signupAuth              : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        profileName = view.findViewById(R.id.sign_up_name_editText)
        profileEmail = view.findViewById(R.id.sign_up_email_editText)
        profileNumber = view.findViewById(R.id.sign_up_phone_number_editText)
        profilePassword = view.findViewById(R.id.sign_up_password_editText)
        profileConfirmPassword = view.findViewById(R.id.sign_up_confirm_password_editText)
        signUp = view.findViewById(R.id.btSignUp)
        progressLayout = view.findViewById(R.id.sign_up_progress_bar)
        signupAuth = FirebaseAuth.getInstance()
        signUp.setOnClickListener {
            onSetup()
        }
        return view
    }
    fun onSetup()
    {
        val name = profileName.text.toString()
        val email = profileEmail.text.toString()
        val pass = profilePassword.text.toString()
        val phonenumber = profileNumber.text.toString()
        if (TextUtils.isEmpty(name)) {
            profileName.error = "Name is Required"
            return
        }
        else
            if (TextUtils.isEmpty(email)) {
                profileEmail.error = "Email is Required."
                return
            }
            else
                if (TextUtils.isEmpty(phonenumber)) {
                    profileNumber.error = "Number is Required"
                }
                else
                    if (TextUtils.isEmpty(pass)) {
                        profilePassword.error = "Password is Required."
                        return
                    }
                    else
                        if (pass.length < 6) {
                            profilePassword.error = "Password Must be >= 6 Characters"
                            return
                        }
                        else
                            if(pass != profileConfirmPassword.text.toString())
                            {
                                Toast.makeText(context,"Password and Confirm password should be same",
                                    Toast.LENGTH_LONG).show()
                            }
                            else
                            {
                                progressLayout.visibility = View.VISIBLE
                                val intent = Intent(context, VerifyUser::class.java)
                                intent.putExtra("ActivityName","Registration")
                                intent.putExtra("Name",name)
                                intent.putExtra("Email",email)
                                intent.putExtra("Number",phonenumber)
                                intent.putExtra("Password",pass)
                                startActivity(intent)
                                progressLayout.visibility = View.GONE
                            }
    }
}