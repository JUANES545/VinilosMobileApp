package com.example.vinilosmobileapp.ui.artist

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentCreateArtistBinding
import com.example.vinilosmobileapp.datasource.remote.AlbumServiceAdapter
import com.example.vinilosmobileapp.datasource.remote.ArtistServiceAdapter
import com.example.vinilosmobileapp.model.Album
import com.example.vinilosmobileapp.model.Prize
import com.example.vinilosmobileapp.model.dto.ArtistCreateDTO
import com.example.vinilosmobileapp.ui.artist.adapter.PrizeInputAdapter
import com.example.vinilosmobileapp.ui.home.adapter.AlbumAdapter
import com.example.vinilosmobileapp.utils.RandomDataProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CreateArtistFragment : Fragment() {

    private var _binding: FragmentCreateArtistBinding? = null
    private val binding get() = _binding!!
    private val vm: CreateArtistViewModel by viewModels()
    private val cal = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000'Z'", Locale.getDefault())
    private var selectedPhotoUrl: String? = null

    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var prizeAdapter: PrizeInputAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentCreateArtistBinding.inflate(i, c, false).also { _binding = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        setupArtistName()
        setupBirthDateCalendar()
        setupDescription()
        setupAdapters()

        binding.imageUploadContainer.setOnClickListener { loadRandomProfilePhoto() }

        binding.buttonAddAlbum.setOnClickListener { showAddAlbumDialog() }
        binding.buttonAddPrize.setOnClickListener { showAddPrizeDialog() }

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

        prizeAdapter = PrizeInputAdapter(mutableListOf())
        binding.recyclerViewPrizes.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = prizeAdapter
        }
    }

    private fun showAddAlbumDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_album, null)
        val dropdownAlbum =
            dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.dropdown_album)
        val guestHint = dialogView.findViewById<TextView>(R.id.guest_hint)

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona un álbum")
            .setView(dialogView)
            .setPositiveButton("Agregar", null)
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()

        AlbumServiceAdapter.getAlbums().enqueue(object : Callback<List<Album>> {
            override fun onResponse(call: Call<List<Album>>, resp: Response<List<Album>>) {
                val list = resp.body().orEmpty()
                val filteredList = list.filter { album ->
                    albumAdapter.albumList.none { it.id == album.id }
                }
                if (filteredList.isEmpty()) {
                    guestHint.visibility = View.VISIBLE
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
                } else {
                    guestHint.visibility = View.GONE
                    val names = filteredList.map { it.name }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        names
                    )
                    dropdownAlbum.setAdapter(adapter)
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = true

                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        val selected = dropdownAlbum.text.toString()
                        val index = names.indexOf(selected)
                        if (index >= 0) {
                            // Añadimos el álbum al adaptador y actualizamos la lista
                            val selectedAlbum = filteredList[index]
                            val updatedList = albumAdapter.albumList.toMutableList().apply {
                                add(selectedAlbum)
                            }
                            albumAdapter.updateAlbums(updatedList)
                            dialog.dismiss()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Album>>, t: Throwable) {
                Toast.makeText(context, "Error cargando álbumes", Toast.LENGTH_SHORT).show()
                guestHint.text = "No se pudieron cargar los álbumes."
                guestHint.visibility = View.VISIBLE
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
            }
        })
    }


    private fun showAddPrizeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_prize, null)
        val prizeDropdown =
            dialogView.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(
                R.id.dropdownPrize
            )
        val inputDate =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputPrizeDate)
                .apply {
                    val randomCalendar = Calendar.getInstance().apply {
                        set((1950..2023).random(), (0..11).random(), (1..28).random())
                    }
                    setText(
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                            randomCalendar.time
                        )
                    )
                }

        // Pre-cargar lista de premios
        ArtistServiceAdapter.getPrizes().enqueue(object : retrofit2.Callback<List<Prize>> {
            override fun onResponse(
                call: retrofit2.Call<List<Prize>>,
                resp: retrofit2.Response<List<Prize>>
            ) {
                val list = resp.body().orEmpty()
                val names =
                    if (list.isNotEmpty()) list.map { it.name } else listOf("No hay premios disponibles")
                prizeDropdown.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        names
                    )
                )
            }

            override fun onFailure(call: retrofit2.Call<List<Prize>>, t: Throwable) {
                Toast.makeText(
                    context,
                    "No se pudieron obtener datos del servidor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Agregar Premio")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val prizeName = prizeDropdown.text.toString()
                val date = inputDate.text.toString().trim().ifEmpty { null }
                prizeAdapter.addPrize(prizeName, date)
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
