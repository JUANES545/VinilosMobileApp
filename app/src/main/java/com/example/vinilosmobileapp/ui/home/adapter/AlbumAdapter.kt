package com.example.vinilosmobileapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vinilosmobileapp.databinding.ItemAlbumBinding
import com.example.vinilosmobileapp.model.Album

class AlbumAdapter(
    private var albumList: List<Album>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.textViewAlbumName.text = album.name
            binding.textViewArtist.text =
                album.genre    // <-- CAMBIO: usamos `genre` o `recordLabel`

            Glide.with(binding.root.context)
                .load(album.cover)
                .centerCrop()
                .into(binding.imageViewAlbumCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumList[position]
        holder.bind(album)
        holder.itemView.setOnClickListener {
            onClick(album.id)
        }
    }

    override fun getItemCount(): Int = albumList.size

    fun updateAlbums(newAlbums: List<Album>) {
        albumList = newAlbums
        notifyDataSetChanged()
    }
}
