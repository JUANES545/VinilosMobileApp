package com.example.vinilosmobileapp.repository

import com.example.vinilosmobileapp.datasource.remote.ArtistServiceAdapter
import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.model.ArtistDetail
import com.example.vinilosmobileapp.model.Prize
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import com.example.vinilosmobileapp.model.dto.PrizeCreateDTO
import com.example.vinilosmobileapp.utils.RandomDataProvider.awardNames
import com.example.vinilosmobileapp.utils.RandomDataProvider.descriptions
import com.example.vinilosmobileapp.utils.RandomDataProvider.organizations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistRepository {

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
        ArtistServiceAdapter.getArtist(artistId).enqueue(object : Callback<ArtistDetail> {
            override fun onResponse(call: Call<ArtistDetail>, response: Response<ArtistDetail>) {
                if (response.isSuccessful) {
                    response.body()?.let(onSuccess)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArtistDetail>, t: Throwable) {
                onError(t.localizedMessage ?: "Network error")
            }
        })
    }

    fun createArtist(
        dto: ArtistCreateDTO,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ArtistServiceAdapter.createArtist(dto).enqueue(object : Callback<Artist> {
            override fun onResponse(call: Call<Artist>, response: Response<Artist>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Artist>, t: Throwable) {
                onError(t.localizedMessage ?: "Error de red")
            }
        })
    }

    fun seedPrizes(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ArtistServiceAdapter.getPrizes().enqueue(object : Callback<List<Prize>> {
            override fun onResponse(call: Call<List<Prize>>, response: Response<List<Prize>>) {
                if (response.isSuccessful && response.body().isNullOrEmpty()) {
                    organizations.forEach { label ->
                        val dto = PrizeCreateDTO(
                            organization = organizations.random(),
                            name = awardNames.random() + " – $label",
                            description = descriptions.random()
                        )
                        ArtistServiceAdapter.createPrize(dto).enqueue(object : Callback<Prize> {
                            override fun onResponse(call: Call<Prize>, rsp: Response<Prize>) {
                                if (!rsp.isSuccessful) {
                                    onError("Error creating prize: ${rsp.code()}")
                                }
                            }

                            override fun onFailure(call: Call<Prize>, t: Throwable) {
                                onError("Network error: ${t.localizedMessage}")
                            }
                        })
                    }
                    onSuccess()
                } else {
                    onSuccess()
                }
            }

            override fun onFailure(call: Call<List<Prize>>, t: Throwable) {
                onError("Network error: ${t.localizedMessage}")
            }
        })
    }

    fun getPrizes(
        onSuccess: (List<Prize>) -> Unit,
        onError: (String) -> Unit
    ) {
        ArtistServiceAdapter.getPrizes().enqueue(object : Callback<List<Prize>> {
            override fun onResponse(call: Call<List<Prize>>, response: Response<List<Prize>>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Prize>>, t: Throwable) {
                onError("No se pudo conectar al servidor. Verifica tu conexión.")
            }
        })
    }

}
