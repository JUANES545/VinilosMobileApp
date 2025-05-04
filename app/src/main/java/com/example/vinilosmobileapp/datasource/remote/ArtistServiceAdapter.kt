package com.example.vinilosmobileapp.datasource.remote

object ArtistServiceAdapter {
    private val service = RetrofitClient.retrofit.create(ArtistService::class.java)
    fun getArtists() = service.getArtists()
}
