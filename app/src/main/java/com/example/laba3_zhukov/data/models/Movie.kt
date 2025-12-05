package com.example.laba3_zhukov.data.models

data class Movie(
    val id: Int? = null,
    val titleRu: String? = null,
    val titleEn: String? = null,
    val year: Int? = null,
    val genres: List<String>? = null,
    val genresRu: List<String>? = null,
    val genresEn: List<String>? = null,
    val rating: Double = 0.0,
    val posterPath: String? = null,
    val actors: List<Actor>? = null,
    val descriptionRu: String? = null,
    val descriptionEn: String? = null
)