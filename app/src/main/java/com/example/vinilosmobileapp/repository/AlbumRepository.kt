package com.example.vinilosmobileapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vinilosmobileapp.datasource.remote.AlbumServiceAdapter
import com.example.vinilosmobileapp.model.Album
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumRepository {

    private val albumsLiveData = MutableLiveData<List<Album>?>()

    fun getAlbums(): LiveData<List<Album>?> {
        AlbumServiceAdapter.getAlbums().enqueue(object : Callback<List<Album>> {
            override fun onResponse(call: Call<List<Album>>, response: Response<List<Album>>) {
                if (response.isSuccessful) {
                    albumsLiveData.value = response.body() ?: emptyList()
                } else {
                    albumsLiveData.value = null
                }
            }

            override fun onFailure(call: Call<List<Album>>, t: Throwable) {
                albumsLiveData.value = null
            }
        })
        return albumsLiveData
    }
}
