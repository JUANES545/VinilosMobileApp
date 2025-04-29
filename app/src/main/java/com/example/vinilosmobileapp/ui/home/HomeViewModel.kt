package com.example.vinilosmobileapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.Album
import com.example.vinilosmobileapp.repository.AlbumRepository

class HomeViewModel : ViewModel() {

    private val repository = AlbumRepository()

    private val _albums = MutableLiveData<List<Album>?>()
    val albums: LiveData<List<Album>?> get() = _albums

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        fetchAlbums()
    }

    fun fetchAlbums() {
        repository.getAlbumsWithErrorHandler(
            onSuccess = { fetchedAlbums ->
                _albums.value = fetchedAlbums
                _errorMessage.value = null
            },
            onError = { error ->
                _albums.value = null
                _errorMessage.value = error
            }
        )
    }
}
