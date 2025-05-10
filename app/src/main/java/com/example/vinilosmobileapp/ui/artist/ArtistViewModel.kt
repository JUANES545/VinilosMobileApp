package com.example.vinilosmobileapp.ui.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.repository.ArtistRepository

class ArtistViewModel : ViewModel() {
    private val repository = ArtistRepository()

    private val _artists = MutableLiveData<List<Artist>?>()
    val artists: LiveData<List<Artist>?> get() = _artists

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _prizeSeedingResult = MutableLiveData<Boolean>()
    val prizeSeedingResult: LiveData<Boolean> get() = _prizeSeedingResult

    init {
        fetchArtists()
    }

    fun fetchArtists() {
        repository.getArtistsWithErrorHandler(
            onSuccess = {
                _artists.value = it
                _errorMessage.value = null
            },
            onError = {
                _artists.value = null
                _errorMessage.value = it
            }
        )
    }

    fun seedPrizes() {
        repository.seedPrizes(
            onSuccess = { _prizeSeedingResult.value = true },
            onError = { _prizeSeedingResult.value = false }
        )
    }

}
