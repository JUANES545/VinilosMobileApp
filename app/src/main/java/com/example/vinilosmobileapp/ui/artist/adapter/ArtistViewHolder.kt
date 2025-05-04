package com.example.vinilosmobileapp.ui.artist.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.ItemArtistBinding
import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.utils.FavoritesManager

class ArtistViewHolder(private val binding: ItemArtistBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(artist: Artist) {
        binding.textViewArtistName.text = artist.name
        binding.textViewArtistBio.text = artist.description
        binding.imageViewArtist.load(artist.image) {
            placeholder(R.drawable.ic_artists)
            error(R.drawable.ic_failed_to_load_image)
            listener(
                onSuccess = { _, _ ->
                    binding.shimmerImageViewArtistCover.hideShimmer()
                },
                onError = { _, _ ->
                    binding.shimmerImageViewArtistCover.hideShimmer()
                }
            )
        }

        setupFavorites(artist)
    }

    private fun setupFavorites(artist: Artist) {
        val isFavorite = FavoritesManager.isFavorite(binding.root.context, artist.id)
        binding.btnFavorite.isSelected = isFavorite
        binding.btnFavorite.setOnClickListener {
            FavoritesManager.toggleFavorite(binding.root.context, artist.id)
            binding.btnFavorite.isSelected = !binding.btnFavorite.isSelected
        }
    }

}
