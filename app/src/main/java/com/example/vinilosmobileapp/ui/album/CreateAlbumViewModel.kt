package com.example.vinilosmobileapp.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.dto.AlbumCreateDTO
import com.example.vinilosmobileapp.repository.AlbumRepository

class CreateAlbumViewModel : ViewModel() {

    private val repository = AlbumRepository()

    val createAlbumResult: LiveData<Boolean> = repository.createAlbumResult

    fun createAlbum(albumCreateDTO: AlbumCreateDTO) {
        repository.createAlbum(albumCreateDTO)
    }
}
