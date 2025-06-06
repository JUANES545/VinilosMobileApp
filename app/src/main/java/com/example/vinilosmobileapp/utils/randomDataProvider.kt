package com.example.vinilosmobileapp.utils

object RandomDataProvider {

    val randomComments = listOf(
        "¡Este álbum es increíble!",
        "Me encanta la música de este artista.",
        "Una obra maestra, sin duda.",
        "¡No puedo dejar de escucharlo!",
        "Este álbum me trae muchos recuerdos.",
        "¡Altamente recomendado!"
    )

    val randomSongs = listOf(
        "Bohemian Rhapsody",
        "Hotel California",
        "Stairway to Heaven",
        "Imagine",
        "Smells Like Teen Spirit",
        "Sweet Child O' Mine",
        "Billie Jean",
        "Hey Jude",
        "Wonderwall",
        "Shape of You"
    )

    val genreOptions = listOf("Classical", "Salsa", "Rock", "Folk")

    val recordLabels = listOf(
        "Sony Music",
        "EMI",
        "Discos Fuentes",
        "Elektra",
        "Fania Records"
    )

    val organizations = listOf(
        "Music Awards",
        "Global Music",
        "World Sound",
        "International Music Fest",
        "Harmony Awards"
    )

    val awardNames = listOf(
        "Best New Artist",
        "Album of the Year",
        "Song of the Year",
        "Best Live Performance",
        "Lifetime Achievement"
    )

    val descriptions = listOf(
        "Awarded to the best new artist of the year.",
        "Awarded to the best album of the year.",
        "Awarded to the top song of the year.",
        "Awarded to the artist with the best live performance of the year.",
        "Awarded to an artist for their outstanding contributions to the music industry."
    )

    val artistName = listOf(
        "Michael Jackson",
        "Madonna",
        "Queen",
        "The Beatles",
        "Elvis Presley",
        "Bob Dylan",
        "David Bowie",
        "Juanes",
        "Stevie Wonder",
        "Aretha Franklin",
        "Migue :v",
        "Jazz :v",
        "Nata :v",
    )
    val artistDescriptions = listOf(
        "Artista visionario con una carrera llena de éxitos, reconocimientos y contribuciones al mundo del arte.",
        "Pionero en su género, conocido por su estilo único, apasionante y su capacidad de innovar constantemente.",
        "Creador de obras maestras que han marcado generaciones y redefinido los estándares de la industria.",
        "Un talento excepcional que combina técnica, emoción y una profunda conexión con su audiencia en cada obra.",
        "Reconocido por su impacto cultural, su legado artístico y su influencia en artistas contemporáneos.",
        "Innovador constante, siempre explorando nuevos horizontes musicales y desafiando los límites creativos."
    )

    val guestNames = listOf(
        "Melómano Anónimo", "Oyente Misterioso", "Amante del Vinilo",
        "Coleccionista X", "Música para el alma"
    )

    fun randomDuration(): String {
        return "${(2..7).random()}:${(0..59).random().toString().padStart(2, '0')} min"
    }
}
