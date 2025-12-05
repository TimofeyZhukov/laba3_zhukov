package com.example.laba3_zhukov.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3_zhukov.R

class GenresAdapter(
    private val genres: List<String>,
    private var selectedGenre: String?,
    private val onGenreClick: (String) -> Unit
) : RecyclerView.Adapter<GenresAdapter.GenreVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false)
        return GenreVH(v)
    }

    override fun onBindViewHolder(holder: GenreVH, position: Int) {
        val genre = genres[position]
        holder.text.text = genre
        val isSelected = selectedGenre?.equals(genre, ignoreCase = true) == true
        holder.text.setBackgroundResource(R.drawable.genre_bg)
        holder.text.isSelected = isSelected
        holder.text.alpha = if (isSelected) 1.0f else 0.85f

        holder.itemView.setOnClickListener {
            selectedGenre = if (isSelected) null else genre
            notifyDataSetChanged()
            onGenreClick(genre)
        }
    }

    override fun getItemCount(): Int = genres.size

    class GenreVH(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.genreText)
    }
}