package com.example.vinilosmobileapp.ui.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentDetailAlbumBinding
import com.example.vinilosmobileapp.datasource.remote.AlbumServiceAdapter
import com.example.vinilosmobileapp.model.*
import com.example.vinilosmobileapp.model.dto.CollectorReferenceDTO
import com.example.vinilosmobileapp.model.dto.CommentCreateDTO
import com.example.vinilosmobileapp.ui.album.adapter.CommentInputAdapter
import com.example.vinilosmobileapp.ui.album.adapter.TrackInputAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAlbumFragment : Fragment() {

    private var _binding: FragmentDetailAlbumBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailAlbumViewModel by viewModels()

    private var albumId: Int = -1
    private lateinit var commentAdapter: CommentInputAdapter
    private lateinit var trackAdapter: TrackInputAdapter

    private val guestNames = listOf(
        "Melómano Anónimo", "Oyente Misterioso", "Amante del Vinilo",
        "Coleccionista X", "Música para el alma"
    )

    private var collectorsAvailable: List<Collector> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.album_detail)

        // Initialize the track adapter
        trackAdapter = TrackInputAdapter(emptyList())
        binding.recyclerViewTracks.apply {
            adapter = trackAdapter
            layoutManager = GridLayoutManager(context, 3)
        }
        // Initialize the comment adapter
        commentAdapter = CommentInputAdapter(emptyList())
        binding.recyclerViewComments.apply {
            adapter = commentAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        albumId = arguments?.getInt("albumId") ?: -1

        if (albumId != -1) {
            fetchAlbum()
        } else {
            showError("ID de álbum no válido")
        }

        binding.buttonRetry.setOnClickListener { fetchAlbum() }

        binding.recyclerViewComments.apply {
            adapter = CommentInputAdapter(emptyList())
            commentAdapter = adapter as CommentInputAdapter
            layoutAnimation = android.view.animation.AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_fade_in
            )
        }

        binding.buttonAddComment.setOnClickListener { showAddCommentDialog() }

        setupObservers()
    }

    private fun fetchAlbum() {
        showLoading()
        viewModel.fetchAlbumDetail(albumId)
    }

    private fun setupObservers() {
        viewModel.albumDetail.observe(viewLifecycleOwner) { album ->
            if (album != null) {
                showAlbum(album)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showError(error)
            }
        }
    }

    private fun showAlbum(album: AlbumDetail) {
        binding.contentLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE

        binding.albumTitle.text = album.name
        binding.albumGenreLabel.text = "${album.genre} - ${album.recordLabel}"
        binding.albumReleaseYear.text = album.releaseDate.take(4)
        binding.albumDescription.text = album.description

        binding.albumImage.load(album.cover) {
            crossfade(true)
            error(R.drawable.ic_failed_to_load_image)
        }

        // Populate tracks
        if (!album.tracks.isNullOrEmpty()) {
            binding.recyclerViewTracks.visibility = View.VISIBLE
            trackAdapter.updateTracks(album.tracks)
        } else {
            binding.recyclerViewTracks.visibility = View.GONE
        }

        // Populate comments
        if (album.comments.isNotEmpty()) {
            binding.recyclerViewComments.visibility = View.VISIBLE
            binding.noCommentsText.visibility = View.GONE
            commentAdapter.updateComments(album.comments)
        } else {
            binding.recyclerViewComments.visibility = View.GONE
            binding.noCommentsText.visibility = View.VISIBLE
        }
    }

    private fun showAddCommentDialog() {
        val context = requireContext()
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_comment, null)
        builder.setView(dialogView)

        val commentInput =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputComment)
        val authorDropdown = dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.inputAuthor)
        val guestHint = dialogView.findViewById<TextView>(R.id.guest_hint)

        val guestName = "Guest: ${guestNames.random()}"

        AlbumServiceAdapter.getCollectors().enqueue(object : Callback<List<Collector>> {
            override fun onResponse(
                call: Call<List<Collector>>,
                response: Response<List<Collector>>
            ) {
                if (response.isSuccessful) {
                    collectorsAvailable = response.body() ?: emptyList()

                    if (collectorsAvailable.isNotEmpty()) {
                        guestHint.visibility = View.GONE
                        val names = collectorsAvailable.map { it.name }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            names
                        )
                        authorDropdown.setAdapter(adapter)
                        authorDropdown.setText(names.firstOrNull(), false)
                    } else {
                        guestHint.visibility = View.VISIBLE
                        authorDropdown.setText(guestName, false)
                    }
                } else {
                    guestHint.visibility = View.VISIBLE
                    authorDropdown.setText(guestName, false)
                }
            }

            override fun onFailure(call: Call<List<Collector>>, t: Throwable) {
                guestHint.visibility = View.VISIBLE
                authorDropdown.setText(guestName, false)
            }
        })

        builder.setTitle("Nuevo comentario")
            .setPositiveButton("Agregar") { _, _ ->
                val commentText = commentInput.text.toString().trim()
                val selectedAuthorName = authorDropdown.text.toString().trim()

                if (commentText.isNotEmpty()) {
                    val collector = collectorsAvailable.find { it.name == selectedAuthorName }

                    if (collector != null) {
                        addComment(commentText, collector.id)
                    } else {
                        createGuestCollector(selectedAuthorName) { guestId ->
                            addComment(commentText, guestId)
                        }
                    }
                } else {
                    Toast.makeText(context, "Comentario obligatorio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)

        builder.create().show()
    }

    private fun addComment(description: String, collectorId: Int) {
        val commentCreateDTO = CommentCreateDTO(
            description = description,
            rating = 5,
            collector = CollectorReferenceDTO(id = collectorId)
        )

        AlbumServiceAdapter.addCommentToAlbum(albumId, commentCreateDTO)

            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        fetchAlbum() // 👈 Refrescamos comentarios reales desde servidor
                        Toast.makeText(
                            requireContext(),
                            "Comentario agregado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al agregar comentario",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Error de red al agregar comentario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun createGuestCollector(name: String, onCollectorCreated: (Int) -> Unit) {
        AlbumServiceAdapter.createCollector(
            name = name,
            telephone = "1234567890",
            email = "${name.replace(" ", "").lowercase()}@guest.com"
        ).enqueue(object : Callback<Collector> {
            override fun onResponse(call: Call<Collector>, response: Response<Collector>) {
                if (response.isSuccessful) {
                    response.body()?.let { onCollectorCreated(it.id) }
                } else {
                    Log.e(
                        "GUEST_CREATION",
                        "Error: ${response.code()} - ${response.errorBody()?.string()}"
                    )
                    Toast.makeText(requireContext(), "Error creando guest", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Collector>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red creando guest", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun showError(message: String) {
        binding.contentLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        binding.textError.text = message
    }

    private fun showLoading() {
        binding.contentLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
