package com.example.vinilosmobileapp.model

data class AlbumCreateDTO(
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String
)
