package com.example.vinilosmobileapp.ui.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import com.example.vinilosmobileapp.repository.ArtistRepository

class CreateArtistViewModel : ViewModel() {
    private val repo = ArtistRepository()
    val createResult: LiveData<Boolean> = repo.createResult
    fun createArtist(dto: ArtistCreateDTO) = repo.createArtist(dto)
}
