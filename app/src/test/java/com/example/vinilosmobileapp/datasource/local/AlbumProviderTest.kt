package com.example.vinilosmobileapp.datasource.local

import org.junit.Assert.*
import org.junit.Test

class AlbumProviderTest {

    @Test
    fun `getAlbums returns correct number of albums`() {
        val albums = AlbumProvider.getAlbums()
        assertEquals(4, albums.size)
    }

    @Test
    fun `first album has expected name`() {
        val album = AlbumProvider.getAlbums().first()
        assertEquals("The Dark Side of the Moon", album.name)
    }

    @Test
    fun `first album has expected genre`() {
        val album = AlbumProvider.getAlbums().first()
        assertEquals("Rock Progresivo", album.genre)
    }

    @Test
    fun `first album has expected record label`() {
        val album = AlbumProvider.getAlbums().first()
        assertEquals("Harvest Records", album.recordLabel)
    }
}
