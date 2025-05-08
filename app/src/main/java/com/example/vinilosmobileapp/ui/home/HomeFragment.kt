package com.example.vinilosmobileapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentHomeBinding
import com.example.vinilosmobileapp.ui.home.adapter.AlbumAdapter
import com.example.vinilosmobileapp.utils.ErrorHandler

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            "album_created",
            viewLifecycleOwner
        ) { _, _ ->
            refreshAlbums()
        }

        setupRecyclerView()
        setupSwipeRefresh()
        setupRetryButton()

        observeAlbums()
        observeErrors()

        binding.fabCreateAlbum.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createAlbumFragment)
        }
    }

    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter(emptyList()) { albumId ->
            val bundle = Bundle().apply { putInt("albumId", albumId) }
            findNavController().navigate(R.id.detailAlbumFragment, bundle)
        }
        binding.recyclerViewAlbums.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerViewAlbums.adapter = albumAdapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshAlbums()
        }
    }

    private fun setupRetryButton() {
        binding.errorLayout.buttonRetry.setOnClickListener {
            refreshAlbums()
        }
    }

    private fun observeAlbums() {
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            binding.swipeRefresh.isRefreshing = false

            if (albums != null && albums.isNotEmpty()) {
                val sortedAlbums = albums.sortedByDescending { it.id }

                binding.recyclerViewAlbums.visibility = View.VISIBLE
                binding.errorLayout.errorContainer.visibility = View.GONE
                albumAdapter.updateAlbums(sortedAlbums)
            } else if (albums != null && albums.isEmpty()) {
                showEmptyState()
            }
        }
    }

    private fun observeErrors() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                ErrorHandler.showErrorState(
                    binding.swipeRefresh,
                    binding.recyclerViewAlbums,
                    binding.errorLayout.errorContainer,
                    errorMessage = getString(R.string.error_connection, error),
                    errorIconRes = R.drawable.ic_no_connection
                )
            }
        }
    }

    private fun showEmptyState() {
        ErrorHandler.showEmptyState(
            binding.swipeRefresh,
            binding.recyclerViewAlbums,
            binding.errorLayout.errorContainer,
            emptyMessage = getString(R.string.error_create_album),
            emptyIconRes = R.drawable.ic_empty_list
        )
    }

    private fun refreshAlbums() {
        binding.swipeRefresh.isRefreshing = true
        viewModel.fetchAlbums()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
