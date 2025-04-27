package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.*
import retrofit2.Call

object AlbumServiceAdapter {
    private val service = RetrofitClient.retrofit.create(AlbumService::class.java)

    fun getAlbums(): Call<List<Album>> = service.getAlbums()

    fun getAlbum(albumId: Int): Call<AlbumDetail> = service.getAlbumById(albumId)

    fun createAlbum(albumCreateDTO: AlbumCreateDTO): Call<Album> {
        return service.createAlbum(albumCreateDTO)
    }

    fun addCommentToAlbum(albumId: Int, description: String, collectorId: Int = 1): Call<Void> {
        val comment = CommentCreateDTO(
            description = description,
            rating = 5,
            collector = CollectorReferenceDTO(collectorId)
        )
        return service.addComment(albumId, comment)
    }

    fun getCollectors(): Call<List<Collector>> {
        return service.getCollectors()
    }

    fun createCollector(name: String, telephone: String, email: String): Call<Collector> {
        val collectorCreateDTO = CollectorCreateDTO(
            name = name,
            telephone = telephone,
            email = email
        )
        return service.createCollector(collectorCreateDTO)
    }
}
