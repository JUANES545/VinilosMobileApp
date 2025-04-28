package com.example.vinilosmobileapp.model.dto

data class CommentCreateDTO(
    val description: String,
    val rating: Int,
    val collector: CollectorReferenceDTO
)

data class CollectorReferenceDTO(
    val id: Int
)
