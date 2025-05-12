package com.example.vinilosmobileapp.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.AlbumDetail
import com.example.vinilosmobileapp.repository.AlbumRepository

class DetailAlbumViewModel : ViewModel() {

    private val repository = AlbumRepository()

    private val _albumDetail = MutableLiveData<AlbumDetail?>()
    val albumDetail: LiveData<AlbumDetail?> get() = _albumDetail

    private val _isLoading = MutableLiveData<Boolean>()

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchAlbumDetail(albumId: Int) {
        _isLoading.value = true
        repository.getAlbumDetail(albumId, onSuccess = { album ->
            _albumDetail.value = album
            _errorMessage.value = null
            _isLoading.value = false
        }, onError = { error ->
            _errorMessage.value = error
            _isLoading.value = false
        })
    }
}
