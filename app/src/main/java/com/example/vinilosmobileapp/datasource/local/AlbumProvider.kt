package com.example.vinilosmobileapp.datasource.local

import com.example.vinilosmobileapp.model.Album

object AlbumProvider {
    fun getAlbums(): List<Album> = listOf(
        Album(
            id = 1,
            name = "The Dark Side of the Moon",
            cover = "https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png",
            releaseDate = "1973-03-01",
            description = "Un álbum conceptual de Pink Floyd que explora la condición humana.",
            genre = "Rock Progresivo",
            recordLabel = "Harvest Records"
        ),
        Album(
            id = 2,
            name = "Abbey Road",
            cover = "https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg",
            releaseDate = "1969-09-26",
            description = "El último álbum grabado por The Beatles, incluyendo éxitos como 'Come Together'.",
            genre = "Rock",
            recordLabel = "Apple Records"
        ),
        Album(
            id = 3,
            name = "Kind of Blue",
            cover = "https://upload.wikimedia.org/wikipedia/commons/a/ad/Kind_of_Blue_%281959%2C_CL_1355%29_album_cover.jpg",
            releaseDate = "1959-08-17",
            description = "El álbum de jazz más influyente de todos los tiempos, por Miles Davis.",
            genre = "Jazz",
            recordLabel = "Columbia Records"
        ),
        Album(
            id = 4,
            name = "Renaissance",
            cover = "https://miro.medium.com/v2/resize:fit:1031/1*YZGOlFZMuxa37pVPnkp6jA.jpeg",
            releaseDate = "2022-07-29",
            description = "El séptimo álbum de estudio de Beyoncé, explorando temas de libertad y celebración.",
            genre = "Dance-Pop",
            recordLabel = "Parkwood Entertainment, Columbia Records"
        )
    )
}
