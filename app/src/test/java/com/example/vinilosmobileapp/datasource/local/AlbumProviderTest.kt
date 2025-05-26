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
    fun `first album has expected title`() {
        val album = AlbumProvider.getAlbums().first()
        assertEquals("Dark Side of the Moon", album.title)
    }
}
