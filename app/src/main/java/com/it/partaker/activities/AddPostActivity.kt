package com.it.partaker.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.it.partaker.R
import com.it.partaker.classes.Donation
import com.it.partaker.classes.User
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_profile.*

class AddPostActivity : AppCompatActivity() {

    private var donationReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null
    private var imageUri : Uri? = null
    private val RequestCode = 438
    private var url: String = "https://firebasestorage.googleapis.com/v0/b/partaker-1fa76.appspot.com/o/download.png?alt=media&token=f4982ae7-c87e-4c19-8cfd-8f2ad26ba8ff"

    override fun onCreate(savedInstanceState: Bundle?) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        donationReference = FirebaseDatabase.getInstance().reference.child("donation").child(firebaseUser?.uid.toString())
        storageRef = FirebaseStorage.getInstance().reference.child("Post Images")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        ivAddPostImage.setOnClickListener {
            pickImage()
        }

        btnAddPostPost.setOnClickListener {

            val donationName: String = etAddPostDonationName.text.toString()
            val donationDesc: String  = etAddPostDonationDesc.text.toString()
            val donationImage = url
            val donationDonor : String = firebaseUser!!.uid
            val donationStatus: String = "Approval Required"
            val donationAssigned : String = "Pending"

            when{
                TextUtils.isEmpty(donationName) -> Toast.makeText(this,"Donation Name is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(donationDesc) -> Toast.makeText(this,"Donation Description is Required", Toast.LENGTH_SHORT).show()
                else -> {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Posting")
                    progressDialog.setMessage("Please Wait It May Take A While")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    val donation = Donation(donationName,donationDesc,donationImage,donationDonor,donationStatus,donationAssigned)
                    val donationHashMap = HashMap<String, Any>()
                    donationHashMap["name"] = donationName
                    donationHashMap["desc"] = donationDesc
                    donationHashMap["image"] = donationImage
                    donationHashMap["donor"] = firebaseUser!!.uid
                    donationHashMap["status"] = donationStatus
                    donationHashMap["assigned"] = donationAssigned


                    donationReference!!.updateChildren(donationHashMap).addOnCompleteListener { it ->
                        if (it.isSuccessful) {

                         Toast.makeText(this, "Donation Post Added", Toast.LENGTH_LONG).show()
                            //Progress Dialog Dismiss
                            progressDialog.dismiss()

                        } //End If Update Children
                        else {
                            Toast.makeText(this,"Donation Post Upload Unsuccessful: " + it.exception!!.toString(),Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        } // End Else Set Value Function
                    } // End Set Value Function
                } // End Else Body of When Block
            } // End When Block
        } // End Function Button Register
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
        val progressBar = ProgressDialog(this@AddPostActivity)
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
                    url = it.result.toString()
                    val addOnCompleteListener = fileRef.downloadUrl.addOnCompleteListener { it1: Task<Uri> ->
                        if (it1.isSuccessful) {
                            url = it1.result.toString()
                            progressBar.dismiss()
                        }
                        else{
                            Toast.makeText(this, "Error: "+ it.exception.toString(), Toast.LENGTH_LONG).show()
                            progressBar.dismiss()
                        } // End Else Upload Task Complete Listener
                    } // End Download Url On Complete Listener
                } // End If Upload Task is Successful
            } // End Upload Task Complete Listener
        } // End If Image Uri is Not Equals To Null
    } // End Upload Image Database Function
}
