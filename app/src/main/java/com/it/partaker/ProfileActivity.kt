package com.it.partaker

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
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
                    tvProfilePhoneNumberFB.text = user!!.getPhoneNumber()
                    tvProfileCityFB.text = user!!.getCity()
                    tvProfileBloodGroupFB.text = user!!.getBloodGroup()
                    tvProfileGenderFB.text = user!!.getGender()

                    Glide.with(this@ProfileActivity)
                        .load(user!!.getProfilePic())
                        .placeholder(R.drawable.default_profile_pic)
                        .transform(CircleCrop())
                        .into(ivProfilePic)
                }
            }
            override fun onCancelled(p0: DatabaseError) { TODO("Not yet implemented") }
        })

        // Sign Out Button Click
        btnProfileSignOut.setOnClickListener {
            mAuth?.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
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
        progressBar.setMessage("Image is Uploading. Please Wait")
        progressBar.show()

        if(imageUri!= null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    var url = it.result.toString()

                    val addOnCompleteListener = fileRef.downloadUrl.addOnCompleteListener { it1: Task<Uri> ->
                        if (it1.isSuccessful)
                        {
                            url = it1.result.toString()
                            val mapProfilePic = HashMap<String, Any>()
                            mapProfilePic["profilePic"] = url
                            userReference!!.updateChildren(mapProfilePic)
                            progressBar.hide()
                        }
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
                } // End If Upload Tassk is Successful
            } // End Upload Task Complete Listener
        } // End If Image Uri is Not Equals To Null
    } // End Upload Image Database Function
} // End Activity Class
/*
val url = if (photoUrl != null) "$photoUrl?w=360" else null //1
Glide.with(itemView)  //2
    .load(url) //3
    .centerCrop() //4
    .placeholder(R.drawable.ic_image_place_holder) //5
    .error(R.drawable.ic_broken_image) //6
    .fallback(R.drawable.ic_no_image) //7
    .into(itemView.ivPhoto) //8


     Glide.with(this) //1
    .load(profilePicUrl)
    .placeholder(R.drawable.ic_profile_placeholder)
    .error(R.drawable.ic_profile_placeholder)
    .skipMemoryCache(true) //2
    .diskCacheStrategy(DiskCacheStrategy.NONE) //3
    .transform(CircleCrop()) //4
    .into(ivProfile)

     */