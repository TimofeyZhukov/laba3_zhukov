package com.example.laba3_zhukov.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SimpleStringAdapter(private val items: List<String>) : RecyclerView.Adapter<SimpleStringAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tv: TextView = v.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tv.text = items[position]
    }

    override fun getItemCount(): Int = items.size
}