package com.example.vinilosmobileapp.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentDetailAlbumBinding
import com.example.vinilosmobileapp.datasource.local.AlbumDetailProvider

class DetailAlbumFragment : Fragment() {

    private var _binding: FragmentDetailAlbumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.detail_album)

        val albumId = arguments?.getInt("albumId") ?: return
        val album = AlbumDetailProvider.getAlbumById(albumId)

        album?.let {
            binding.albumTitle.text = it.title
            binding.albumArtist.text = it.artist
            binding.albumYear.text = "${it.year} · ${it.duration} · ${it.genre}"
            binding.albumDescription.text = it.description

            Glide.with(this).load(it.imageUrl).into(binding.albumImage)

            // Lista de canciones (simplificada)
            binding.trackList.text =
                it.tracks.joinToString("\n") { track -> "${track.title}    ${track.duration}" }

            // Comentarios
            binding.comments.text = it.comments.joinToString("\n\n") { comment ->
                "${comment.user}:\n${comment.text}\nHace ${comment.daysAgo} días"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}