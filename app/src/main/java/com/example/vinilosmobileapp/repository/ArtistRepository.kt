package com.example.vinilosmobileapp.repository

import com.example.vinilosmobileapp.datasource.remote.ArtistServiceAdapter
import com.example.vinilosmobileapp.model.Artist
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
                onError(t.localizedMessage ?: "Error de red")
            }
        })
    }
}
