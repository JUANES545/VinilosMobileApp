package com.example.vinilosmobileapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.ItemAlbumBinding
import com.example.vinilosmobileapp.model.Album

class AlbumAdapter(
    var albumList: List<Album>,
    val onClick: (Int) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.textViewAlbumName.text = album.name
            binding.textViewArtist.text = album.genre

            binding.imageViewAlbumCover.load(album.cover) {
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_failed_to_load_image)
                listener(
                    onSuccess = { _, _ ->
                        binding.shimmerImageViewAlbumCover.hideShimmer()
                    },
                    onError = { _, _ ->
                        binding.shimmerImageViewAlbumCover.hideShimmer()
                    }
                )
            }

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
