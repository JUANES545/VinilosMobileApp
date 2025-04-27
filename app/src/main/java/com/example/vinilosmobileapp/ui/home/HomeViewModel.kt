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

    init {
        fetchAlbums()
    }

    fun fetchAlbums() {
        repository.getAlbums().observeForever { fetchedAlbums ->
            _albums.value = fetchedAlbums
        }
    }
}
