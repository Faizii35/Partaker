package com.it.partaker.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.it.partaker.R
import com.it.partaker.activities.AddPostActivity
import com.it.partaker.activities.LoginActivity
import com.it.partaker.adapter.DonorAdapter
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var adapter : DonorAdapter

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

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val manager = LinearLayoutManager(requireContext())
//        rvHFDonor.layoutManager = manager
//        val adapter = DonorAdapter()
//        rvHFDonor.adapter = adapter

        val donRef = FirebaseDatabase.getInstance().reference.child("donations")


        val manager = LinearLayoutManager(activity)
        rvHFDonor.layoutManager = manager

        adapter = DonorAdapter(requireContext())
        rvHFDonor.adapter = adapter

        donRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val donationList = mutableListOf<Donation>()
                    for(data in snapshot.children)
                    {
                        val donation = data.getValue(Donation::class.java)
                        donation?.let {
                            donationList.add(it)
                        }
                    }
                    adapter.setDonations(donationList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

//        view.fa_btn_HF_add_donation.setOnClickListener {
//            val intent = Intent(context, AddPostActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//        }
//        val donationList : List<Donation>? = null
//        val recyclerViewDonationPost: RecyclerView
//        recyclerViewDonationPost = view.findViewById(R.id.rvHFDonor)
//        recyclerViewDonationPost.setHasFixedSize(true)

        

    }


    fun getDonation(){
//        donRef.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    val donationList = mutableListOf<Donation>()
//                    for(data in snapshot.children)
//                    {
//                        val donation = data.getValue(Donation::class.java)
//                        donation?.let {
//                            donationList.add(it)
//                        }
//                    }
//
//                    adapter.setDonations(donationList)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }

}