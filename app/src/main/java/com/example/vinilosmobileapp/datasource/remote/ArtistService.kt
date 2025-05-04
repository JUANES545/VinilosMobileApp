package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.model.ArtistDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistService {
    @GET("musicians")
    fun getArtists(): Call<List<Artist>>

    @GET("musicians/{id}")
    fun getArtistById(@Path("id") artistId: Int): Call<ArtistDetail>
}
