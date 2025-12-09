package com.example.laba3_zhukov

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3_zhukov.data.models.Movie
import com.example.laba3_zhukov.data.repository.MovieRepository
import com.example.laba3_zhukov.data.repository.SharedPrefManager
import com.example.laba3_zhukov.ui.adapters.ActorAdapter
import java.util.Locale

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var movie: Movie
    private lateinit var prefs: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        prefs = SharedPrefManager(this)
        MovieRepository.loadMovies(this)

        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        val movieTitle = intent.getStringExtra("MOVIE_TITLE")
        val actorsRecycler = findViewById<RecyclerView>(R.id.actorsRecyclerView)
        actorsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val actorsList = listOf(
            Pair("Мэттью МакКонахи", R.drawable.actor_matthew),
            Pair("Энн Хэтэуэй", R.drawable.actor_anne),
            Pair("Джессика Честейн", R.drawable.actor_jessica)
        )

        actorsRecycler.adapter = ActorAdapter(actorsList)
        var found: Movie? = null
        if (movieId != -1) {
            found = MovieRepository.getMovieById(this, movieId)
        }
        if (found == null && !movieTitle.isNullOrEmpty()) {
            found = MovieRepository.getMovies(this).find {
                (it.titleRu ?: "").equals(movieTitle, ignoreCase = true) ||
                        (it.titleEn ?: "").equals(movieTitle, ignoreCase = true)
            }
        }

        if (found == null) {
            Toast.makeText(this, getString(R.string.toast_movie_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        movie = found
        bindViews()
    }

    private fun bindViews() {
        val posterImageView = findViewById<ImageView>(R.id.posterImageView)
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val subtitleTextView = findViewById<TextView>(R.id.subtitleTextView)
        val ratingTextView = findViewById<TextView>(R.id.ratingTextView)
        val overviewLabel = findViewById<TextView>(R.id.overviewLabel)
        val overviewTextView = findViewById<TextView>(R.id.overviewTextView)
        val castLabel = findViewById<TextView>(R.id.castLabel)
        val actorsRecyclerView = findViewById<RecyclerView>(R.id.actorsRecyclerView)
        val trailerButton = findViewById<Button>(R.id.trailerButton)
        val favoriteButton = findViewById<ImageButton>(R.id.favoriteButton)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val extraInfoTextView = findViewById<TextView>(R.id.extraInfoTextView)
        val langCode = prefs.getLanguage()
        val isRussian = (langCode == "ru")
        val conf = Configuration(resources.configuration)
        val locale = Locale(langCode)
        conf.setLocale(locale)
        val localizedCtx = createConfigurationContext(conf)
        overviewLabel.text = localizedCtx.getString(R.string.overview)
        castLabel.text = localizedCtx.getString(R.string.cast)
        trailerButton.text = localizedCtx.getString(R.string.see_trailer)
        titleTextView.text = if (isRussian) movie.titleRu ?: movie.titleEn else movie.titleEn ?: movie.titleRu
        val genresList = if (isRussian) {
            movie.genresRu ?: movie.genres ?: emptyList()
        } else {
            movie.genresEn ?: movie.genres ?: emptyList()
        }
        val genresString = genresList.joinToString(", ")
        subtitleTextView.text = listOfNotNull(movie.year?.toString(), if (genresString.isNotBlank()) genresString else null)
            .joinToString(" • ")

        ratingTextView.text = String.format("%.1f", movie.rating)
        overviewTextView.text = if (isRussian) movie.descriptionRu ?: movie.descriptionEn ?: "" else movie.descriptionEn ?: movie.descriptionRu ?: ""
        extraInfoTextView.visibility = View.GONE
        extraInfoTextView.text = ""

        try {
            val posterId = resources.getIdentifier(movie.posterPath, "drawable", packageName)
            if (posterId != 0) posterImageView.setImageResource(posterId)
            else posterImageView.setImageResource(R.drawable.ic_movie_placeholder)
        } catch (e: Exception) {
            posterImageView.setImageResource(R.drawable.ic_movie_placeholder)
        }

        actorsRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val actors = movie.actors?.map {
            val actorName = if (isRussian) {
                it.nameRu ?: it.nameEn ?: ""
            } else {
                it.nameEn ?: it.nameRu ?: ""
            }
            val resId = resources.getIdentifier(it.photo ?: "ic_actor_placeholder", "drawable", packageName)
            Pair(actorName, if (resId != 0) resId else R.drawable.ic_actor_placeholder)
        } ?: emptyList()
        actorsRecyclerView.adapter = ActorAdapter(actors)

        trailerButton.setOnClickListener {
            val titleForSearch = if (isRussian) movie.titleRu ?: movie.titleEn else movie.titleEn ?: movie.titleRu
            val trailerTerm = localizedCtx.getString(R.string.trailer_search_term)
            val url = "https://www.youtube.com/results?search_query=" + Uri.encode("$titleForSearch $trailerTerm")
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        backButton.setOnClickListener { finish() }

        favoriteButton.setOnClickListener {
            val id = movie.id ?: -1
            if (id != -1) {
                if (prefs.isFavorite(id)) {
                    prefs.removeFavorite(id)
                    Toast.makeText(this, localizedCtx.getString(R.string.toast_removed_favorite), Toast.LENGTH_SHORT).show()
                } else {
                    prefs.addFavorite(id)
                    Toast.makeText(this, localizedCtx.getString(R.string.toast_added_favorite), Toast.LENGTH_SHORT).show()
                }
                updateFavoriteIcon(favoriteButton, id)
            }
        }

        updateFavoriteIcon(favoriteButton, movie.id ?: -1)
    }

    private fun updateFavoriteIcon(btn: ImageButton, id: Int) {
        if (id == -1) {
            btn.setImageResource(R.drawable.ic_favorite_border)
            return
        }
        if (prefs.isFavorite(id)) {
            btn.setImageResource(R.drawable.ic_favorite)
        } else {
            btn.setImageResource(R.drawable.ic_favorite_border)
        }
    }
}