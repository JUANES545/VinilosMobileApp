package com.example.vinilosmobileapp.ui.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import com.example.vinilosmobileapp.repository.ArtistRepository

class CreateArtistViewModel : ViewModel() {
    private val repository = ArtistRepository()

    private val _createResult = MutableLiveData<Boolean>()
    val createResult: LiveData<Boolean> get() = _createResult

    fun createArtist(dto: ArtistCreateDTO) {
        repository.createArtist(
            dto,
            onSuccess = { _createResult.value = true },
            onError = { _createResult.value = false }
        )
    }
}
