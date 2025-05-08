package com.example.vinilosmobileapp.ui.artist

import android.annotation.SuppressLint
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
import com.example.vinilosmobileapp.databinding.FragmentDetailArtistBinding
import com.example.vinilosmobileapp.datasource.local.AlbumProvider
import com.example.vinilosmobileapp.model.ArtistDetail
import com.example.vinilosmobileapp.ui.artist.adapter.PrizeAdapter
import com.example.vinilosmobileapp.ui.home.adapter.AlbumAdapter

class DetailArtistFragment : Fragment() {

    private var _binding: FragmentDetailArtistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailArtistViewModel by viewModels()
    private lateinit var albumsAdapter: AlbumAdapter
    private lateinit var prizesAdapter: PrizeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailArtistBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.artist_detail)
        val artistId = arguments?.getInt("artistId") ?: -1
        if (artistId < 0) {
            Toast.makeText(requireContext(), "ID de artista inválido", Toast.LENGTH_SHORT).show()
            return
        }

        setupMusicType()
        setupAlbums()
        setupPrizes()

        // Observers
        viewModel.artist.observe(viewLifecycleOwner) { artist ->
            artist?.let { showArtistDetails(it) }
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

    @SuppressLint("SetTextI18n")
    private fun setupMusicType() {
        val randomAlbum = AlbumProvider.getAlbums().random()
        val genre = randomAlbum.genre
        val recordLabel = randomAlbum.recordLabel
        binding.tvRoleLocation.text = "$genre - $recordLabel"
    }

    private fun setupPrizes() {
        with(binding) {
            prizesAdapter = PrizeAdapter(emptyList())
            recyclerPrizes.adapter = prizesAdapter

            tvStatPrizes.text = prizesAdapter.itemCount.toString()
            if (prizesAdapter.itemCount == 0) {
                textNoPrizes.visibility = View.VISIBLE
                recyclerPrizes.visibility = View.GONE
            } else {
                textNoPrizes.visibility = View.GONE
                recyclerPrizes.visibility = View.VISIBLE
            }
        }
    }

    private fun setupAlbums() {
        albumsAdapter = AlbumAdapter(emptyList()) { albumId ->
            val bundle = Bundle().apply { putInt("albumId", albumId) }
            findNavController().navigate(R.id.detailAlbumFragment, bundle)
        }
        with(binding) {
            recyclerAlbums.adapter = albumsAdapter

            tvStatAlbums.text = albumsAdapter.itemCount.toString()
            if (albumsAdapter.itemCount == 0) {
                textNoAlbums.visibility = View.VISIBLE
                recyclerAlbums.visibility = View.GONE
            } else {
                textNoAlbums.visibility = View.GONE
                recyclerAlbums.visibility = View.VISIBLE
            }
        }
    }

    private fun showArtistDetails(artistDetail: ArtistDetail) {
        with(binding) {
            progressBar.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE

            ivArtist.load(artistDetail.image) {
                placeholder(R.drawable.ic_artists)
                error(R.drawable.ic_failed_to_load_image)
                listener(
                    onSuccess = { _, _ ->
                        shimmerArtist.hideShimmer()
                    },
                    onError = { _, _ ->
                        shimmerArtist.hideShimmer()
                    }
                )
            }
            tvName.text = artistDetail.name
            tvDescription.text = artistDetail.description
            tvBirthDate.text = artistDetail.birthDate.take(10)
        }

        albumsAdapter.updateAlbums(artistDetail.albums)
        prizesAdapter.updatePrizes(artistDetail.performerPrizes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
