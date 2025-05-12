package com.example.vinilosmobileapp.ui.album.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosmobileapp.databinding.ItemTrackBinding
import com.example.vinilosmobileapp.model.Track

class TrackInputAdapter(private var tracks: List<Track>) :
    RecyclerView.Adapter<TrackInputAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.trackTitle.text = track.name
            binding.trackDuration.text = track.duration ?: "Duración desconocida"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    fun getTracks(): List<Track> = tracks

    fun addTrack(track: Track) {
        tracks = tracks + track
        notifyItemInserted(tracks.size - 1)
    }
}
