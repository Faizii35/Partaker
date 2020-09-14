package com.it.partaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it.partaker.R
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.rv_hf_donor_item.view.*

class DonorAdapter: RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {
    class DonorViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {}

    var list: List<Donation> = listOf()
    fun setItems(donList: List<Donation>){
        list = donList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_hf_donor_item, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donations = list[position]
        holder.itemView.tvRVHFDonorName.text = donations.name
        holder.itemView.tvRVHFDonorDesc.text = donations.name
        holder.itemView.tvRVHFDonorName.text = donations.name
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}