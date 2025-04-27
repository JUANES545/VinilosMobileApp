package com.example.vinilosmobileapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.Album
import com.example.vinilosmobileapp.repository.AlbumRepository

class HomeViewModel : ViewModel() {
    private val repository = AlbumRepository()

    val albums: LiveData<List<Album>?> = repository.getAlbums()
}
