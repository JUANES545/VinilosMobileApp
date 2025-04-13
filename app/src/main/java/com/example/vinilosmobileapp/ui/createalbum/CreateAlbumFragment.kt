package com.example.vinilosmobileapp.ui.createalbum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vinilosmobileapp.databinding.FragmentCreateAlbumBinding

class CreateAlbumFragment : Fragment() {

    private var _binding: FragmentCreateAlbumBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateAlbumViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreateAlbum.setOnClickListener {
            val name = binding.inputAlbumName.text.toString()
            val year = binding.inputAlbumYear.text.toString()
            //val artist = binding.inputAlbumArtist.text.toString()
            //val genre = binding.inputAlbumGenre.text.toString()

//            if (name.isBlank() || year.isBlank() || artist.isBlank() || genre.isBlank()) {
//                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }

            viewModel.submitAlbum(name, year.toInt(), "artist", "genre")
        }

        // Observing success
        viewModel.albumCreated.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Album created successfully!", Toast.LENGTH_LONG)
                    .show()
                // Optionally navigate back
                // findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
