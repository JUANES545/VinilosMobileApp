package com.example.vinilosmobileapp.ui.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.ArtistDetail
import com.example.vinilosmobileapp.model.Prize
import com.example.vinilosmobileapp.repository.ArtistRepository

class DetailArtistViewModel : ViewModel() {
    private val artistRepository = ArtistRepository()

    private val _artist = MutableLiveData<ArtistDetail?>()
    val artist: LiveData<ArtistDetail?> = _artist

    private val _prizes = MutableLiveData<List<Prize>>()
    val prizes: LiveData<List<Prize>> = _prizes

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchArtist(artistId: Int) {
        artistRepository.getArtistDetail(artistId,
            onSuccess = {
                _artist.value = it
                fetchPrizes() // Fetch prizes when artist details are successfully loaded
                _error.value = null
            },
            onError = {
                _artist.value = null
                _error.value = it
            })
    }

    private fun fetchPrizes() {
        artistRepository.getPrizes(
            onSuccess = { _prizes.value = it },
            onError = { _error.value = it }
        )
    }
}
