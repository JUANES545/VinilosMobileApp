package com.example.vinilosmobileapp.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentArtistBinding
import com.example.vinilosmobileapp.datasource.remote.ArtistServiceAdapter
import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.model.Prize
import com.example.vinilosmobileapp.model.dto.PrizeCreateDTO
import com.example.vinilosmobileapp.ui.artist.adapter.ArtistAdapter
import com.example.vinilosmobileapp.utils.FavoritesManager
import com.example.vinilosmobileapp.utils.RandomDataProvider.awardNames
import com.example.vinilosmobileapp.utils.RandomDataProvider.descriptions
import com.example.vinilosmobileapp.utils.RandomDataProvider.organizations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistFragment : Fragment() {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistViewModel by viewModels()
    private lateinit var artistAdapter: ArtistAdapter
    private var showingFavorites = false
    private var allArtists = emptyList<Artist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArtistBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ensurePrizesSeeded()
        setupRecyclerView()
        setupFabListeners()
        setupSwipeRefresh()
        setupRetryButton()
        observeViewModel()

        setupFabListeners()
    }

    private fun ensurePrizesSeeded() {
        ArtistServiceAdapter.getPrizes().enqueue(object : Callback<List<Prize>> {
            override fun onResponse(call: Call<List<Prize>>, response: Response<List<Prize>>) {
                if (response.isSuccessful && response.body().isNullOrEmpty()) {
                    seedPrizes()
                }
            }

            override fun onFailure(call: Call<List<Prize>>, t: Throwable) {
                seedPrizes()
            }
        })
    }

    private fun seedPrizes() {
        organizations.forEach { label ->
            // elegimos aleatoriamente organization / name / description
            val dto = PrizeCreateDTO(
                organization = organizations.random(),
                name = awardNames.random() + " – $label",
                description = descriptions.random()
            )
            ArtistServiceAdapter.createPrize(dto)
                .enqueue(object : Callback<Prize> {
                    override fun onResponse(call: Call<Prize>, rsp: Response<Prize>) {
                        if (rsp.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "⚙️ Prize creado: ${rsp.body()?.id}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "❌ Error creando prize: ${rsp.code()} – ${
                                    rsp.errorBody()?.string()
                                }",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Prize>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "❌ Red al crear prize: ${t.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    private fun setupRecyclerView() {
        artistAdapter = ArtistAdapter(emptyList()) { artistId ->
            val args = Bundle().apply { putInt("artistId", artistId) }
            findNavController().navigate(
                R.id.action_artistFragment_to_artistDetailFragment,
                args
            )
        }
        binding.recyclerViewArtists.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = artistAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshArtist.setOnRefreshListener {
            viewModel.fetchArtists()
        }
    }

    private fun setupRetryButton() {
        binding.buttonRetryArtist.setOnClickListener {
            viewModel.fetchArtists()
        }
    }

    private fun observeViewModel() {
        viewModel.artists.observe(viewLifecycleOwner) { list ->
            binding.swipeRefreshArtist.isRefreshing = false
            if (!list.isNullOrEmpty()) {
                artistAdapter.updateArtists(list)
                allArtists = list
                applyFilter()
                binding.recyclerViewArtists.visibility = View.VISIBLE
                binding.errorLayoutArtist.visibility = View.GONE
            } else {
                binding.recyclerViewArtists.visibility = View.GONE
                binding.errorLayoutArtist.visibility = View.VISIBLE
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                binding.swipeRefreshArtist.isRefreshing = false
                binding.recyclerViewArtists.visibility = View.GONE
                binding.errorLayoutArtist.visibility = View.VISIBLE
                binding.textErrorArtist.text = msg
            }
        }
    }

    private fun setupFabListeners() {
        // Set the listener for artist creation result
        setFragmentResultListener("artist_created") { _, _ ->
            viewModel.fetchArtists()
        }

        binding.fabAddArtist.setOnClickListener {
            findNavController().navigate(R.id.action_artist_to_createArtist)
        }
        // Toggle favorites filter
        binding.fabFilterFavorites.setOnClickListener {
            showingFavorites = !showingFavorites
            updateFilterIcon()
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                if (showingFavorites) "Artistas Favoritos" else "Todos los Artistas"
            applyFilter()
        }
    }

    private fun updateFilterIcon() {
        val icon = if (!showingFavorites) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        binding.fabFilterFavorites.post {
            binding.fabFilterFavorites.setImageResource(icon)
        }
    }

    private fun applyFilter() {
        val listToShow = if (showingFavorites) {
            allArtists.filter { FavoritesManager.isFavorite(requireContext(), it.id) }
        } else {
            allArtists
        }

        if (listToShow.isEmpty()) {
            binding.errorLayoutArtist.visibility = View.VISIBLE
            binding.imageErrorArtist.setImageResource(R.drawable.ic_empty_list) // Set empty list image
            binding.buttonRetryArtist.visibility = if (showingFavorites) View.GONE else View.VISIBLE
            binding.textErrorArtist.text = if (showingFavorites) {
                "No tienes artistas favoritos :c"
            } else {
                "Error al cargar artistas."
            }
        } else {
            binding.recyclerViewArtists.visibility = View.VISIBLE
            binding.errorLayoutArtist.visibility = View.GONE
            binding.buttonRetryArtist.visibility = View.GONE
        }

        artistAdapter.updateArtists(listToShow)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
