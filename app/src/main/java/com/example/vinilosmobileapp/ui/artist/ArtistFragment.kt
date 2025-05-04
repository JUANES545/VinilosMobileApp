package com.example.vinilosmobileapp.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentArtistBinding
import com.example.vinilosmobileapp.ui.artist.adapter.ArtistAdapter

class ArtistFragment : Fragment() {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistViewModel by viewModels()
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArtistBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupRetryButton()
        observeViewModel()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
