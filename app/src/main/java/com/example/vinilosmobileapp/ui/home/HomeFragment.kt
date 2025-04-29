package com.example.vinilosmobileapp.ui.home

import android.os.Bundle
import android.text.Html
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
        binding.buttonRetry.setOnClickListener {
            refreshAlbums()
        }
    }

    private fun observeAlbums() {
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            binding.swipeRefresh.isRefreshing = false

            if (albums != null && albums.isNotEmpty()) {
                val sortedAlbums = albums.sortedByDescending { it.id }

                binding.recyclerViewAlbums.visibility = View.VISIBLE
                binding.textError.visibility = View.GONE
                binding.imageError.visibility = View.GONE
                binding.buttonRetry.visibility = View.GONE
                albumAdapter.updateAlbums(sortedAlbums)
            } else if (albums != null && albums.isEmpty()) {
                showEmptyState()
            }
        }
    }

    private fun observeErrors() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.swipeRefresh.isRefreshing = false
                binding.recyclerViewAlbums.visibility = View.GONE

                binding.textError.text = Html.fromHtml("<b>Error de conexión</b><br><br>$error")
                binding.textError.visibility = View.VISIBLE

                binding.imageError.setImageResource(R.drawable.ic_no_connection)
                binding.imageError.visibility = View.VISIBLE

                binding.buttonRetry.visibility = View.VISIBLE
            }
        }
    }

    private fun refreshAlbums() {
        binding.swipeRefresh.isRefreshing = true
        viewModel.fetchAlbums()
    }

    private fun showEmptyState() {
        binding.swipeRefresh.isRefreshing = false
        binding.recyclerViewAlbums.visibility = View.GONE

        binding.textError.text =
            Html.fromHtml("No hay álbumes disponibles<br><br><b>Crea uno nuevo.</b>")
        binding.textError.visibility = View.VISIBLE

        binding.imageError.setImageResource(R.drawable.ic_empty_list)
        binding.imageError.visibility = View.VISIBLE

        binding.buttonRetry.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
