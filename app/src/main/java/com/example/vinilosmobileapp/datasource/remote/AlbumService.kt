package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.*
import com.example.vinilosmobileapp.model.dto.AlbumCreateDTO
import com.example.vinilosmobileapp.model.dto.CollectorCreateDTO
import com.example.vinilosmobileapp.model.dto.CommentCreateDTO
import com.example.vinilosmobileapp.model.dto.TrackCreateDTO
import retrofit2.Call
import retrofit2.http.*

interface AlbumService {

    @GET("albums")
    fun getAlbums(): Call<List<Album>>

    @GET("albums/{id}")
    fun getAlbumById(@Path("id") id: Int): Call<AlbumDetail>

    @POST("albums")
    fun createAlbum(@Body albumCreateDTO: AlbumCreateDTO): Call<Album>

    @POST("albums/{albumId}/comments")
    fun addComment(
        @Path("albumId") albumId: Int,
        @Body commentCreateDTO: CommentCreateDTO
    ): Call<Void>

    @POST("albums/{albumId}/tracks")
    fun addTrack(@Path("albumId") albumId: Int, @Body trackCreateDTO: TrackCreateDTO): Call<Void>

    @GET("collectors")
    fun getCollectors(): Call<List<Collector>>

    @POST("collectors")
    fun createCollector(@Body collectorCreateDTO: CollectorCreateDTO): Call<Collector>
}
