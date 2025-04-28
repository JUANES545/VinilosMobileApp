package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.*
import com.example.vinilosmobileapp.model.dto.AlbumCreateDTO
import com.example.vinilosmobileapp.model.dto.CollectorCreateDTO
import com.example.vinilosmobileapp.model.dto.CommentCreateDTO
import com.example.vinilosmobileapp.model.dto.TrackCreateDTO
import retrofit2.Call

object AlbumServiceAdapter {
    private val service = RetrofitClient.retrofit.create(AlbumService::class.java)

    fun getAlbums(): Call<List<Album>> = service.getAlbums()

    fun getAlbum(albumId: Int): Call<AlbumDetail> = service.getAlbumById(albumId)

    fun createAlbum(albumCreateDTO: AlbumCreateDTO): Call<Album> {
        return service.createAlbum(albumCreateDTO)
    }

    fun addCommentToAlbum(albumId: Int, commentCreateDTO: CommentCreateDTO): Call<Void> {
        return service.addComment(albumId, commentCreateDTO)
    }

    fun addTrackToAlbum(albumId: Int, trackCreateDTO: TrackCreateDTO): Call<Void> {
        return service.addTrack(albumId, trackCreateDTO)
    }

    fun getCollectors(): Call<List<Collector>> {
        return service.getCollectors()
    }

    fun createCollector(name: String, telephone: String, email: String): Call<Collector> {
        val collector = CollectorCreateDTO(
            name = name,
            telephone = telephone,
            email = email
        )
        return service.createCollector(collector)
    }
}
