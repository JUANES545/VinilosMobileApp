package com.example.vinilosmobileapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vinilosmobileapp.datasource.remote.ArtistServiceAdapter
import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.model.ArtistDetail
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistRepository {

    private val _createResult = MutableLiveData<Boolean>()
    val createResult: LiveData<Boolean> = _createResult

    fun getArtistsWithErrorHandler(
        onSuccess: (List<Artist>) -> Unit,
        onError: (String) -> Unit
    ) {
        ArtistServiceAdapter.getArtists().enqueue(object : Callback<List<Artist>> {
            override fun onResponse(call: Call<List<Artist>>, response: Response<List<Artist>>) {
                if (response.isSuccessful) {
                    response.body()?.let(onSuccess)
                } else {
                    onError("Error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Artist>>, t: Throwable) {
                onError("No se pudo conectar al servidor. Verifica tu conexión.")
            }
        })
    }

    fun getArtistDetail(
        artistId: Int,
        onSuccess: (ArtistDetail) -> Unit,
        onError: (String) -> Unit
    ) {
        ArtistServiceAdapter.getArtist(artistId)
            .enqueue(object : Callback<ArtistDetail> {
                override fun onResponse(
                    call: Call<ArtistDetail>,
                    response: Response<ArtistDetail>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let(onSuccess)
                    } else {
                        onError("Error ${response.code()}: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ArtistDetail>, t: Throwable) {
                    onError(t.localizedMessage ?: "Error de red")
                }
            })
    }

    fun createArtist(dto: ArtistCreateDTO) {
        ArtistServiceAdapter.createArtist(dto)
            .enqueue(object : Callback<Artist> {
                override fun onResponse(call: Call<Artist>, resp: Response<Artist>) {
                    _createResult.value = resp.isSuccessful
                }

                override fun onFailure(call: Call<Artist>, t: Throwable) {
                    _createResult.value = false
                }
            })
    }

}
