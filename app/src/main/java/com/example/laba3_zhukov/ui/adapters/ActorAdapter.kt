package com.example.laba3_zhukov.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3_zhukov.R

class ActorAdapter(private val items: List<Pair<String, Int>>) : RecyclerView.Adapter<ActorAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.actorImage)
        val name: TextView = v.findViewById(R.id.actorName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_actor, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val (actorName, drawableId) = items[position]
        holder.name.text = actorName
        holder.img.contentDescription = actorName
        holder.img.setImageResource(drawableId)
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.img.setImageDrawable(null)
    }
}