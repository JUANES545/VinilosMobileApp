package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.Album
import com.example.vinilosmobileapp.model.AlbumCreateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AlbumService {
    @GET("albums")
    fun getAlbums(): Call<List<Album>>

    @POST("albums")
    fun createAlbum(@Body album: AlbumCreateDTO): Call<Album>
}
