package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.Artist
import retrofit2.Call
import retrofit2.http.GET

interface ArtistService {
    @GET("musicians")
    fun getArtists(): Call<List<Artist>>
}
