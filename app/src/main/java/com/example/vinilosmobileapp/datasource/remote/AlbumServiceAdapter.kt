package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.Album
import retrofit2.Call

object AlbumServiceAdapter {
    private val service = RetrofitClient.retrofit.create(AlbumService::class.java)

    fun getAlbums(): Call<List<Album>> = service.getAlbums()
}
