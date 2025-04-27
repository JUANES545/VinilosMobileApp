package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.Album
import retrofit2.Call
import retrofit2.http.GET

interface AlbumService {
    @GET("albums")
    fun getAlbums(): Call<List<Album>>
}
