package com.example.vinilosmobileapp.repository

import androidx.lifecycle.MutableLiveData
import com.example.vinilosmobileapp.datasource.remote.AlbumServiceAdapter
import com.example.vinilosmobileapp.model.Album
import com.example.vinilosmobileapp.model.dto.AlbumCreateDTO
import com.example.vinilosmobileapp.model.AlbumDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumRepository {

    val createAlbumResult = MutableLiveData<Boolean>()

    fun getAlbumsWithErrorHandler(
        onSuccess: (List<Album>) -> Unit,
        onError: (String) -> Unit
    ) {
        AlbumServiceAdapter.getAlbums().enqueue(object : Callback<List<Album>> {
            override fun onResponse(call: Call<List<Album>>, response: Response<List<Album>>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Código: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Album>>, t: Throwable) {
                onError("No se pudo conectar al servidor. Verifica tu conexión a Internet.")
            }
        })
    }

    fun createAlbum(albumCreateDTO: AlbumCreateDTO) {
        AlbumServiceAdapter.createAlbum(albumCreateDTO).enqueue(object : Callback<Album> {
            override fun onResponse(call: Call<Album>, response: Response<Album>) {
                createAlbumResult.value = response.isSuccessful
            }

            override fun onFailure(call: Call<Album>, t: Throwable) {
                createAlbumResult.value = false
            }
        })
    }

    fun getAlbumDetail(
        albumId: Int,
        onSuccess: (AlbumDetail) -> Unit,
        onError: (String) -> Unit
    ) {
        AlbumServiceAdapter.getAlbum(albumId).enqueue(object : Callback<AlbumDetail> {
            override fun onResponse(call: Call<AlbumDetail>, response: Response<AlbumDetail>) {
                if (response.isSuccessful) {
                    response.body()?.let { album ->
                        onSuccess(album)
                    } ?: onError("Álbum no encontrado")
                } else {
                    onError("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AlbumDetail>, t: Throwable) {
                onError(t.message ?: "Error de red")
            }
        })
    }

}
