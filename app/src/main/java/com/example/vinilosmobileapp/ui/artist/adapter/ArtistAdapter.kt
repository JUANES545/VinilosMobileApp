package com.example.vinilosmobileapp.ui.artist.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosmobileapp.databinding.ItemArtistBinding
import com.example.vinilosmobileapp.model.Artist

class ArtistAdapter(
    private var artistList: List<Artist>,
    private val onClick: (artistId: Int) -> Unit
) : RecyclerView.Adapter<ArtistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ItemArtistBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = artistList[position]
        holder.bind(artist)
        holder.itemView.setOnClickListener {
            onClick(artist.id)
        }
    }

    override fun getItemCount(): Int = artistList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateArtists(newList: List<Artist>) {
        artistList = newList
        notifyDataSetChanged()
    }
}
