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
import coil.load
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.databinding.FragmentDetailAlbumBinding
import com.example.vinilosmobileapp.datasource.remote.AlbumServiceAdapter
import com.example.vinilosmobileapp.model.*
import com.example.vinilosmobileapp.ui.album.adapter.CommentInputAdapter
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

    private val guestNames = listOf(
        "Melómano Anónimo", "Oyente Misterioso", "Amante del Vinilo",
        "Coleccionista X", "Música para el alma"
    )

    private var collectorsAvailable: List<Collector>? = null
    private lateinit var selectedCollector: Collector

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
            getString(R.string.detail_album)

        albumId = arguments?.getInt("albumId") ?: -1

        if (albumId != -1) {
            fetchAlbum()
        } else {
            showError("ID de álbum no válido")
        }

        binding.buttonRetry.setOnClickListener {
            fetchAlbum()
        }

        binding.recyclerViewComments.apply {
            adapter = CommentInputAdapter(emptyList())
            commentAdapter = adapter as CommentInputAdapter

            layoutAnimation = android.view.animation.AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_fade_in
            )
        }

        binding.buttonAddComment.setOnClickListener {
            showAddCommentDialog()
        }

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

        val comments = album.comments.ifEmpty { emptyList() }

        if (comments.isEmpty()) {
            binding.recyclerViewComments.visibility = View.GONE
            binding.noCommentsText.visibility = View.VISIBLE
        } else {
            binding.recyclerViewComments.visibility = View.VISIBLE
            binding.noCommentsText.visibility = View.GONE
            commentAdapter.updateComments(comments)
            binding.recyclerViewComments.scheduleLayoutAnimation()
        }

        binding.trackList.text = if (album.tracks.isNullOrEmpty()) {
            "No hay canciones disponibles."
        } else {
            album.tracks.joinToString("\n") { track ->
                "${track.name} - ${track.duration ?: "?"}"
            }
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

        val guestName = "Guest: ${guestNames.random()}"

        val guestHint = dialogView.findViewById<TextView>(R.id.guest_hint)

        AlbumServiceAdapter.getCollectors().enqueue(object : Callback<List<Collector>> {
            override fun onResponse(
                call: Call<List<Collector>>,
                response: Response<List<Collector>>
            ) {
                if (response.isSuccessful) {
                    collectorsAvailable = response.body() ?: emptyList()

                    if (collectorsAvailable!!.isNotEmpty()) {
                        guestHint.visibility = View.GONE
                        val names = collectorsAvailable!!.map { it.name }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            names
                        )
                        authorDropdown.setAdapter(adapter)
                        authorDropdown.setText(names.random(), false)
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
                    val collectorId =
                        collectorsAvailable?.firstOrNull { it.name == selectedAuthorName }?.id
                    if (collectorId != null) {
                        addComment(commentText, collectorId)
                    } else {
                        createGuestCollector(selectedAuthorName) { newCollectorId ->
                            addComment(commentText, newCollectorId)
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)

        builder.create().show()
    }

    private fun addComment(description: String, collectorId: Int) {
        AlbumServiceAdapter.addCommentToAlbum(albumId, description, collectorId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Comentario agregado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Simula añadir el comentario a la lista local
                        val simulatedComment = Comment(
                            id = (0..10000).random(), // id temporal aleatorio
                            description = description,
                            collector = Collector(1 , name = "Tú"), // Puedes usar el nombre si quieres
                            rating = 5
                        )
                        commentAdapter.addComment(simulatedComment)

                        binding.recyclerViewComments.visibility = View.VISIBLE
                        binding.noCommentsText.visibility = View.GONE
                        binding.recyclerViewComments.scheduleLayoutAnimation()
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
                    val errorBody = response.errorBody()?.string()
                    Log.e("GUEST_CREATION", "Error: ${response.code()} - $errorBody")
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
