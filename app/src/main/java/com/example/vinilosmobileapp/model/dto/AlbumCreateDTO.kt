package com.example.vinilosmobileapp.model.dto

data class AlbumCreateDTO(
    val name:        String,
    val cover:       String,
    val releaseDate: String,
    val description: String,
    val genre:       String,
    val recordLabel: String
)

data class TrackCreateDTO(
    val name:     String,
    val duration: String
)
