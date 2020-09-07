package com.it.partaker

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        tvLoginSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java )
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            if(email == "" || password == "")
            {
                Toast.makeText(this, "Please Fill Required Data",Toast.LENGTH_SHORT).show()
            } // End If Check Validation
            else
            {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (mAuth.currentUser!!.isEmailVerified) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                            etLoginEmail.setText("")
                            etLoginPassword.setText("")

                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this,"Please Verify Your Email First!", Toast.LENGTH_SHORT).show()
                        } // End Else Is Verified Email
                    } // End If SignIn
                    else {
                        Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
                    } // End Else SignIn
                } // End SignIn Function
            } // End Else Check Validation
        } // End Button Login Function
    } // End OnCreate Function
} // End Activity