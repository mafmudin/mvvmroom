package com.example.mvvmroom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmroom.R
import com.example.mvvmroom.model.LocationData
import kotlinx.android.synthetic.main.rv_item_location.view.*

class LocationAdapter(private val listener: LocationAdapterListener)  : RecyclerView.Adapter<LocationAdapter.Holder>(){
    var list: MutableList<LocationData> = arrayListOf()

    fun updateList(list: MutableList<LocationData>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.rv_item_location, parent, false)) {

        fun bind(data: LocationData, position: Int) {
            with(itemView) {
                tvTitle.text = data.name
                if (data.status == 1){
                    tvStatus.text = String.format("Online Booking Enabled")
                }else{
                    tvStatus.text = ""
                }

                if (position %2 == 0){
                    rootView.setBackgroundResource(R.color.gray)
                }else{
                    rootView.setBackgroundResource(R.color.white)
                }

                itemView.setOnClickListener {
                    listener.onClickList(data, position)
                }
            }
        }
    }
    interface LocationAdapterListener{
        fun onClickList(data: LocationData, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        return Holder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position], position)
    }
}