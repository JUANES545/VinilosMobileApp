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

            val guestNames = listOf(
                "Melómano Anónimo",
                "Oyente Misterioso",
                "Amante del Vinilo",
                "Coleccionista X",
                "Música para el alma"
            )

            val genreOptions = listOf("Classical", "Salsa", "Rock", "Folk")

            val artistOptions = listOf(
                "Sony Music",
                "EMI",
                "Discos Fuentes",
                "Elektra",
                "Fania Records"
            )

            fun randomDuration(): String {
                return "${(2..7).random()}:${(0..59).random().toString().padStart(2, '0')} min"
            }
        }
