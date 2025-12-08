package com.example.laba3_zhukov.ui.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3_zhukov.R
import com.example.laba3_zhukov.data.models.Movie
import com.example.laba3_zhukov.data.repository.MovieRepository
import com.example.laba3_zhukov.data.repository.SharedPrefManager
import com.example.laba3_zhukov.ui.adapters.MovieAdapter

class HomeFragment : Fragment() {

    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var genresRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var prefs: SharedPrefManager
    private var allMovies: List<Movie> = emptyList()
    private var currentFilterGenre: String? = null
    private var currentSearchQuery: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SharedPrefManager(requireContext())

        moviesRecyclerView = view.findViewById(R.id.moviesRecyclerView)
        genresRecyclerView = view.findViewById(R.id.genresRecyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        moviesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        genresRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        loadMovies()
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString()?.trim()
                applyFilters()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadMovies() {
        try {
            MovieRepository.loadMovies(requireContext())
            allMovies = MovieRepository.getMovies(requireContext())
            setupGenres(allMovies)
            applyFilters()
        } catch (e: Exception) {
            Log.e("HOME", "Ошибка загрузки фильмов: ${e.message}", e)
            allMovies = emptyList()
            applyFilters()
        }
    }

    private fun setupGenres(movies: List<Movie>) {
        val isRussian = prefs.isRussian()
        val genres = movies.flatMap { movie ->
            if (isRussian) {
                movie.genresRu ?: movie.genres ?: emptyList()
            } else {
                movie.genresEn ?: movie.genres ?: emptyList()
            }
        }
        val distinctGenres = genres.map { it.trim() }.filter { it.isNotEmpty() }.distinct().sorted()
        if (distinctGenres.isEmpty()) {
            genresRecyclerView.visibility = View.GONE
            return
        } else {
            genresRecyclerView.visibility = View.VISIBLE
        }
        val adapter = com.example.laba3_zhukov.ui.adapters.GenresAdapter(distinctGenres, currentFilterGenre) { genre ->
            currentFilterGenre = if (currentFilterGenre == genre) null else genre
            applyFilters()
        }
        genresRecyclerView.adapter = adapter
    }

    private fun applyFilters() {
        val isRussian = prefs.isRussian()
        val filteredByGenre = if (currentFilterGenre.isNullOrEmpty()) {
            allMovies
        } else {
            val genreToMatch = currentFilterGenre!!.trim()
            allMovies.filter { movie ->
                val movieGenres = if (isRussian) {
                    movie.genresRu ?: movie.genres ?: emptyList()
                } else {
                    movie.genresEn ?: movie.genres ?: emptyList()
                }
                movieGenres.any { it.equals(genreToMatch, ignoreCase = true) }
            }
        }

        val finalFilteredMovies = if (currentSearchQuery.isNullOrEmpty()) {
            filteredByGenre
        } else {
            val query = currentSearchQuery!!.lowercase()
            filteredByGenre.filter { movie ->
                val titleRu = movie.titleRu?.lowercase() ?: ""
                val titleEn = movie.titleEn?.lowercase() ?: ""
                titleRu.contains(query) || titleEn.contains(query)
            }
        }

        val adapter = MovieAdapter(finalFilteredMovies, { movie -> openMovieDetail(movie) }, requireContext())
        moviesRecyclerView.adapter = adapter
    }

    private fun openMovieDetail(movie: Movie) {
        try {
            val intent = android.content.Intent(requireContext(), com.example.laba3_zhukov.MovieDetailActivity::class.java)
            intent.putExtra("MOVIE_ID", movie.id)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("HOME", "Ошибка открытия деталей: ${e.message}", e)
        }
    }
}