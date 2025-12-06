package com.example.laba3_zhukov.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3_zhukov.R
import com.example.laba3_zhukov.MainActivity
import com.example.laba3_zhukov.data.repository.MovieRepository
import com.example.laba3_zhukov.data.repository.SharedPrefManager
import com.example.laba3_zhukov.ui.adapters.MovieAdapter

class FavoritesFragment : Fragment() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var goToHomeButton: Button
    private lateinit var backButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FAVORITES", "onCreateView вызван")
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FAVORITES", "onViewCreated вызван")

        try {
            favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView)
            emptyTextView = view.findViewById(R.id.emptyTextView)
            goToHomeButton = view.findViewById(R.id.goToHomeButton)
            backButton = view.findViewById(R.id.backButton)

            if (!::favoritesRecyclerView.isInitialized || !::emptyTextView.isInitialized ||
                !::goToHomeButton.isInitialized || !::backButton.isInitialized) {
                throw IllegalStateException("Не удалось найти все элементы макета")
            }

            Log.d("FAVORITES", "Все элементы инициализированы")
            loadFavorites()
            setupButtons()

        } catch (e: Exception) {
            Log.e("FAVORITES", "Критическая ошибка в onViewCreated: ${e.message}", e)
            showErrorMessage("Ошибка загрузки экрана избранного")
        }
    }

    private fun showErrorMessage(message: String) {
        view?.let {
            val errorText = TextView(requireContext())
            errorText.text = message
            errorText.textSize = 16f
            errorText.setPadding(16, 16, 16, 16)
            (it as? ViewGroup)?.addView(errorText)
        }
    }

    private fun loadFavorites() {
        try {
            val sharedPrefManager = SharedPrefManager(requireContext())
            val favoriteMovies = MovieRepository.getFavoriteMovies(requireContext())

            if (favoriteMovies.isEmpty()) {
                emptyTextView.text = getString(R.string.no_favorites)
                emptyTextView.visibility = View.VISIBLE
                goToHomeButton.visibility = View.VISIBLE
                favoritesRecyclerView.visibility = View.GONE

                Log.d("FAVORITES", "Нет избранных фильмов")
            } else {
                emptyTextView.visibility = View.GONE
                goToHomeButton.visibility = View.GONE
                favoritesRecyclerView.visibility = View.VISIBLE

                val adapter = MovieAdapter(
                    favoriteMovies,
                    { movie -> onMovieClick(movie) },
                    requireContext()
                )

                favoritesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                favoritesRecyclerView.adapter = adapter

                Log.d("FAVORITES", "Показано ${favoriteMovies.size} избранных фильмов")
            }

        } catch (e: Exception) {
            Log.e("FAVORITES", "Ошибка загрузки избранного: ${e.message}", e)
            emptyTextView.text = getString(R.string.no_favorites)
            emptyTextView.visibility = View.VISIBLE
            goToHomeButton.visibility = View.VISIBLE
            favoritesRecyclerView.visibility = View.GONE
        }
    }

    private fun setupButtons() {
        goToHomeButton.setOnClickListener {
            try {
                val mainActivity = activity as? MainActivity
                if (mainActivity != null) {
                    mainActivity.showHomeFragment()
                    val bottomNav = mainActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav)
                    bottomNav.selectedItemId = R.id.homeFragment
                } else {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment())
                        .commit()
                }
            } catch (e: Exception) {
                Log.e("FAVORITES", "Ошибка навигации: ${e.message}")
            }
        }

        backButton.setOnClickListener {
            try {
                val mainActivity = activity as? MainActivity
                if (mainActivity != null) {
                    mainActivity.showHomeFragment()
                    val bottomNav = mainActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav)
                    bottomNav.selectedItemId = R.id.homeFragment
                } else {
                    if (!requireActivity().supportFragmentManager.popBackStackImmediate()) {
                        requireActivity().onBackPressed()
                    }
                }
            } catch (e: Exception) {
                Log.e("FAVORITES", "Ошибка при нажатии назад: ${e.message}")
            }
        }
    }

    private fun onMovieClick(movie: com.example.laba3_zhukov.data.models.Movie) {
        try {
            val intent = android.content.Intent(requireContext(),
                com.example.laba3_zhukov.MovieDetailActivity::class.java)
            intent.putExtra("MOVIE_ID", movie.id)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("FAVORITES", "Ошибка открытия деталей фильма: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            loadFavorites()
        } catch (e: Exception) {
            Log.e("FAVORITES", "Ошибка в onResume: ${e.message}")
        }
    }
}