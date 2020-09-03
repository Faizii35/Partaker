package com.it.partaker

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
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
    private var coverChecker : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user : User? = snapshot.getValue(User::class.java)
                    tvProfileFullNameFB.text = user!!.getFullName()
                    tvProfileBloodGroupFB.text = user.getBloodGroup()
                    tvProfileCityFB.text = user.getCity()
                    tvProfileGenderFB.text = user.getGender()
                    tvProfilePhoneNumberFB.text = user.getPhoneNumber()

                    Picasso.with(applicationContext).load(user.getProfilePic()).into(ivProfilePic)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Error Value Data Change Function",Toast.LENGTH_SHORT).show()
            }
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
        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data?.data != null)
        {
            imageUri = data.data
            Toast.makeText(this, "Updating", Toast.LENGTH_SHORT).show()
            uploadImageDatabase()

        }
    }
    private fun uploadImageDatabase() {
        val progressBar = ProgressDialog(applicationContext)
        progressBar.setMessage("Image is Uploading. Please Wait")
        progressBar.show()

        if(imageUri!= null)
        {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)
            uploadTask.addOnCompleteListener {
                if(it.isSuccessful)
                {
                    val downloadUrl = it.result
                    val url = downloadUrl.toString()

                    val mapProfilePic = HashMap<String, Any>()
                    mapProfilePic["profilePic"] = url
                    userReference!!.updateChildren(mapProfilePic)
                }
            }
        }
    }
}
