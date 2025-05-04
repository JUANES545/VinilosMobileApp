package com.example.vinilosmobileapp.ui.artist

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentCreateArtistBinding
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import com.example.vinilosmobileapp.utils.RandomDataProvider
import java.text.SimpleDateFormat
import java.util.*

class CreateArtistFragment : Fragment() {

    private var _binding: FragmentCreateArtistBinding? = null
    private val binding get() = _binding!!
    private val vm: CreateArtistViewModel by viewModels()
    private val cal = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000'Z'", Locale.getDefault())
    private var selectedPhotoUrl: String? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentCreateArtistBinding.inflate(i, c, false).also { _binding = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        setupArtistName()
        setupBirthDateCalendar()
        setupDescription()

        binding.imageUploadContainer.setOnClickListener { loadRandomProfilePhoto() }

        binding.btnCreateArtist.setOnClickListener {
            var valid = true
            listOf(binding.tilName, binding.tilBirth, binding.tilDesc).forEach { it.error = null }

            if (binding.etName.text.isNullOrBlank()) {
                binding.tilName.error = "Nombre obligatorio"; valid = false
            }
            if (binding.etBirth.text.isNullOrBlank()) {
                binding.tilBirth.error = "Fecha de nacimiento Requerida"; valid = false
            }
            if (binding.etDesc.text.isNullOrBlank()) {
                binding.tilDesc.error = "Descripción Requerido"; valid = false
            }
            if (!valid) return@setOnClickListener

            val dto = ArtistCreateDTO(
                name = binding.etName.text.toString().trim(),
                image = selectedPhotoUrl ?: "https://http.cat/images/102.jpg",
                birthDate = sdf.format(cal.time),
                description = binding.etDesc.text.toString().trim()
            )

            vm.createArtist(dto)
        }

        vm.createResult.observe(viewLifecycleOwner) { ok ->
            if (ok) {
                Toast.makeText(requireContext(), "Artista creado", Toast.LENGTH_SHORT).show()
                // avisamos para que el ArtistFragment refresque
                setFragmentResult("artist_created", Bundle())
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Error al crear artista", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupArtistName() {
        var isFirstClick = true
        binding.etName.setOnClickListener {
            if (isFirstClick && binding.etName.text.isNullOrBlank()) {
                val randomName = RandomDataProvider.artistName.random()
                binding.etName.setText(randomName)
                isFirstClick = false
            }
        }
    }

    private fun setupBirthDateCalendar() {
        // Generar una fecha aleatoria entre 1955 y 2006
        val randomYear = (1955..2006).random()
        val randomMonth = (0..11).random()
        val randomDay = (1..cal.getActualMaximum(Calendar.DAY_OF_MONTH)).random()
        cal.set(randomYear, randomMonth, randomDay)

        // Muestra fecha aleatoria en el campo al abrir por primera vez
        binding.etBirth.setText(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(cal.time)
        )

        binding.etBirth.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    cal.set(y, m, d)
                    binding.etBirth.setText(
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(cal.time)
                    )
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupDescription() {
        var isFirstClick = true
        binding.etDesc.setOnClickListener {
            if (isFirstClick && binding.etDesc.text.isNullOrBlank()) {
                val randomDescription = RandomDataProvider.artistDescriptions.random()
                binding.etDesc.setText(randomDescription)
                isFirstClick = false
            }
        }
    }

    private fun loadRandomProfilePhoto() {
        val randomImageUrl = "https://i.pravatar.cc/300?u=${System.currentTimeMillis()}"

        binding.imagePreview.apply {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            scaleType = ImageView.ScaleType.CENTER_CROP
            load(randomImageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_loading_2)
                error(R.drawable.ic_profile)
            }
        }
        selectedPhotoUrl = randomImageUrl
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
