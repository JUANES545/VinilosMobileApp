package com.example.vinilosmobileapp.model.dto

import com.example.vinilosmobileapp.model.Comment
import com.example.vinilosmobileapp.model.Track

data class AlbumCreateDTO(
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String
)

data class TrackCreateDTO(
    val name: String,
    val duration: String
)
