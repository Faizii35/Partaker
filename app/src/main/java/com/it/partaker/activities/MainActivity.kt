package com.it.partaker.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.it.partaker.R
import com.it.partaker.R.id.nav_host_fragment
import com.it.partaker.classes.User
import com.it.partaker.fragments.HomeFragment
import com.it.partaker.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var mAuth: FirebaseAuth? = null
    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null
    private var imageUri : Uri? = null
    private val RequestCode = 438

    var selectedFragment : Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

//        val spinBlood = findViewById<Spinner>(R.id.spnEditProfileBloodGroup)
//        val bloodGroup = arrayOf("Not Known","A+","A-","B+","B-","AB+","AB-","O+","O-")
//        val ArrayAdp = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, bloodGroup)
//        spinBlood.adapter = ArrayAdp



        //Data Retrieval From Firebase in Profile Fragment
        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    tvMainActivityNavHeaderName.text = user!!.getFullName()
                    tvMainActivityNavHeaderEmail.text = user.getEmail()
                    Glide.with(this@MainActivity)
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.default_profile_pic)
                        .transform(CircleCrop())
                        .into(ivMainActivityNavHeaderProfile)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Value Event Listener Failed: ",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

//         //Change Password Text Click Listener
//        tvProfileChangePassword.setOnClickListener {
//            profileLayout.visibility = View.GONE
//            fragmentChangePassword.visibility = View.VISIBLE
//        }

        // Delete Account Button Click
//        btnProfileDelete.setOnClickListener {
//            AlertDialog.Builder(this).apply {
//                setTitle("Are you sure?")
//                setPositiveButton("Yes") { _, _ ->
//                    val user = FirebaseAuth.getInstance().currentUser!!
//                    user.delete().addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                Toast.makeText(this@MainActivity,"Account Deleted", Toast.LENGTH_LONG).show()
//                            } else {
//                                Toast.makeText(this@MainActivity, "Error: ${task.exception}", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    startActivity(intent)
//                }
//                setNegativeButton("Cancel") { _, _ ->
//                    Toast.makeText(this@MainActivity, "Process Cancelled", Toast.LENGTH_SHORT).show()
//                }
//            }.create().show()
//        }

        //Profile Pic Set On Click Listener
//        ivProfilePic.setOnClickListener {
//            pickImage()
//        }



        //Drawer Related Code of Main Activity Lies Below
        showEmployeeNavigationDrawer()

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
//                    if (user?.token != null && user?.role == 1) {
//                        toolbar.title = "Worker"
//                        loadFragment(HomeFragment())
//                        closeDrawer()
//                    } else if (user?.token != null && user?.role == 0) {
                    toolbar.title = "Donor"
                    supportFragmentManager.beginTransaction().replace(nav_host_fragment,HomeFragment()).commit()
                    closeDrawer()
                    true
                }
                R.id.nav_profile -> {
                    selectedFragment = ProfileFragment()
//                    loadFragment(ProfileFragment())
                    toolbar.title = "Profile"
                    closeDrawer()
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_myDonations -> {
//                    loadFragment(CompletedCampaignsFragment())
                    toolbar.title = "My Donations"
                    closeDrawer()
                    Toast.makeText(this, "My Donations", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_aboutApp -> {
                    toolbar.title = "About App"
                    closeDrawer()
                    Toast.makeText(this, "About App", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_reviewApp -> {
                    toolbar.title = "Review App"

                        val uri = Uri.parse("market://details?id=$packageName")
                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        try {
                            startActivity(goToMarket)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
                        }
                    closeDrawer()
                    Toast.makeText(this, "Review App", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_shareApp -> {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Partaker")
                    startActivity(Intent.createChooser(shareIntent, null))
                    toolbar.title = "Share App"
                    closeDrawer()
                    Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Are you sure?")
                        setPositiveButton("Yes") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            Toast.makeText(this@MainActivity, "Logout", Toast.LENGTH_SHORT).show()
                        }
                        setNegativeButton("Cancel") { _, _ ->
                            Toast.makeText(
                                this@MainActivity,
                                "Process Cancelled",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }.create().show()
                    closeDrawer()
                    true
                }

                else -> {
                    Toast.makeText(this, "Item Not Selected", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
        if(selectedFragment!=null){
            supportFragmentManager.beginTransaction().replace(nav_host_fragment,selectedFragment!!).commit()
        }
    }

//    private fun pickImage() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(intent, RequestCode)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data?.data != null) {
//            imageUri = data.data
//            uploadImageDatabase()
//        }
//    }
//
//    private fun uploadImageDatabase() {
//        val progressBar = ProgressDialog(this)
//        progressBar.setTitle("Upload Image")
//        progressBar.setCanceledOnTouchOutside(false)
//        progressBar.setMessage("Image is Uploading. Please Wait A While")
//        progressBar.show()
//
//        if(imageUri!= null) {
//            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
//            val uploadTask: StorageTask<*>
//            uploadTask = fileRef.putFile(imageUri!!)
//
//            uploadTask.addOnCompleteListener {
//                if (it.isSuccessful) {
//                    var url = it.result.toString()
//                    val addOnCompleteListener = fileRef.downloadUrl.addOnCompleteListener { it1: Task<Uri> ->
//                        if (it1.isSuccessful) {
//                            url = it1.result.toString()
//                            val mapProfilePic = HashMap<String, Any>()
//                            mapProfilePic["profilePic"] = url
//                            userReference!!.updateChildren(mapProfilePic)
//                            progressBar.dismiss()
//                        }
//                        else{
//                            Toast.makeText(this, "Error: "+ it.exception.toString(),Toast.LENGTH_LONG).show()
//                            progressBar.dismiss()
//                        } // End Else Upload Task Complete Listener
//                    } // End Download Url On Complete Listener
//                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
//                    val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
//                    userRef.addValueEventListener(object : ValueEventListener {
//                        override fun onDataChange(p0: DataSnapshot) {
//                            if (p0.exists()) {
//                                val user = p0.getValue<User>(User::class.java)
//                                Glide.with(this@MainActivity)
//                                    .load(user!!.getProfilePic())
//                                    .placeholder(R.drawable.default_profile_pic)
//                                    .transform(CircleCrop())
//                                    .into(ivProfilePic)
//                            }
//                        } // End On Data Change Function
//                        override fun onCancelled(p0: DatabaseError) {
//                            TODO("Not yet implemented")
//                        } // End On Data Cancel Function
//                    }) // End Add Value Event Listener
//                } // End If Upload Task is Successful
//            } // End Upload Task Complete Listener
//        } // End If Image Uri is Not Equals To Null
//    } // End Upload Image Database Function




    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(nav_host_fragment, fragment)
            .addToBackStack("")
            .commit()
    }

    //Drawer Related Code of Main Activity Lies Below
    private fun showEmployeeNavigationDrawer() {
        setSupportActionBar(toolbar)
        val drawerToggle: androidx.appcompat.app.ActionBarDrawerToggle =
            object : androidx.appcompat.app.ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                (R.string.navigation_drawer_open),
                (R.string.navigation_drawer_close)
            ) {

            }
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

    }

    private fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }
}
