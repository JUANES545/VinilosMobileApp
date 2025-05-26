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
    private val artistOptions = RandomDataProvider.recordLabels

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
            getString(R.string.album_create_new)

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
            description = description.ifEmpty { "Álbum creado desde la app móvil." },
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
        val comments = commentInputAdapter.getComments()
        val tracks = trackInputAdapter.getTracks()
        var totalPending = comments.size + tracks.size
        var hasErrors = false

        // Si no hay nada que enviar, volvemos ya
        if (totalPending == 0) {
            checkPendingRequests(0, false)
            return
        }

        // Helpers para decrementar y comprobar
        fun onCompleteSingle(error: Boolean) {
            totalPending--
            if (error) hasErrors = true
            checkPendingRequests(totalPending, hasErrors)
        }

        // 1) comments
        comments.forEach { comment ->
            // Si no hay collectors, crea uno antes de enviar
            val sendWithCollector: (Int) -> Unit = { collectorId ->
                val dto = CommentCreateDTO(
                    description = comment.description,
                    rating = comment.rating,
                    collector = CollectorReferenceDTO(collectorId)
                )
                AlbumServiceAdapter.addCommentToAlbum(albumId, dto)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, resp: Response<Void>) {
                            onCompleteSingle(!resp.isSuccessful)
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            onCompleteSingle(true)
                        }
                    })
            }

            if (currentCollectors.isEmpty()) {
                createGuestCollector { sendWithCollector(it) }
            } else {
                sendWithCollector(comment.collector?.id ?: currentCollectors.first().id)
            }
        }

        // 2) tracks
        tracks.forEach { track ->
            val dto = TrackCreateDTO(track.name, track.duration ?: "3:30 min")
            AlbumServiceAdapter.addTrackToAlbum(albumId, dto)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, resp: Response<Void>) {
                        onCompleteSingle(!resp.isSuccessful)
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        onCompleteSingle(true)
                    }
                })
        }
    }

    private fun checkPendingRequests(pending: Int, hasErrors: Boolean) {
        if (!isAdded) return
        if (pending == 0) {
            if (hasErrors) {
                Toast.makeText(
                    requireContext(),
                    "Álbum creado, pero hubo errores al agregar contenido",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Álbum y contenido creado exitosamente",
                    Toast.LENGTH_LONG
                ).show()
            }
            parentFragmentManager.setFragmentResult("album_created", Bundle())
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun createGuestCollector(onGuestCreated: (Int) -> Unit) {
        val guestName = "Anonimo${System.currentTimeMillis()}"
        val email = "${guestName}@guest.com"
        AlbumServiceAdapter.createCollector(
            name = guestName,
            telephone = "0000000000",
            email = email
        ).enqueue(object : Callback<Collector> {
            override fun onResponse(call: Call<Collector>, response: Response<Collector>) {
                if (response.isSuccessful) {
                    val guest = response.body()
                    if (guest != null) {
                        Log.i("CreateAlbum", "✅ Guest creado con ID: ${guest.id}")
                        onGuestCreated(guest.id)
                    } else {
                        Log.e("CreateAlbum", "❌ Error creando guest: cuerpo vacío")
                        onGuestCreated(currentCollectors.firstOrNull()?.id ?: 1)
                    }
                } else {
                    val err = response.errorBody()?.string()
                    Log.e("CreateAlbum", "❌ Error creando guest: ${response.code()} - $err")
                    onGuestCreated(currentCollectors.firstOrNull()?.id ?: 1)
                }
            }

            override fun onFailure(call: Call<Collector>, t: Throwable) {
                Log.e("CreateAlbum", "❌ Error de red creando guest: ${t.localizedMessage}")
                onGuestCreated(currentCollectors.firstOrNull()?.id ?: 1)
            }
        })
    }


    private fun fetchCollectors() {
        AlbumServiceAdapter.getCollectors().enqueue(object : Callback<List<Collector>> {
            override fun onResponse(
                call: Call<List<Collector>>,
                response: Response<List<Collector>>
            ) {
                currentCollectors = if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
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

        val inputCollector =
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
            inputCollector.setAdapter(adapter)
            guestHint.visibility = View.GONE
        } else {
            guestHint.visibility = View.VISIBLE
        }

        builder.setTitle("Agregar Comentario")
            .setPositiveButton("Agregar", null) // Validación manual después
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                val commentText = inputComment.text?.toString()?.trim() ?: ""

                if (commentText.isEmpty()) {
                    inputCommentLayout.error = "El comentario es obligatorio"
                    return@setOnClickListener
                } else {
                    inputCommentLayout.error = null
                }

                val selectedAuthor = inputCollector.text?.toString()?.trim()

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
