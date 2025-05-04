package com.example.vinilosmobileapp.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentArtistDetailBinding
import com.example.vinilosmobileapp.ui.artist.adapter.PrizeAdapter
import com.example.vinilosmobileapp.ui.home.adapter.AlbumAdapter

class ArtistDetailFragment : Fragment() {

    private var _binding: FragmentArtistDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArtistDetailViewModel by viewModels()
    private lateinit var albumsAdapter: AlbumAdapter
    private lateinit var prizesAdapter: PrizeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArtistDetailBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.detail_artist)
        val artistId = arguments?.getInt("artistId") ?: -1
        if (artistId < 0) {
            Toast.makeText(requireContext(), "ID de artista inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Recycler para álbumes
        albumsAdapter = AlbumAdapter(emptyList()) { albumId ->
            val bundle = Bundle().apply { putInt("albumId", albumId) }
            findNavController().navigate(R.id.detailAlbumFragment, bundle)
        }
        binding.recyclerAlbums.apply {
            adapter = albumsAdapter
        }

        // Recycler para premios
        prizesAdapter = PrizeAdapter(emptyList())
        binding.recyclerPrizes.adapter = prizesAdapter

        // Observers
        viewModel.artist.observe(viewLifecycleOwner) { artist ->
            artist?.let { showArtist(it) }
        }
        viewModel.error.observe(viewLifecycleOwner) { err ->
            err?.let {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        // Fetch
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchArtist(artistId)
    }

    private fun showArtist(a: com.example.vinilosmobileapp.model.ArtistDetail) {
        binding.progressBar.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE

        binding.ivArtist.load(a.image) {
            placeholder(R.drawable.ic_artists)
            error(R.drawable.ic_failed_to_load_image)
        }
        binding.tvName.text = a.name
        binding.tvDescription.text = a.description
        binding.tvBirthDate.text = a.birthDate.take(10)

        albumsAdapter.updateAlbums(a.albums)
        prizesAdapter.updatePrizes(a.performerPrizes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
