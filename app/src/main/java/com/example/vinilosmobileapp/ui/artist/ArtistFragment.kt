package com.example.vinilosmobileapp.ui.artist

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentArtistBinding
import com.example.vinilosmobileapp.model.Artist
import com.example.vinilosmobileapp.ui.artist.adapter.ArtistAdapter
import com.example.vinilosmobileapp.utils.ErrorHandler
import com.example.vinilosmobileapp.utils.FavoritesManager

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
        viewModel.prizeSeedingResult.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Log.d("ArtistFragment", "Prizes seeded successfully.")
            } else {
                Log.e("ArtistFragment", "Failed to seed prizes.")
            }
        }
        viewModel.seedPrizes()
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
        binding.errorLayout.buttonRetry.setOnClickListener {
            viewModel.fetchArtists()
        }
    }

    private fun observeViewModel() {
        viewModel.artists.observe(viewLifecycleOwner) { list ->
            binding.swipeRefreshArtist.isRefreshing = false

            if (!list.isNullOrEmpty()) {
                val sortedList = list.sortedByDescending { it.id }
                binding.recyclerViewArtists.visibility = View.VISIBLE
                binding.errorLayout.errorContainer.visibility = View.GONE
                artistAdapter.updateArtists(sortedList)
                allArtists = sortedList
                applyFilter()
            } else if (list != null && list.isEmpty()) {
                showEmptyState()
            }
        }
        observeErrors()
    }

    private fun observeErrors() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                ErrorHandler.showErrorState(
                    binding.swipeRefreshArtist,
                    binding.recyclerViewArtists,
                    binding.errorLayout.errorContainer,
                    errorMessage = "${getString(R.string.error_de_conexi_n)}\n\n$error",
                    errorIconRes = R.drawable.ic_no_connection
                )
            }
        }
    }

    private fun showEmptyState() {
        ErrorHandler.showEmptyState(
            binding.swipeRefreshArtist,
            binding.recyclerViewArtists,
            binding.errorLayout.errorContainer,
            emptyMessage = getString(R.string.error_create_artist),
            emptyIconRes = R.drawable.ic_empty_list
        )
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
        val title = if (showingFavorites) getString(R.string.artistas_favoritos) else
            getString(R.string.todos_los_artistas)
        val spannableTitle = SpannableString(title).apply {
            if (showingFavorites) {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gold
                        )
                    ), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.title = spannableTitle

        val listToShow = if (showingFavorites) {
            allArtists.filter { FavoritesManager.isFavorite(requireContext(), it.id) }
        } else {
            allArtists
        }

        if (listToShow.isEmpty() && showingFavorites) {
            ErrorHandler.showEmptyState(
                binding.swipeRefreshArtist,
                binding.recyclerViewArtists,
                binding.errorLayout.errorContainer,
                emptyMessage = getString(R.string.artist_no_favorites),
                emptyIconRes = R.drawable.ic_empty_list
            )
        } else {
            binding.errorLayout.errorContainer.visibility = View.GONE
            binding.recyclerViewArtists.visibility = View.VISIBLE
        }

        artistAdapter.updateArtists(listToShow)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
