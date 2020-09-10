package com.it.partaker.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.it.partaker.R
import com.it.partaker.classes.User
import com.it.partaker.fragments.ChangePasswordFragment
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null
    private var imageUri : Uri? = null
    private val RequestCode = 438

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Load Change Password Fragment
       // supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view_tag,ChangePasswordFragment()).commit()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    tvProfileFullNameFB.text = user!!.getFullName()
                    tvProfilePhoneNumberFB.text = user.getPhoneNumber()
                    tvProfileCityFB.text = user.getCity()
                    tvProfileBloodGroupFB.text = user.getBloodGroup()
                    tvProfileGenderFB.text = user.getGender()

                    Glide.with(this@ProfileActivity)
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.default_profile_pic)
                        .transform(CircleCrop())
                        .into(ivProfilePic)
                }
            }
            override fun onCancelled(p0: DatabaseError) { TODO("Not yet implemented") }
        })

        // Change Password Text Click
        tvProfileChangePassword.setOnClickListener {
            profileLayout.visibility = View.GONE
            fragmentChangePassword.visibility = View.VISIBLE
        }


        // Sign Out Button Click
        btnProfileDelete.setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes") { _, _ ->

                    val user = FirebaseAuth.getInstance().currentUser!!

                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@ProfileActivity,"Account Deleted", Toast.LENGTH_LONG).show()
                            }
                            else
                            {
                                Toast.makeText(this@ProfileActivity, "Error: ${task.exception}", Toast.LENGTH_SHORT).show()

                            }
                        }

                    val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                    intent.flags = FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)

                }
                setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this@ProfileActivity, "Process Cancelled", Toast.LENGTH_SHORT).show()

                }
            }.create().show()
        }


        ivProfilePic.setOnClickListener {
            pickImage()
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUri = data.data
            uploadImageDatabase()
        }
    }
    private fun uploadImageDatabase() {
        val progressBar = ProgressDialog(this)
        progressBar.setTitle("Upload Image")
        progressBar.setCanceledOnTouchOutside(false)
        progressBar.setMessage("Image is Uploading. Please Wait A While")
        progressBar.show()

        if(imageUri!= null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    var url = it.result.toString()

                    val addOnCompleteListener = fileRef.downloadUrl.addOnCompleteListener { it1: Task<Uri> ->
                        if (it1.isSuccessful) {
                            url = it1.result.toString()
                            val mapProfilePic = HashMap<String, Any>()
                            mapProfilePic["profilePic"] = url
                            userReference!!.updateChildren(mapProfilePic)
                            progressBar.dismiss()
                        }
                        else{
                            Toast.makeText(this, "Error: "+ it.exception.toString(),Toast.LENGTH_LONG).show()
                            progressBar.dismiss()
                        } // End Else Upload Task Complete Listener
                    } // End Download Url On Complete Listener

                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
                    userRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                    val user = p0.getValue<User>(User::class.java)

                                    Glide.with(this@ProfileActivity)
                                        .load(user!!.getProfilePic())
                                        .placeholder(R.drawable.default_profile_pic)
                                        .transform(CircleCrop())
                                        .into(ivProfilePic)
                                }
                        } // End On Data Change Function
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        } // End On Data Cancel Function
                    }) // End Add Value Event Listener
                } // End If Upload Task is Successful
            } // End Upload Task Complete Listener
        } // End If Image Uri is Not Equals To Null
    } // End Upload Image Database Function
} // End Activity Class