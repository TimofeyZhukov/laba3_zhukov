package com.example.laba3_zhukov.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3_zhukov.R
import com.example.laba3_zhukov.data.models.Movie
import com.example.laba3_zhukov.data.repository.SharedPrefManager
import com.example.laba3_zhukov.MovieDetailActivity

class MovieAdapter(
    private val movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val sharedPrefManager = SharedPrefManager(context.applicationContext)

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterImageView: ImageView = itemView.findViewById(R.id.moviePoster)
        val titleTextView: TextView = itemView.findViewById(R.id.movieTitle)
        val ratingBar: RatingBar = itemView.findViewById(R.id.movieRatingBar)
        val ratingTextView: TextView = itemView.findViewById(R.id.movieRatingText)
        val yearTextView: TextView = itemView.findViewById(R.id.movieYear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        try {
            val movie = movies[position]
            val isRussian = sharedPrefManager.isRussian()
            try {
                val posterId = holder.itemView.context.resources.getIdentifier(
                    movie.posterPath,
                    "drawable",
                    holder.itemView.context.packageName
                )

                if (posterId != 0) {
                    holder.posterImageView.setImageResource(posterId)
                } else {
                    holder.posterImageView.setImageResource(R.drawable.ic_movie_placeholder)
                }
            } catch (e: Exception) {
                holder.posterImageView.setImageResource(R.drawable.ic_movie_placeholder)
            }

            holder.titleTextView.text = if (isRussian) movie.titleRu else movie.titleEn
            holder.ratingBar.rating = (movie.rating / 2).toFloat()
            holder.ratingTextView.text = String.format("%.1f", movie.rating)
            holder.yearTextView.text = movie.year.toString()
            holder.itemView.setOnClickListener {
                val intent = Intent(context, MovieDetailActivity::class.java)
                intent.putExtra("MOVIE_ID", movie.id ?: -1)
                intent.putExtra("MOVIE_TITLE", movie.titleRu ?: movie.titleEn)
                context.startActivity(intent)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = movies.size
}