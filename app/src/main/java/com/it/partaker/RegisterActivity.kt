package com.it.partaker

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var refUsers: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        //Intent For Sign In Activity
        tvRegisterSignIn.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java )
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        var gender = "Male"
        var registerAs = "Donor"

        //Gender Radio Button Value
        rgGender.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                gender = radio.text.toString()
            })

        //Register As Radio Button Value
        rgRegisterAs.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                registerAs = radio.text.toString()
            })

        //Register Button Click
        btnRegister.setOnClickListener {
            val fullName: String = etFullName.text.toString().trim()
            val phoneNumber: String  = etPhoneNumber.text.toString().trim()
            val city: String  = etCity.text.toString().trim()
            val email: String  = etEmail.text.toString().trim()
            val password: String  = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val bloodGroup = "Not Known"
            val profilePic = "https://firebasestorage.googleapis.com/v0/b/partaker-1fa76.appspot.com/o/download.png?alt=media&token=f4982ae7-c87e-4c19-8cfd-8f2ad26ba8ff"

            if(fullName==""|| phoneNumber==""||city==""||email==""||password=="" || confirmPassword != password)
            {
                Toast.makeText(this,"Please Fill Required Data Correctly",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this,"Hey There",Toast.LENGTH_SHORT).show()

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful) {

                       mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                           if(it.isSuccessful){

                               val userID = mAuth.currentUser!!.uid
                               refUsers = FirebaseDatabase.getInstance().reference.child("users").child(userID)

                               val user = User(userID,fullName,phoneNumber,city,email,password,gender,registerAs,bloodGroup,profilePic)
                               refUsers.setValue(user).addOnCompleteListener { it ->
                                   if (it.isSuccessful)
                                   {
                                       Toast.makeText(this,"Registered Successfully. Please Check Your Email For Verification",
                                           Toast.LENGTH_SHORT).show()
                                       val intent = Intent(this, LoginActivity::class.java )
                                       intent.flags = FLAG_ACTIVITY_CLEAR_TOP
                                       startActivity(intent)
                                   } //End If Update Children
                               } // End Update Children Function
                           } // End If Send Verification Email
                           else{
                               Toast.makeText(this, "Verification Email Not Sent", Toast.LENGTH_SHORT).show()
                           } // End Else Send Verification Email
                       } // End Send Verification Email Function
                    } // End If Create User
                    else
                    {
                        Toast.makeText(this,"Unsuccessful",Toast.LENGTH_SHORT).show()
                    } // End Else Create User
                } // End Create User Function
            } // End Else Checking Values
        } // End Function Button Register
    } // End On Create
} // End Class Activity