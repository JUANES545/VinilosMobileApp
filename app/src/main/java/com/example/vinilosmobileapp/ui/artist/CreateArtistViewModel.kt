package com.example.vinilosmobileapp.ui.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vinilosmobileapp.model.Album
import com.example.vinilosmobileapp.model.PerformerPrize
import com.example.vinilosmobileapp.model.Prize
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import com.example.vinilosmobileapp.repository.AlbumRepository
import com.example.vinilosmobileapp.repository.ArtistRepository

class CreateArtistViewModel : ViewModel() {

    private val artistRepository = ArtistRepository()
    private val albumRepository = AlbumRepository()

    private val _createResult = MutableLiveData<Boolean>()
    val createResult: LiveData<Boolean> get() = _createResult

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _prizes = MutableLiveData<List<Prize>>()
    val prizes: LiveData<List<Prize>> get() = _prizes

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private var createdArtistId: Int? = null

    fun createArtist(dto: ArtistCreateDTO) {
        artistRepository.createArtist(dto,
            onSuccess = { artistId ->
                _createResult.postValue(true)
                createdArtistId = artistId
            },
            onError = { error ->
                _createResult.value = false
                _errorMessage.value = error
            }
        )
    }

    fun fetchAlbums() {
        albumRepository.getAlbums(
            onSuccess = { _albums.value = it },
            onError = { _errorMessage.value = it }
        )
    }

    fun fetchPrizes() {
        artistRepository.getPrizes(
            onSuccess = { _prizes.value = it },
            onError = { _errorMessage.value = it }
        )
    }

    fun getCreatedArtistId(): Int? {
        return createdArtistId
    }

    fun addAlbumsToArtist(artistId: Int, albumIds: List<Album>) {
        albumIds.forEach { album ->
            artistRepository.addAlbumToArtist(artistId, album.id,
                onSuccess = {
                    // Handle success if needed
                },
                onError = { error ->
                    _errorMessage.value = error
                }
            )
        }
    }

    fun addPrizesToArtist(artistId: Int, prizes: List<PerformerPrize>) {
        prizes.forEach { prize ->
            artistRepository.addPrizeToArtist(
                prizeId = prize.id,
                artistId = artistId,
                premiationDate = prize.premiationDate,
                onSuccess = {
                    // Handle success if needed
                },
                onError = { error ->
                    _errorMessage.value = error
                }
            )
        }
    }

}
