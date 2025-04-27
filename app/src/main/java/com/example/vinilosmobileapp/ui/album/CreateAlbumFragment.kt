package com.example.vinilosmobileapp.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentCreateAlbumBinding
import com.example.vinilosmobileapp.model.AlbumCreateDTO
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class CreateAlbumFragment : Fragment() {

    private var _binding: FragmentCreateAlbumBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateAlbumViewModel by viewModels()
    private var selectedCoverUrl: String? = null

    private val genreOptions = listOf("Classical", "Salsa", "Rock", "Folk")
    private val artistOptions =
        listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.create_new_album)

        setupDropdowns()
        setupYearPicker()

        binding.imageUploadContainer.setOnClickListener {
            loadRandomCoverImage()
        }

        binding.buttonCreateAlbum.setOnClickListener {
            validateAndCreateAlbum()
        }

        viewModel.createAlbumResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Álbum creado exitosamente", Toast.LENGTH_LONG)
                    .show()

                // 👇 Enviar resultado de éxito
                parentFragmentManager.setFragmentResult("album_created", Bundle())

                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Error al crear el álbum", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun setupDropdowns() {
        val genreAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            genreOptions
        )
        binding.dropdownGenre.setAdapter(genreAdapter)

        val artistAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            artistOptions
        )
        binding.dropdownArtist.setAdapter(artistAdapter)
    }

    private fun setupYearPicker() {
        // Evitar teclado manual
        binding.inputAlbumYear.inputType = android.text.InputType.TYPE_NULL

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (1920..currentYear).toList().reversed() // De más reciente a más antiguo

        val yearsAsString = years.map { it.toString() }.toTypedArray()

        binding.inputAlbumYear.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Selecciona el año de lanzamiento")
                .setItems(yearsAsString) { dialog, which ->
                    binding.inputAlbumYear.setText(yearsAsString[which])
                    dialog.dismiss()
                }
                .show()
        }

        binding.inputAlbumYear.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.performClick()
            }
        }
    }

    private fun loadRandomCoverImage() {
        val randomImageUrl = "https://picsum.photos/800/600?random=${System.currentTimeMillis()}"

        val layoutParams = binding.imagePreview.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.imagePreview.layoutParams = layoutParams

        binding.imagePreview.scaleType = ImageView.ScaleType.CENTER_CROP

        binding.imagePreview.load(randomImageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_search)
            error(R.drawable.ic_failed_to_load_image)
        }

        selectedCoverUrl = randomImageUrl
    }


    private fun validateAndCreateAlbum() {
        var isValid = true

        val name = binding.inputAlbumName.text.toString().trim().replaceFirstChar { it.uppercase() }
        val year = binding.inputAlbumYear.text.toString().trim()
        val genre = binding.dropdownGenre.text.toString().trim()
        val artist = binding.dropdownArtist.text.toString().trim()

        // Limpiar errores anteriores
        binding.inputAlbumNameLayout.error = null
        binding.inputAlbumYearLayout.error = null
        binding.dropdownGenreLayout.error = null
        binding.dropdownArtistLayout.error = null

        if (name.isEmpty()) {
            binding.inputAlbumNameLayout.error = "Este campo es obligatorio"
            isValid = false
        }

        if (year.isEmpty()) {
            binding.inputAlbumYearLayout.error = "Este campo es obligatorio"
            isValid = false
        }

        if (genre.isEmpty()) {
            binding.dropdownGenreLayout.error = "Selecciona un género"
            isValid = false
        }

        if (artist.isEmpty()) {
            binding.dropdownArtistLayout.error = "Selecciona un sello discográfico"
            isValid = false
        }

        if (!isValid) return

        val coverUrl = selectedCoverUrl ?: "https://http.cat/images/102.jpg"
        val description = "Álbum creado desde la app móvil."
        val releaseDateFormatted = "$year-01-01"

        val albumCreateDTO = AlbumCreateDTO(
            name = name,
            cover = coverUrl,
            releaseDate = releaseDateFormatted,
            description = description,
            genre = genre,
            recordLabel = artist
        )

        viewModel.createAlbum(albumCreateDTO)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
