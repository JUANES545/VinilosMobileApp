package com.example.vinilosmobileapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentHomeBinding
import com.example.vinilosmobileapp.datasource.local.AlbumProvider
import com.example.vinilosmobileapp.ui.home.adapter.AlbumAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        val albumAdapter = AlbumAdapter(AlbumProvider.getAlbums()) { albumId ->
            val bundle = Bundle().apply {
                putInt("albumId", albumId)
            }
            findNavController().navigate(R.id.detailAlbumFragment, bundle)
        }

        binding.recyclerViewAlbums.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerViewAlbums.adapter = albumAdapter

        binding.fabCreateAlbum.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createAlbumFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
