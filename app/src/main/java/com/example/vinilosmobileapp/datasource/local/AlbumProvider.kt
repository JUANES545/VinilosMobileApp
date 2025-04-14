package com.example.vinilosmobileapp.datasource.local

import com.example.vinilosmobileapp.model.Album

object AlbumProvider {
    fun getAlbums(): List<Album> = listOf(
        Album(
            id = 1,
            title = "Dark Side of the Moon",
            artist = "Pink Floyd",
            imageUrl = "https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png"
        ),
        Album(
            id = 2,
            title = "Abbey Road",
            artist = "The Beatles",
            imageUrl = "https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg"
        ),
        Album(
            id = 3,
            title = "Kind of Blue",
            artist = "Miles Davis",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/a/ad/Kind_of_Blue_%281959%2C_CL_1355%29_album_cover.jpg"
        ),
        Album(
            id = 4,
            title = "Renaissance",
            artist = "Beyoncé",
            imageUrl = "https://miro.medium.com/v2/resize:fit:1031/1*YZGOlFZMuxa37pVPnkp6jA.jpeg"
        )
    )
}
