package com.example.vinilosmobileapp.ui.artist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosmobileapp.databinding.ItemArtistBinding
import com.example.vinilosmobileapp.model.Artist

class ArtistAdapter(
    private var artistList: List<Artist>
) : RecyclerView.Adapter<ArtistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ItemArtistBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artistList[position])
    }

    override fun getItemCount(): Int = artistList.size

    fun updateArtists(newList: List<Artist>) {
        artistList = newList
        notifyDataSetChanged()
    }
}
