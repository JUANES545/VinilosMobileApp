package com.example.vinilosmobileapp.model

data class AlbumDetail(
    val id: Int,
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String,
    val tracks: List<Track>?,
    val comments: List<Comment> = emptyList()
)

data class Track(
    val id: Int,
    val name: String,
    val duration: String?
)

data class Comment(
    val id: Int,
    val description: String,
    val collector: Collector?,
    val rating: Int
)

data class Collector(
    val id: Int,
    val name: String
)
