package com.example.vinilosmobileapp.datasource.remote

import com.example.vinilosmobileapp.model.*
import com.example.vinilosmobileapp.model.dto.AlbumCreateDTO
import com.example.vinilosmobileapp.model.dto.CollectorCreateDTO
import com.example.vinilosmobileapp.model.dto.CommentCreateDTO
import com.example.vinilosmobileapp.model.dto.TrackCreateDTO
import retrofit2.Call
import retrofit2.http.*

/**
 * Interface que define los servicios de red relacionados con álbumes, comentarios, pistas y coleccionistas.
 */
interface AlbumService {

    /**
     * Obtiene la lista de todos los álbumes.
     * @return Una llamada que devuelve una lista de objetos Album.
     */
    @GET("albums")
    fun getAlbums(): Call<List<Album>>

    /**
     * Obtiene los detalles de un álbum específico por su ID.
     * @param id El ID del álbum a buscar.
     * @return Una llamada que devuelve un objeto AlbumDetail.
     */
    @GET("albums/{id}")
    fun getAlbumById(@Path("id") id: Int): Call<AlbumDetail>

    /**
     * Crea un nuevo álbum.
     * @param albumCreateDTO Objeto que contiene los datos necesarios para crear un álbum.
     * @return Una llamada que devuelve el álbum creado.
     */
    @POST("albums")
    fun createAlbum(@Body albumCreateDTO: AlbumCreateDTO): Call<Album>

    /**
     * Agrega un comentario a un álbum específico.
     * @param albumId El ID del álbum al que se agregará el comentario.
     * @param commentCreateDTO Objeto que contiene los datos del comentario.
     * @return Una llamada que devuelve un objeto vacío (Void) si la operación es exitosa.
     */
    @POST("albums/{albumId}/comments")
    fun addComment(
        @Path("albumId") albumId: Int,
        @Body commentCreateDTO: CommentCreateDTO
    ): Call<Void>

    /**
     * Agrega una pista a un álbum específico.
     * @param albumId El ID del álbum al que se agregará la pista.
     * @param trackCreateDTO Objeto que contiene los datos de la pista.
     * @return Una llamada que devuelve un objeto vacío (Void) si la operación es exitosa.
     */
    @POST("albums/{albumId}/tracks")
    fun addTrack(@Path("albumId") albumId: Int, @Body trackCreateDTO: TrackCreateDTO): Call<Void>

    /**
     * Obtiene la lista de todos los coleccionistas.
     * @return Una llamada que devuelve una lista de objetos Collector.
     */
    @GET("collectors")
    fun getCollectors(): Call<List<Collector>>

    /**
     * Crea un nuevo coleccionista.
     * @param collectorCreateDTO Objeto que contiene los datos necesarios para crear un coleccionista.
     * @return Una llamada que devuelve el coleccionista creado.
     */
    @POST("collectors")
    fun createCollector(@Body collectorCreateDTO: CollectorCreateDTO): Call<Collector>
}