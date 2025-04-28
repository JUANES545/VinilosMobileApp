package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.Album
import com.example.vinilosmobileapp.model.dto.AlbumCreateDTO
import com.example.vinilosmobileapp.model.AlbumDetail
import com.example.vinilosmobileapp.model.Collector
import com.example.vinilosmobileapp.model.dto.CollectorCreateDTO
import com.example.vinilosmobileapp.model.dto.CommentCreateDTO
import retrofit2.Call
import retrofit2.http.*

interface AlbumService {

    @GET("albums")
    fun getAlbums(): Call<List<Album>>

    @GET("albums/{id}")
    fun getAlbumById(@Path("id") albumId: Int): Call<AlbumDetail>

    @POST("albums")
    fun createAlbum(@Body album: AlbumCreateDTO): Call<Album>

    @POST("albums/{albumId}/comments")
    fun addComment(
        @Path("albumId") albumId: Int,
        @Body comment: CommentCreateDTO
    ): Call<Void>

    @GET("collectors")
    fun getCollectors(): Call<List<Collector>>

    @POST("collectors")
    fun createCollector(@Body collector: CollectorCreateDTO): Call<Collector>

}
