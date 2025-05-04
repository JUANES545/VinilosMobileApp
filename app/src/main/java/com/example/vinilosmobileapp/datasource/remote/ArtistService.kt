package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.model.ArtistDetail
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ArtistService {
    @GET("musicians")
    fun getArtists(): Call<List<Artist>>

    @GET("musicians/{id}")
    fun getArtistById(@Path("id") artistId: Int): Call<ArtistDetail>

    @POST("musicians")
    fun createMusician(@Body artist: ArtistCreateDTO): Call<Artist>
}
