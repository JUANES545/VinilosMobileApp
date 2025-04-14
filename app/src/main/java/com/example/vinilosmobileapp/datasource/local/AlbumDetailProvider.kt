package com.example.vinilosmobileapp.datasource.local

object AlbumDetailProvider {
    fun getAlbumById(id: Int): AlbumDetail? = sampleAlbums.find { it.id == id }

    private val sampleAlbums = listOf(
        AlbumDetail(
            id = 1,
            title = "The Dark Side of the Moon",
            artist = "Pink Floyd",
            year = 1973,
            duration = "43 min",
            genre = "Rock Progresivo",
            description = "Un álbum conceptual que explora los conflictos que afectan a la vida cotidiana...",
            imageUrl = "https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png",
            tracks = listOf(
                Track("Speak to Me", "1:30"),
                Track("Breathe", "2:43"),
                Track("Time", "7:01"),
                Track("Money", "6:23")
            ),
            comments = listOf(
                Comment(
                    "Carlos Ruiz",
                    "Una obra maestra atemporal que sigue siendo relevante hoy en día.",
                    2
                ),
                Comment(
                    "Ana García",
                    "La calidad del vinilo es excepcional. Los efectos sonoros son increíbles.",
                    5
                )
            )
        ),
        AlbumDetail(
            id = 2,
            title = "Abbey Road",
            artist = "The Beatles",
            year = 1973,
            duration = "43 min",
            genre = "Rock Progresivo",
            description = "Un álbum conceptual que explora los conflictos que afectan a la vida cotidiana...",
            imageUrl = "https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg",
            tracks = listOf(
                Track("Speak to Me", "1:30"),
                Track("Breathe", "2:43"),
                Track("Time", "7:01"),
                Track("Money", "6:23")
            ),
            comments = listOf(
                Comment(
                    "Carlos Ruiz",
                    "Una obra maestra atemporal que sigue siendo relevante hoy en día.",
                    2
                ),
                Comment(
                    "Ana García",
                    "La calidad del vinilo es excepcional. Los efectos sonoros son increíbles.",
                    5
                )
            )
        ),
        AlbumDetail(
            id = 3,
            title = "Kind of Blue",
            artist = "Miles Davis",
            year = 1973,
            duration = "43 min",
            genre = "Rock Progresivo",
            description = "Un álbum conceptual que explora los conflictos que afectan a la vida cotidiana...",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/a/ad/Kind_of_Blue_%281959%2C_CL_1355%29_album_cover.jpg",
            tracks = listOf(
                Track("Speak to Me", "1:30"),
                Track("Breathe", "2:43"),
                Track("Time", "7:01"),
                Track("Money", "6:23")
            ),
            comments = listOf(
                Comment(
                    "Carlos Ruiz",
                    "Una obra maestra atemporal que sigue siendo relevante hoy en día.",
                    2
                ),
                Comment(
                    "Ana García",
                    "La calidad del vinilo es excepcional. Los efectos sonoros son increíbles.",
                    5
                )
            )
        ),
        AlbumDetail(
            id = 4,
            title = "Renaissance",
            artist = "Beyoncé",
            year = 2018,
            duration = "43 min",
            genre = "Rock Progresivo",
            description = "Un álbum conceptual que explora los conflictos que afectan a la vida cotidiana...",
            imageUrl = "https://miro.medium.com/v2/resize:fit:1031/1*YZGOlFZMuxa37pVPnkp6jA.jpeg",
            tracks = listOf(
                Track("Speak to Me", "1:30"),
                Track("Breathe", "2:43"),
                Track("Time", "7:01"),
                Track("Money", "6:23")
            ),
            comments = listOf(
                Comment(
                    "Carlos Ruiz",
                    "Una obra maestra atemporal que sigue siendo relevante hoy en día.",
                    2
                ),
                Comment(
                    "Ana García",
                    "La calidad del vinilo es excepcional. Los efectos sonoros son increíbles.",
                    5
                )
            )
        ),


    )
}

data class AlbumDetail(
    val id: Int,
    val title: String,
    val artist: String,
    val year: Int,
    val duration: String,
    val genre: String,
    val description: String,
    val imageUrl: String,
    val tracks: List<Track>,
    val comments: List<Comment>
)

data class Track(val title: String, val duration: String)
data class Comment(val user: String, val text: String, val daysAgo: Int)
