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
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentCreateArtistBinding
import com.example.vinilosmobileapp.model.PerformerPrize
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import com.example.vinilosmobileapp.ui.artist.adapter.PrizeInputAdapter
import com.example.vinilosmobileapp.ui.home.adapter.AlbumAdapter
import com.example.vinilosmobileapp.utils.DialogHelper
import com.example.vinilosmobileapp.utils.DialogHelper.hideKeyboard
import com.example.vinilosmobileapp.utils.RandomDataProvider
import java.text.SimpleDateFormat
import java.util.*

class CreateArtistFragment : Fragment() {

    private var _binding: FragmentCreateArtistBinding? = null
    val binding get() = _binding!!
    private val vm: CreateArtistViewModel by viewModels()
    private val cal = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000'Z'", Locale.getDefault())
    private var selectedPhotoUrl: String? = null

    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var prizeInputAdapter: PrizeInputAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentCreateArtistBinding.inflate(i, c, false).also { _binding = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        setupArtistName()
        setupBirthDateCalendar()
        setupDescription()
        setupAdapters()
        binding.imageUploadContainer.setOnClickListener { loadRandomProfilePhoto() }
        binding.buttonAddAlbum.setOnClickListener {
            DialogHelper.showAddAlbumDialog(
                layoutInflater = layoutInflater,
                lifecycleOwner = viewLifecycleOwner,
                viewModel = vm,
                albumAdapter = albumAdapter
            )
        }
        binding.buttonAddPrize.setOnClickListener {
            DialogHelper.showAddPrizeDialog(
                layoutInflater = layoutInflater,
                lifecycleOwner = viewLifecycleOwner,
                viewModel = vm,
                prizeAdapter = prizeInputAdapter
            )
        }
        binding.btnCreateArtist.setOnClickListener {
            sendAlbumData()
        }

        vm.createResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Artista creado", Toast.LENGTH_SHORT).show()
                sendAlbumDataComplements()
                setFragmentResult("artist_created", Bundle())
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Error al crear artista", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        vm.prizes.observe(viewLifecycleOwner) { prizeList ->
            prizeInputAdapter.updatePrizes(prizeList ?: emptyList())
        }
        vm.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendAlbumData() {
        if (validateInputs()) {
            val dto = ArtistCreateDTO(
                name = binding.etName.text.toString().trim(),
                image = selectedPhotoUrl ?: "https://http.cat/images/102.jpg",
                birthDate = sdf.format(cal.time),
                description = binding.etDesc.text.toString().trim()
            )
            // Step 1: Send artist data to the server
            vm.createArtist(dto)
        }
    }

    private fun sendAlbumDataComplements() {
        // Step 2: Observe the result and send albums/prizes if lists are not empty
        val createdArtistId = vm.getCreatedArtistId()

        // Send albums if the list is not empty
        val selectedAlbums = albumAdapter.getSelectedAlbums()
        if (selectedAlbums.isNotEmpty() && createdArtistId != null) {
            vm.addAlbumsToArtist(createdArtistId, selectedAlbums)
        }

        // Send prizes if the list is not empty
        val selectedPrizes = prizeInputAdapter.getSelectedPrizes()
        if (selectedPrizes.isNotEmpty() && createdArtistId != null) {
            vm.addPrizesToArtist(createdArtistId, selectedPrizes)
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true
        listOf(binding.tilName, binding.tilBirth, binding.tilDesc).forEach { it.error = null }

        if (binding.etName.text.isNullOrBlank()) {
            binding.tilName.error = "Nombre obligatorio"; valid = false
        }
        if (binding.etBirth.text.isNullOrBlank()) {
            binding.tilBirth.error = "Fecha de nacimiento requerida"; valid = false
        }
        if (binding.etDesc.text.isNullOrBlank()) {
            binding.tilDesc.error = "Descripción requerida"; valid = false
        }
        return valid
    }

    private fun setupAdapters() {
        // Reutilizamos el AlbumAdapter de la carpeta home
        albumAdapter = AlbumAdapter(emptyList()) { albumId ->
            Toast.makeText(requireContext(), "Álbum seleccionado: $albumId", Toast.LENGTH_SHORT)
                .show()
        }

        binding.recyclerViewAlbums.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = albumAdapter
        }

        // Initialize PrizeInputAdapter with performerPrizes and prizes
        val performerPrizes = mutableListOf<PerformerPrize>() // Replace with actual data
       // val prizes = vm.prizes.value ?: emptyList() // Fetch prizes from ViewModel
        vm.fetchPrizes() // Fetch prizes from ViewModel
        val prizes = vm.prizes.value ?: emptyList() // Fetch prizes from ViewModel
        prizeInputAdapter = PrizeInputAdapter(performerPrizes, prizes)

        binding.recyclerViewPrizes.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = prizeInputAdapter
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
        hideKeyboard(requireContext())

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
