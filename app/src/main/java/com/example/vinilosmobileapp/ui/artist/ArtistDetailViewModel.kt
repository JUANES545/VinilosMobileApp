package com.example.vinilosmobileapp.ui.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.ArtistDetail
import com.example.vinilosmobileapp.repository.ArtistRepository

class ArtistDetailViewModel : ViewModel() {
    private val repo = ArtistRepository()

    private val _artist = MutableLiveData<ArtistDetail?>()
    val artist: LiveData<ArtistDetail?> = _artist

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchArtist(artistId: Int) {
        repo.getArtistDetail(artistId,
            onSuccess = {
                _artist.value = it
                _error.value = null
            },
            onError = {
                _artist.value = null
                _error.value = it
            })
    }
}
