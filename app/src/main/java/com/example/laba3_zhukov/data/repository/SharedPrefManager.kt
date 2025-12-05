package com.example.laba3_zhukov.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPrefManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("MovieGuidePrefs", Context.MODE_PRIVATE)

    fun getFavoriteMovieIds(): Set<Int> {
        val favoritesString = prefs.getString("favorites", "") ?: ""
        Log.d("SHARED_PREF", "Получена строка избранного: '$favoritesString'")

        return if (favoritesString.isEmpty()) {
            Log.d("SHARED_PREF", "Строка пуста, возвращаем пустой набор")
            emptySet()
        } else {
            try {
                val ids = favoritesString.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .mapNotNull { it.toIntOrNull() }
                    .toSet()

                Log.d("SHARED_PREF", "Распарсены ID: $ids")
                ids
            } catch (e: Exception) {
                Log.e("SHARED_PREF", "Ошибка парсинга: ${e.message}")
                emptySet()
            }
        }
    }

    fun addFavorite(movieId: Int) {
        try {
            val favorites = getFavoriteMovieIds().toMutableSet()
            Log.d("SHARED_PREF", "До добавления: $favorites, добавляем: $movieId")

            favorites.add(movieId)
            val favoritesString = favorites.joinToString(",")

            Log.d("SHARED_PREF", "Сохраняем: $favoritesString")
            prefs.edit().putString("favorites", favoritesString).apply()
            Log.d("SHARED_PREF", "Успешно сохранено")

        } catch (e: Exception) {
            Log.e("SHARED_PREF", "Ошибка при добавлении в избранное: ${e.message}")
        }
    }

    fun removeFavorite(movieId: Int) {
        try {
            val favorites = getFavoriteMovieIds().toMutableSet()
            Log.d("SHARED_PREF", "До удаления: $favorites, удаляем: $movieId")

            favorites.remove(movieId)
            val favoritesString = favorites.joinToString(",")

            prefs.edit().putString("favorites", favoritesString).apply()
            Log.d("SHARED_PREF", "Успешно удалено")

        } catch (e: Exception) {
            Log.e("SHARED_PREF", "Ошибка при удалении из избранного: ${e.message}")
        }
    }

    fun isFavorite(movieId: Int): Boolean {
        val isFav = getFavoriteMovieIds().contains(movieId)
        Log.d("SHARED_PREF", "Проверка ID $movieId: $isFav")
        return isFav
    }

    fun getLanguage(): String {
        return prefs.getString("language", "ru") ?: "ru"
    }

    fun getLanguageCode(): String {
        return getLanguage()
    }

    fun setLanguage(language: String) {
        prefs.edit().putString("language", language).apply()
    }

    fun isRussian(): Boolean {
        return getLanguage() == "ru"
    }

    fun getTheme(): String {
        return prefs.getString("theme", "light") ?: "light"
    }

    fun setTheme(theme: String) {
        prefs.edit().putString("theme", theme).apply()
    }

    fun isDarkTheme(): Boolean {
        return getTheme() == "dark"
    }
}