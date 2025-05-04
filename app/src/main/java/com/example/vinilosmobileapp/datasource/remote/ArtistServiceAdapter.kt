package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.ArtistDetail
import retrofit2.Call

object ArtistServiceAdapter {
    private val service = RetrofitClient.retrofit.create(ArtistService::class.java)
    fun getArtists() = service.getArtists()

    fun getArtist(artistId: Int): Call<ArtistDetail> =
        service.getArtistById(artistId)
}
