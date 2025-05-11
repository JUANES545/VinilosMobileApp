package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.model.ArtistDetail
import com.example.vinilosmobileapp.model.Prize
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import com.example.vinilosmobileapp.model.dto.PrizeCreateDTO
import com.example.vinilosmobileapp.model.dto.PrizeDateDTO
import retrofit2.Call

object ArtistServiceAdapter {
    private val service = RetrofitClient.retrofit.create(ArtistService::class.java)

    fun getArtists() = service.getArtists()

    fun getArtist(artistId: Int): Call<ArtistDetail> =
        service.getArtistById(artistId)

    fun createArtist(dto: ArtistCreateDTO): Call<Artist> =
        service.createMusician(dto)

    fun getPrizes(): Call<List<Prize>> = service.getPrizes()

    fun createPrize(dto: PrizeCreateDTO): Call<Prize> = service.createPrize(dto)

    fun addAlbumToArtist(artistId: Int, albumId: Int): Call<Void> {
        return service.addAlbumToArtist(artistId, albumId)
    }

    fun addPrizeToArtist(prizeId: Int, artistId: Int, prizeDateDTO: PrizeDateDTO): Call<Void> {
        return service.addPrizeToArtist(prizeId, artistId, prizeDateDTO)
    }
}
