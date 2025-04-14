package com.example.vinilosmobileapp.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateAlbumViewModel : ViewModel() {

    private val _albumCreated = MutableLiveData<Boolean>()
    val albumCreated: LiveData<Boolean> = _albumCreated

    fun submitAlbum(name: String, year: Int, artist: String, genre: String) {
        // Here you'd call your use case or repository
        // Fake success callback
        _albumCreated.value = true
    }
}
