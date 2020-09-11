package com.it.partaker.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.it.partaker.activities.LoginActivity
import com.it.partaker.classes.User
import kotlinx.android.synthetic.main.fragment_profile.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var mAuth: FirebaseAuth? = null
    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null
    private var imageUri : Uri? = null
    private val RequestCode = 438


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")


        userReference!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    tvProfileFullNameFB.text = user!!.getFullName()
                    tvProfilePhoneNumberFB.text = user.getPhoneNumber()
                    tvProfileCityFB.text = user.getCity()
                    tvProfileBloodGroupFB.text = user.getBloodGroup()
                    tvProfileGenderFB.text = user.getGender()
                    Glide.with(this@ProfileFragment)
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.default_profile_pic)
                        .transform(CircleCrop())
                        .into(ivProfilePic)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,"Value Event Listener Failed: ", Toast.LENGTH_LONG).show()
            }
        })


        //Change Password Text Click Listener
        tvProfileChangePassword.setOnClickListener {
            profileLayout.visibility = View.GONE
            fragmentChangePassword.visibility = View.VISIBLE
        }

        // Delete Account Button Click Listener
        btnProfileDelete.setOnClickListener {
            context?.let { it1 ->
                AlertDialog.Builder(it1).apply {
                    setTitle("Are you sure?")
                    setPositiveButton("Yes") { _, _ ->
                        val user = FirebaseAuth.getInstance().currentUser!!
                        user.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context,"Account Deleted", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Error: ${task.exception}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                    setNegativeButton("Cancel") { _, _ ->
                        Toast.makeText(context, "Process Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }.create().show()
            }
        }

        ivProfilePic.setOnClickListener {
            pickImage()
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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
        val progressBar = ProgressDialog(context)
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
                            Toast.makeText(context, "Error: "+ it.exception.toString(),Toast.LENGTH_LONG).show()
                            progressBar.dismiss()
                        } // End Else Upload Task Complete Listener
                    } // End Download Url On Complete Listener
                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
                    userRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                val user = p0.getValue<User>(User::class.java)
                                Glide.with(this@ProfileFragment)
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

}