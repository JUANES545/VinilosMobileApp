package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.model.ArtistDetail
import com.example.vinilosmobileapp.model.Prize
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import com.example.vinilosmobileapp.model.dto.PrizeCreateDTO
import com.example.vinilosmobileapp.model.dto.PrizeDateDTO
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

    @GET("prizes")
    fun getPrizes(): Call<List<Prize>>

    @POST("prizes")
    fun createPrize(@Body dto: PrizeCreateDTO): Call<Prize>

    @POST("musicians/{artistId}/albums/{albumId}")
    fun addAlbumToArtist(
        @Path("artistId") artistId: Int,
        @Path("albumId") albumId: Int
    ): Call<Void>

    @POST("prizes/{prizeId}/musicians/{artistId}")
    fun addPrizeToArtist(
        @Path("prizeId") prizeId: Int,
        @Path("artistId") artistId: Int,
        @Body premiationDate: PrizeDateDTO
    ): Call<Void>
}
