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
import androidx.recyclerview.widget.LinearLayoutManager
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
    private val vm: CreateArtistViewModel by viewModels()
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

        val artistId = getArtistIdFromArguments() ?: return

        setupMusicType()
        setupRecyclerViews()
        setupObservers()
        fetchArtistDetails(artistId)
    }

    private fun getArtistIdFromArguments(): Int? {
        val artistId = arguments?.getInt("artistId") ?: -1
        if (artistId < 0) {
            Toast.makeText(requireContext(), "ID de artista inválido", Toast.LENGTH_SHORT).show()
            return null
        }
        return artistId
    }

    private fun setupRecyclerViews() {
        setupAlbumsRecyclerView()
        setupPrizesRecyclerView()
    }

    private fun setupAlbumsRecyclerView() {
        albumsAdapter = AlbumAdapter(emptyList()) { albumId ->
            val bundle = Bundle().apply { putInt("albumId", albumId) }
            findNavController().navigate(R.id.detailAlbumFragment, bundle)
        }
        binding.recyclerAlbums.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = albumsAdapter
        }
    }

    private fun setupPrizesRecyclerView() {
        vm.fetchPrizes()
        val prizes = vm.prizes.value ?: emptyList() // Fetch prizes from ViewModel
        prizesAdapter = PrizeAdapter(
            performerPrizes = viewModel.artist.value?.performerPrizes ?: emptyList(),
            prizes = prizes
        )
        binding.recyclerPrizes.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = prizesAdapter
        }
    }

    private fun setupObservers() {
        viewModel.artist.observe(viewLifecycleOwner) { artist ->
            artist?.let { showArtistDetails(it) }
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
        vm.prizes.observe(viewLifecycleOwner) { prizes ->
            prizesAdapter.updatePrizes(
                viewModel.artist.value?.performerPrizes ?: emptyList(),
                prizes
            )
        }
    }

    private fun fetchArtistDetails(artistId: Int) {
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

    private fun showArtistDetails(artistDetail: ArtistDetail) {
        with(binding) {
            progressBar.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE

            displayArtistInfo(artistDetail)
            updateAlbumsSection(artistDetail)
            updatePrizesSection(artistDetail)
        }
    }

    private fun displayArtistInfo(artistDetail: ArtistDetail) {
        with(binding) {
            ivArtist.load(artistDetail.image) {
                placeholder(R.drawable.ic_artists)
                error(R.drawable.ic_failed_to_load_image)
                listener(
                    onSuccess = { _, _ -> shimmerArtist.hideShimmer() },
                    onError = { _, _ -> shimmerArtist.hideShimmer() }
                )
            }
            tvName.text = artistDetail.name
            tvDescription.text = artistDetail.description
            tvStatAlbums.text = artistDetail.albums.size.toString()
            tvStatPrizes.text = artistDetail.performerPrizes.size.toString()
            tvBirthDate.text = artistDetail.birthDate.take(10)
        }
    }

    private fun updateAlbumsSection(artistDetail: ArtistDetail) {
        with(binding) {
            if (artistDetail.albums.isNotEmpty()) {
                recyclerAlbums.visibility = View.VISIBLE
                textNoAlbums.visibility = View.GONE
                albumsAdapter.updateAlbums(artistDetail.albums)
            } else {
                recyclerAlbums.visibility = View.GONE
                textNoAlbums.visibility = View.VISIBLE
            }
        }
    }

    private fun updatePrizesSection(artistDetail: ArtistDetail) {
        with(binding) {
            if (artistDetail.performerPrizes.isNotEmpty()) {
                recyclerPrizes.visibility = View.VISIBLE
                textNoPrizes.visibility = View.GONE
                prizesAdapter.updatePrizes(
                    artistDetail.performerPrizes,
                    viewModel.prizes.value ?: emptyList()
                )
            } else {
                recyclerPrizes.visibility = View.GONE
                textNoPrizes.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
