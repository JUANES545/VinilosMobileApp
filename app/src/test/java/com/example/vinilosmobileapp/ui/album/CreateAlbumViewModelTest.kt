package com.example.vinilosmobileapp.ui.album

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateAlbumViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CreateAlbumViewModel

    @Before
    fun setUp() {
        viewModel = CreateAlbumViewModel()
    }

    @Test
    fun `submitAlbum sets albumCreated to true`() {
        // Observamos LiveData sin hacer nada, solo para activar el observer
        val observer = Observer<Boolean> {}
        try {
            viewModel.albumCreated.observeForever(observer)

            viewModel.submitAlbum(
                name = "Test Album",
                year = 2023,
                artist = "Test Artist",
                genre = "Rock"
            )

            val result = viewModel.albumCreated.value
            assertTrue(result == true)

        } finally {
            viewModel.albumCreated.removeObserver(observer)
        }
    }
}
