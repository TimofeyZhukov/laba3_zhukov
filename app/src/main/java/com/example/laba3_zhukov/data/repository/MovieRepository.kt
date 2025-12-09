package com.example.laba3_zhukov.data.repository

import android.content.Context
import android.util.Log
import com.example.laba3_zhukov.data.models.Actor
import com.example.laba3_zhukov.data.models.Movie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MovieRepository {

    private var movies: List<Movie> = emptyList()

    fun loadMovies(context: Context): List<Movie> {
        Log.d("MOVIE_REPO", "Загрузка фильмов...")

        if (movies.isNotEmpty()) {
            Log.d("MOVIE_REPO", "Фильмы уже в кэше: ${movies.size}")
            return movies
        }

        try {
            Log.d("MOVIE_REPO", "Читаем assets/movies.json")
            val jsonString = context.assets.open("movies.json")
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

            val type = object : TypeToken<List<Movie>>() {}.type
            val parsed: List<Movie>? = Gson().fromJson(jsonString, type)

            movies = if (parsed != null && parsed.isNotEmpty()) {
                parsed
            } else {
                Log.w("MOVIE_REPO", "JSON пуст или не содержит фильмов — возвращаем mock")
                getMockMovies()
            }

            Log.d("MOVIE_REPO", "Загружено фильмов: ${movies.size}")

        } catch (e: Exception) {
            Log.e("MOVIE_REPO", "Ошибка при чтении JSON: ${e.message}")
            movies = getMockMovies()
            Log.d("MOVIE_REPO", "Используем mock-данные: ${movies.size}")
        }

        return movies
    }

    fun getMovies(context: Context): List<Movie> {
        if (movies.isEmpty()) loadMovies(context)
        return movies
    }

    fun getMovieById(context: Context, id: Int): Movie? {
        if (movies.isEmpty()) loadMovies(context)
        val found = movies.find { it.id != null && it.id == id }
        Log.d("MOVIE_REPO", "getMovieById($id) -> ${if (found != null) "найден" else "не найден"}")
        return found
    }

    fun getMovieByTitle(context: Context, title: String?): Movie? {
        if (title.isNullOrBlank()) return null
        if (movies.isEmpty()) loadMovies(context)
        val q = title.trim().lowercase()
        val exact = movies.find { (it.titleRu ?: "").lowercase() == q || (it.titleEn ?: "").lowercase() == q }
        if (exact != null) return exact
        return movies.find {
            (it.titleRu ?: "").lowercase().contains(q) || (it.titleEn ?: "").lowercase().contains(q)
        }
    }

    fun getMoviesByGenre(context: Context, genre: String): List<Movie> {
        if (movies.isEmpty()) loadMovies(context)
        return movies.filter { (it.genres ?: emptyList()).contains(genre) }
    }

    fun getFavoriteMovies(context: Context): List<Movie> {
        if (movies.isEmpty()) loadMovies(context)
        val favoriteIds = try {
            com.example.laba3_zhukov.data.repository.SharedPrefManager(context).getFavoriteMovieIds()
        } catch (e: Exception) {
            Log.e("MOVIE_REPO", "Ошибка чтения избранных: ${e.message}")
            emptyList()
        }
        return movies.filter { favoriteIds.contains(it.id) }
    }

    private fun getMockMovies(): List<Movie> {
        return listOf(
            Movie(
                id = 1,
                titleRu = "Интерстеллар",
                titleEn = "Interstellar",
                year = 2014,
                genresRu = listOf("Драма", "Научная фантастика"),
                genresEn = listOf("Drama", "Science Fiction"),
                rating = 8.6,
                posterPath = "poster_interstellar",
                actors = listOf(
                    Actor(nameRu = "Мэттью МакКонахй", nameEn = "Matthew McConaughey", photo = "actor_matthew"),
                    Actor(nameRu = "Энн Хэтэуэй", nameEn = "Anne Hathaway", photo = "actor_anne"),
                    Actor(nameRu = "Джессика Честейн", nameEn = "Jessica Chastain", photo = "actor_jessica")
                ),
                descriptionRu = "Когда засуха, пыльные бури и вымирание растений приводят человечество к продовольственному кризису, коллектив исследователей и учёных отправляется сквозь червоточину (которая предположительно соединяет области пространства-времени через большое расстояние) в путешествие, чтобы превзойти прежние ограничения для космических путешествий человека и найти планету с подходящими для человечества условиями.",
                descriptionEn = "When droughts, dust storms, and plant extinctions lead humanity to a food crisis, a team of explorers and scientists travels through a wormhole (which presumably connects regions of space-time across great distances) to surpass the previous limitations for human space travel and find a planet with suitable conditions for humanity."
            ),
            Movie(
                id = 2,
                titleRu = "Начало",
                titleEn = "Inception",
                year = 2010,
                genresRu = listOf("Боевик", "Фантастика"),
                genresEn = listOf("Action", "Sci-Fi"),
                rating = 8.8,
                posterPath = "poster_inception",
                actors = listOf(
                    Actor(nameRu = "Леонардо ДиКаприо", nameEn = "Leonardo DiCaprio", photo = "actor_leo"),
                    Actor(nameRu = "Джозеф Гордон-Левитт", nameEn = "Joseph Gordon-Levitt", photo = "actor_joseph")
                ),
                descriptionRu = "Доминик Кобб и его напарник Артур — «извлекатели». Они занимаются корпоративным шпионажем с помощью экспериментальной военной технологии, позволяющей проникать в подсознание жертвы через мир снов.",
                descriptionEn = "Dominic Cobb and his partner Arthur are \"extractors.\" They engage in corporate espionage using experimental military technology that allows them to enter a victim's subconscious through the dream world."
            ),
            Movie(
                id = 3,
                titleRu = "Матрица",
                titleEn = "The Matrix",
                year = 1999,
                genresRu = listOf("Боевик", "Научная фантастика"),
                genresEn = listOf("Action", "Science Fiction"),
                rating = 8.7,
                posterPath = "poster_matrix",
                actors = listOf(
                    Actor(nameRu = "Киану Ривз", nameEn = "Keanu Reeves", photo = "actor_keanu"),
                    Actor(nameRu = "Лоренс Фишбёрн", nameEn = "Laurence Fishburne", photo = "actor_laurence")
                ),
                descriptionRu = "Нео узнаёт, что привычный мир — иллюзия, созданная разумными машинами для порабощения человечества. Планета погружена в вечный сумрак, города лежат в руинах. Люди порабощены машинами, которые используют их для производства энергии.",
                descriptionEn = "Neo learns that the familiar world is an illusion created by intelligent machines to enslave humanity. The planet is plunged into eternal darkness, and cities lie in ruins. People are enslaved by machines that use them to produce energy."
            )
        )
    }

    fun clearCache() {
        movies = emptyList()
    }
}