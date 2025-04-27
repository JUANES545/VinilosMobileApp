package com.example.vinilosmobileapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentHomeBinding
import com.example.vinilosmobileapp.ui.home.adapter.AlbumAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

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

        albumAdapter = AlbumAdapter(emptyList()) { albumId ->
            val bundle = Bundle().apply {
                putInt("albumId", albumId)
            }
            findNavController().navigate(R.id.detailAlbumFragment, bundle)
        }

        binding.recyclerViewAlbums.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerViewAlbums.adapter = albumAdapter

        binding.buttonRetry.setOnClickListener {
            reloadAlbums(forceReload = true)
        }

        reloadAlbums()

        binding.fabCreateAlbum.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createAlbumFragment)
        }
    }

    private fun reloadAlbums(forceReload: Boolean = false) {
        if (!forceReload) {
            binding.shimmerLayout.visibility = View.VISIBLE
            binding.shimmerLayout.startShimmer()
        }

        binding.imageError.visibility = View.GONE
        binding.textError.visibility = View.GONE
        binding.buttonRetry.visibility = View.GONE
        binding.recyclerViewAlbums.visibility = View.GONE

        // Limpia observers anteriores antes de observar de nuevo
        homeViewModel.albums.removeObservers(viewLifecycleOwner)

        homeViewModel.albums.observe(viewLifecycleOwner, Observer { albums ->
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE

            if (albums != null) {
                if (albums.isEmpty()) {
                    showEmptyState()
                } else {
                    showAlbums(albums)
                }
            } else {
                showErrorState()
            }
        })
    }


    private fun showAlbums(albums: List<com.example.vinilosmobileapp.model.Album>) {
        binding.recyclerViewAlbums.visibility = View.VISIBLE
        binding.imageError.visibility = View.GONE
        binding.textError.visibility = View.GONE
        binding.buttonRetry.visibility = View.GONE

        albumAdapter.updateAlbums(albums)
    }

    private fun showErrorState() {
        binding.recyclerViewAlbums.visibility = View.GONE

        binding.imageError.setImageResource(R.drawable.ic_no_connection) // imagen de error de conexión
        binding.textError.text = "Error de conexión. Verifica tu internet."
        binding.buttonRetry.visibility = View.VISIBLE

        showFadeIn(binding.imageError)
        showFadeIn(binding.textError)
        showFadeIn(binding.buttonRetry)
    }

    private fun showEmptyState() {
        binding.recyclerViewAlbums.visibility = View.GONE

        binding.imageError.setImageResource(R.drawable.ic_empty_list) // imagen para lista vacía
        binding.textError.text = "No hay álbumes disponibles."
        binding.buttonRetry.visibility = View.GONE // No mostramos retry en lista vacía

        showFadeIn(binding.imageError)
        showFadeIn(binding.textError)
    }

    private fun showFadeIn(view: View) {
        view.visibility = View.VISIBLE
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 500
        view.startAnimation(fadeIn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
