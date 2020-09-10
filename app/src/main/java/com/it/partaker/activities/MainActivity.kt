package com.it.partaker.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.it.partaker.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
//                        loadFragment(ParentHomeFragment())
                        closeDrawer()
                    true
                }
                R.id.nav_profile -> {
//                    loadFragment(ProfileFragment())
                    closeDrawer()

                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)

                    true
                }
                R.id.nav_myDonations -> {
//                    loadFragment(CompletedCampaignsFragment())
                    closeDrawer()
                    Toast.makeText(this, "My Donations", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_aboutApp -> {
//                    loadFragment(ActiveCampaignsFragment())
                    closeDrawer()
                    Toast.makeText(this, "About App", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_reviewApp -> {
//                    loadFragment(UpcomingCampaignsFragment())
                    closeDrawer()
                    Toast.makeText(this, "Review App", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_shareApp -> {
//                    val shareIntent = Intent()
//                    shareIntent.action = Intent.ACTION_SEND
//                    shareIntent.type = "text/plain"
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Poliofy")
//                    startActivity(Intent.createChooser(shareIntent, null))
                    closeDrawer()
                    Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Are you sure?")
                        setPositiveButton("Yes") { _, _ ->
                                FirebaseAuth.getInstance().signOut()
                            Toast.makeText(this@MainActivity, "Logout", Toast.LENGTH_SHORT).show()
                        }
                        setNegativeButton("Cancel") { _, _ ->
                            Toast.makeText(this@MainActivity, "Process Cancelled", Toast.LENGTH_SHORT).show()

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
    }

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
