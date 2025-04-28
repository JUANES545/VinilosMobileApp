package com.example.vinilosmobileapp.ui.album

import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentCreateAlbumBinding
import com.example.vinilosmobileapp.datasource.remote.AlbumServiceAdapter
import com.example.vinilosmobileapp.model.dto.AlbumCreateDTO
import com.example.vinilosmobileapp.model.Collector
import com.example.vinilosmobileapp.model.Comment
import com.example.vinilosmobileapp.model.Track
import com.example.vinilosmobileapp.model.dto.CollectorReferenceDTO
import com.example.vinilosmobileapp.model.dto.CommentCreateDTO
import com.example.vinilosmobileapp.model.dto.TrackCreateDTO
import com.example.vinilosmobileapp.ui.album.adapter.CommentInputAdapter
import com.example.vinilosmobileapp.ui.album.adapter.TrackInputAdapter
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
        commentInputAdapter = CommentInputAdapter(emptyList())
        binding.recyclerViewComments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentInputAdapter
        }

        trackInputAdapter = TrackInputAdapter(emptyList())
        binding.recyclerViewTracks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackInputAdapter
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
                placeholder(R.drawable.ic_search)
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
        val description = binding.inputAlbumDescription.text?.toString()?.trim()
            ?: "Álbum creado desde la app móvil."
        val releaseDateFormatted = "$year-01-01"

        val commentsList = commentInputAdapter.getComments().map { comment ->
            CommentCreateDTO(
                description = comment.description ?: "",
                rating = comment.rating ?: 5,
                collector = CollectorReferenceDTO(comment.collector?.id ?: 1)
            )
        }

        val tracksList = trackInputAdapter.getTracks().map { track ->
            TrackCreateDTO(
                name = track.name,
                duration = track.duration ?: "3:30 min"
            )
        }

        val albumCreateDTO = AlbumCreateDTO(
            name = name,
            cover = coverUrl,
            releaseDate = releaseDateFormatted,
            description = description,
            genre = genre,
            recordLabel = artist,
            tracks = if (tracksList.isEmpty()) emptyList() else tracksList,
            comments = if (commentsList.isEmpty()) emptyList() else commentsList
        )

        viewModel.createAlbum(albumCreateDTO)
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

        val inputAuthorLayout =
            dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.inputAuthorLayout)
        val inputAuthor =
            dialogView.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(
                R.id.inputAuthor
            )

        val guestHint = dialogView.findViewById<TextView>(R.id.guest_hint)

        // 🔵 Configurar Dropdown con los collectors
        val collectorsNames = currentCollectors.map { it.name }
        if (collectorsNames.isNotEmpty()) {
            val adapter =
                ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, collectorsNames)
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
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
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

            Toast.makeText(context, "Comentario agregado", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun showAddTrackDialog() {
        val randomSongs = listOf(
            "Bohemian Rhapsody",
            "Hotel California",
            "Stairway to Heaven",
            "Imagine",
            "Smells Like Teen Spirit",
            "Sweet Child O' Mine",
            "Billie Jean",
            "Hey Jude",
            "Wonderwall",
            "Shape of You"
        )
        val randomSong = randomSongs.random()
        val randomDuration =
            "${(2..7).random()}:${(0..59).random().toString().padStart(2, '0')} min"

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
        inputTrackName.setText(randomSong)
        inputTrackDuration.setText(randomDuration)

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
