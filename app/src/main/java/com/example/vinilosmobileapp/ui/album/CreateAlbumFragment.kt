package com.example.vinilosmobileapp.ui.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentCreateAlbumBinding
import com.example.vinilosmobileapp.datasource.remote.AlbumServiceAdapter
import com.example.vinilosmobileapp.model.Album
import com.example.vinilosmobileapp.model.dto.AlbumCreateDTO
import com.example.vinilosmobileapp.model.Collector
import com.example.vinilosmobileapp.model.Comment
import com.example.vinilosmobileapp.model.Track
import com.example.vinilosmobileapp.model.dto.CollectorReferenceDTO
import com.example.vinilosmobileapp.model.dto.CommentCreateDTO
import com.example.vinilosmobileapp.model.dto.TrackCreateDTO
import com.example.vinilosmobileapp.ui.album.adapter.CommentInputAdapter
import com.example.vinilosmobileapp.ui.album.adapter.TrackInputAdapter
import com.example.vinilosmobileapp.utils.RandomDataProvider
import com.example.vinilosmobileapp.utils.RandomDataProvider.randomComments
import com.example.vinilosmobileapp.utils.RandomDataProvider.randomDuration
import com.example.vinilosmobileapp.utils.RandomDataProvider.randomSongs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreateAlbumFragment : Fragment() {

    private var _binding: FragmentCreateAlbumBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateAlbumViewModel by viewModels()

    private var selectedCoverUrl: String? = null
    private var currentCollectors: List<Collector> = emptyList()
    private lateinit var commentInputAdapter: CommentInputAdapter
    private lateinit var trackInputAdapter: TrackInputAdapter

    private val genreOptions = RandomDataProvider.genreOptions
    private val artistOptions = RandomDataProvider.artistOptions

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
        setupAdapters()
        fetchCollectors()

        binding.imageUploadContainer.setOnClickListener { loadRandomCoverImage() }
        binding.buttonCreateAlbum.setOnClickListener { validateAndCreateAlbum() }
        binding.buttonAddComment.setOnClickListener { showAddCommentDialog() }
        binding.buttonAddTrack.setOnClickListener { showAddTrackDialog() }

        viewModel.createAlbumResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Álbum creado exitosamente", Toast.LENGTH_LONG)
                    .show()
                parentFragmentManager.setFragmentResult("album_created", Bundle())
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Error al crear el álbum", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun setupDropdowns() {
        binding.dropdownGenre.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                genreOptions
            )
        )
        binding.dropdownArtist.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                artistOptions
            )
        )
    }

    private fun setupYearPicker() {
        binding.inputAlbumYear.inputType = android.text.InputType.TYPE_NULL
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (1920..currentYear).toList().reversed().map { it.toString() }.toTypedArray()

        binding.inputAlbumYear.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Selecciona el año de lanzamiento")
                .setItems(years) { dialog, which ->
                    binding.inputAlbumYear.setText(years[which])
                    dialog.dismiss()
                }
                .show()
        }
        binding.inputAlbumYear.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) v.performClick() }
    }

    private fun setupAdapters() {
        trackInputAdapter = TrackInputAdapter(emptyList())
        binding.recyclerViewTracks.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = trackInputAdapter
        }
        commentInputAdapter = CommentInputAdapter(emptyList())
        binding.recyclerViewComments.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = commentInputAdapter
        }

    }

    private fun loadRandomCoverImage() {
        val randomImageUrl = "https://picsum.photos/800/600?random=${System.currentTimeMillis()}"
        binding.imagePreview.apply {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            scaleType = ImageView.ScaleType.CENTER_CROP
            load(randomImageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_loading_2)
                error(R.drawable.ic_failed_to_load_image)
            }
        }
        selectedCoverUrl = randomImageUrl
    }

    private fun validateAndCreateAlbum() {
        var isValid = true

        val name = binding.inputAlbumName.text.toString().trim().replaceFirstChar { it.uppercase() }
        val year = binding.inputAlbumYear.text.toString().trim()
        val genre = binding.dropdownGenre.text.toString().trim()
        val artist = binding.dropdownArtist.text.toString().trim()

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

        if (!isValid) {
            Log.e("CreateAlbum", "❌ Validación fallida: Campos obligatorios vacíos")
            return
        }

        val coverUrl = selectedCoverUrl ?: "https://http.cat/images/102.jpg"
        val description = binding.inputAlbumDescription.text?.toString()?.trim()
            ?: "Álbum creado desde la app móvil."
        val releaseDateFormatted = "$year-01-01"

        val albumCreateDTO = AlbumCreateDTO(
            name = name,
            cover = coverUrl,
            releaseDate = releaseDateFormatted,
            description = if (description.isEmpty())
                "Álbum creado desde la app móvil."
            else description,
            genre = genre,
            recordLabel = artist
        )

        Log.d("CreateAlbum", "🚀 Creando álbum: $albumCreateDTO")

        AlbumServiceAdapter.createAlbum(albumCreateDTO).enqueue(object : Callback<Album> {
            override fun onResponse(call: Call<Album>, response: Response<Album>) {
                if (response.isSuccessful) {
                    val createdAlbum = response.body()
                    if (createdAlbum != null) {
                        Log.i("CreateAlbum", "✅ Álbum creado con ID: ${createdAlbum.id}")
                        sendCommentsAndTracks(createdAlbum.id)
                    }
                } else {
                    Log.e(
                        "CreateAlbum",
                        "❌ Error al crear álbum: ${response.code()} - ${
                            response.errorBody()?.string()
                        }"
                    )
                    Toast.makeText(
                        requireContext(),
                        "Error al crear el álbum",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Album>, t: Throwable) {
                Log.e("CreateAlbum", "❌ Error de red al crear álbum: ${t.localizedMessage}")
                Toast.makeText(
                    requireContext(),
                    "Error de red al crear el álbum",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun sendCommentsAndTracks(albumId: Int) {
        var pendingRequests = 0
        var hasErrors = false

        // Enviar comentarios
        for (comment in commentInputAdapter.getComments()) {
            pendingRequests++
            val commentDTO = CommentCreateDTO(
                description = comment.description,
                rating = comment.rating,
                collector = CollectorReferenceDTO(comment.collector?.id ?: 1) // Default ID
            )
            AlbumServiceAdapter.addCommentToAlbum(albumId, commentDTO)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        pendingRequests--
                        if (!response.isSuccessful) {
                            hasErrors = true
                            Log.e(
                                "CreateAlbum",
                                "❌ Error al crear comentario: ${response.code()} - ${
                                    response.errorBody()?.string()
                                }"
                            )
                        }
                        checkPendingRequests(pendingRequests, hasErrors)
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        pendingRequests--
                        hasErrors = true
                        Log.e(
                            "CreateAlbum",
                            "❌ Error de red al crear comentario: ${t.localizedMessage}"
                        )
                        checkPendingRequests(pendingRequests, hasErrors)
                    }
                })
        }

        // Enviar tracks
        for (track in trackInputAdapter.getTracks()) {
            pendingRequests++
            val trackDTO =
                TrackCreateDTO(name = track.name, duration = track.duration ?: "3:30 min")
            AlbumServiceAdapter.addTrackToAlbum(albumId, trackDTO).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    pendingRequests--
                    if (!response.isSuccessful) {
                        hasErrors = true
                        Log.e(
                            "CreateAlbum",
                            "❌ Error al crear track: ${response.code()} - ${
                                response.errorBody()?.string()
                            }"
                        )
                    }
                    checkPendingRequests(pendingRequests, hasErrors)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    pendingRequests--
                    hasErrors = true
                    Log.e("CreateAlbum", "❌ Error de red al crear track: ${t.localizedMessage}")
                    checkPendingRequests(pendingRequests, hasErrors)
                }
            })
        }

        // Si no hay comentarios ni tracks, verificar inmediatamente
        if (pendingRequests == 0) {
            checkPendingRequests(pendingRequests, hasErrors)
        }
    }

    private fun checkPendingRequests(pending: Int, hasErrors: Boolean) {
        if (pending == 0) {
            if (hasErrors) {
                Toast.makeText(
                    requireContext(),
                    "Álbum creado, pero hubo errores al agregar comentarios o canciones",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Álbum y contenido creado exitosamente",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Notificar al HomeFragment y regresar
            parentFragmentManager.setFragmentResult("album_created", Bundle())
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun createGuestCollector(onGuestCreated: (Int) -> Unit) {
        val guestName = "Anónimo ${System.currentTimeMillis()}"
        AlbumServiceAdapter.createCollector(
            name = guestName,
            telephone = "000-0000000",
            email = "$guestName@example.com"
        ).enqueue(object : Callback<Collector> {
            override fun onResponse(call: Call<Collector>, response: Response<Collector>) {
                if (response.isSuccessful) {
                    val guestCollector = response.body()
                    if (guestCollector != null) {
                        Log.i("CreateAlbum", "✅ Guest creado con ID: ${guestCollector.id}")
                        onGuestCreated(guestCollector.id)
                    } else {
                        Log.e("CreateAlbum", "❌ Error creando guest: respuesta vacía")
                    }
                } else {
                    Log.e("CreateAlbum", "❌ Error creando guest: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Collector>, t: Throwable) {
                Log.e("CreateAlbum", "❌ Error de red creando guest: ${t.localizedMessage}")
            }
        })
    }


    private fun fetchCollectors() {
        AlbumServiceAdapter.getCollectors().enqueue(object : Callback<List<Collector>> {
            override fun onResponse(
                call: Call<List<Collector>>,
                response: Response<List<Collector>>
            ) {
                if (response.isSuccessful) {
                    currentCollectors = response.body() ?: emptyList()
                } else {
                    currentCollectors = emptyList()
                }
            }

            override fun onFailure(call: Call<List<Collector>>, t: Throwable) {
                currentCollectors = emptyList()
            }
        })
    }

    private fun showAddCommentDialog() {
        val context = requireContext()
        val builder = MaterialAlertDialogBuilder(context)

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_comment, null)
        builder.setView(dialogView)

        val inputCommentLayout =
            dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.inputCommentLayout)
        val inputComment =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputComment)

        val inputAuthor =
            dialogView.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(
                R.id.inputAuthor
            )

        val guestHint = dialogView.findViewById<TextView>(R.id.guest_hint)
        inputComment.setText(randomComments.random())

        // 🔵 Configurar Dropdown con los collectors
        val collectorsNames = currentCollectors.map { it.name }
        if (collectorsNames.isNotEmpty()) {
            val adapter =
                ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    collectorsNames
                )
            inputAuthor.setAdapter(adapter)
            guestHint.visibility = View.GONE
        } else {
            guestHint.visibility = View.VISIBLE
        }

        builder.setTitle("Agregar Comentario")
            .setPositiveButton("Agregar", null) // Validación manual después
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()

        // 👀 Validación manual en positivo
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                val commentText = inputComment.text?.toString()?.trim() ?: ""

                // Validar comentario obligatorio
                if (commentText.isEmpty()) {
                    inputCommentLayout.error = "El comentario es obligatorio"
                    return@setOnClickListener
                } else {
                    inputCommentLayout.error = null
                }

                // Capturar autor
                val selectedAuthor = inputAuthor.text?.toString()?.trim()

                val selectedCollector = currentCollectors.find { it.name == selectedAuthor }
                val collectorId = selectedCollector?.id ?: 1 // Default ID 1 si no existe
                val authorName = if (!selectedAuthor.isNullOrEmpty()) {
                    selectedAuthor
                } else {
                    "Anónimo"
                }

                // Crear el comentario
                val comment = Comment(
                    id = 0,
                    description = commentText,
                    rating = 5,
                    collector = Collector(id = collectorId, name = authorName)
                )

                commentInputAdapter.addComment(comment)

                dialog.dismiss()
            }
    }

    private fun showAddTrackDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_track, null)
        val inputTrackName =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.input_track_name
            )
        val inputTrackDuration =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.input_track_duration
            )

        // Asignar una canción aleatoria al campo de nombre
        inputTrackName.setText(randomSongs.random())
        inputTrackDuration.setText(randomDuration())

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Agregar Canción")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val trackName = inputTrackName.text.toString().trim()
                val trackDuration =
                    inputTrackDuration.text.toString().trim().ifEmpty { null }

                if (trackName.isNotEmpty()) {
                    trackInputAdapter.addTrack(
                        Track(id = 1, name = trackName, duration = trackDuration)
                    )
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
