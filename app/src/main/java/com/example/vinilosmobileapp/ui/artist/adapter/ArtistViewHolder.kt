package com.example.vinilosmobileapp.ui.artist.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.ItemArtistBinding
import com.example.vinilosmobileapp.model.Artist

class ArtistViewHolder(private val binding: ItemArtistBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(artist: Artist) {
        binding.textViewArtistName.text = artist.name
        binding.textViewArtistBio.text = artist.description
        binding.imageViewArtist.load(artist.image) {
            placeholder(R.drawable.ic_image_placeholder)
            error(R.drawable.ic_failed_to_load_image)
        }
    }
}
