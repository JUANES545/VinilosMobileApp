package com.example.vinilosmobileapp.model.dto

data class ArtistCreateDTO(
    val name: String,
    val image: String,
    val description: String,
    val birthDate: String
)

data class PrizeCreateDTO(
    val organization: String,
    val name: String,
    val description: String
)
