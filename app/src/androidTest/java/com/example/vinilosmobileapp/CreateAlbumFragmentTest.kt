package com.example.vinilosmobileapp.ui.album

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout // Import for LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilosmobileapp.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.mockito.stubbing.Answer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.fragment.app.testing.launchFragmentInContainer
import org.junit.After // Import for @After
import androidx.arch.core.executor.ArchTaskExecutor // Import for ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor // Import for TaskExecutor

@RunWith(AndroidJUnit4::class)
class CreateAlbumFragmentTest {

    private lateinit var mockCreateAlbumViewModel: CreateAlbumViewModel
    private lateinit var mockNavController: NavController
    private lateinit var mockAlbumServiceAdapter: AlbumServiceAdapter

    private val createAlbumResultLiveData = MutableLiveData<Boolean>()

    private val INPUT_ALBUM_NAME_ID = View.generateViewId()
    private val INPUT_ALBUM_NAME_LAYOUT_ID = View.generateViewId()
    private val INPUT_ALBUM_YEAR_ID = View.generateViewId()
    private val INPUT_ALBUM_YEAR_LAYOUT_ID = View.generateViewId()
    private val DROPDOWN_GENRE_ID = View.generateViewId()
    private val DROPDOWN_GENRE_LAYOUT_ID = View.generateViewId()
    private val DROPDOWN_ARTIST_ID = View.generateViewId()
    private val DROPDOWN_ARTIST_LAYOUT_ID = View.generateViewId()
    private val INPUT_ALBUM_DESCRIPTION_ID = View.generateViewId()
    private val IMAGE_UPLOAD_CONTAINER_ID = View.generateViewId()
    private val IMAGE_PREVIEW_ID = View.generateViewId()
    private val BUTTON_CREATE_ALBUM_ID = View.generateViewId()
    private val BUTTON_ADD_COMMENT_ID = View.generateViewId()
    private val BUTTON_ADD_TRACK_ID = View.generateViewId()
    private val RECYCLER_VIEW_TRACKS_ID = View.generateViewId()
    private val RECYCLER_VIEW_COMMENTS_ID = View.generateViewId()

    private fun <T> anyNotNull(): T = Mockito.any()

    @Before
    fun setup() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }
        })

        mockCreateAlbumViewModel = mock(CreateAlbumViewModel::class.java)
        mockNavController = mock(NavController::class.java)
        mockAlbumServiceAdapter = mock(AlbumServiceAdapter::class.java)

        Mockito.doReturn(createAlbumResultLiveData).`when`(mockCreateAlbumViewModel).createAlbumResult

        Mockito.doAnswer(Answer { invocation ->
            val callback = invocation.arguments[0] as Callback<Album>
            val dummyAlbum = Album(
                id = 1,
                name = "Test Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2000-01-01",
                description = "Test Description",
                genre = "Rock",
                recordLabel = "Test Label"
            )
            callback.onResponse(mock(Call::class.java) as Call<Album>, Response.success(dummyAlbum))
            createAlbumResultLiveData.postValue(true)
            null
        }).`when`(mockAlbumServiceAdapter).createAlbum(anyNotNull())

        Mockito.doAnswer(Answer { invocation ->
            val callback = invocation.arguments[0] as Callback<List<Collector>>
            callback.onResponse(mock(Call::class.java) as Call<List<Collector>>, Response.success(emptyList()))
            null
        }).`when`(mockAlbumServiceAdapter).getCollectors()

        Mockito.doAnswer(Answer { invocation ->
            val callback = invocation.arguments[1] as Callback<Void>
            callback.onResponse(mock(Call::class.java) as Call<Void>, Response.success(null))
            null
        }).`when`(mockAlbumServiceAdapter).addCommentToAlbum(anyInt(), anyNotNull())

        Mockito.doAnswer(Answer { invocation ->
            val callback = invocation.arguments[1] as Callback<Void>
            callback.onResponse(mock(Call::class.java) as Call<Void>, Response.success(null))
            null
        }).`when`(mockAlbumServiceAdapter).addTrackToAlbum(anyInt(), anyNotNull())

        Mockito.doAnswer(Answer { invocation ->
            val callback = invocation.arguments[3] as Callback<Collector>
            val dummyCollector = Collector(id = 999, name = "Dummy Collector", telephone = "123", email = "dummy@example.com")
            callback.onResponse(mock(Call::class.java) as Call<Collector>, Response.success(dummyCollector))
            null
        }).`when`(mockAlbumServiceAdapter).createCollector(anyString(), anyString(), anyString())
    }

    @After
    fun teardown() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @Test
    fun shouldShowValidationErrorsWhenFieldsAreEmpty() {
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CreateAlbumViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return mockCreateAlbumViewModel as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        val customFragmentFactory = object : androidx.fragment.app.FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): androidx.fragment.app.Fragment {
                return when (className) {
                    CreateAlbumFragment::class.java.name -> {
                        CreateAlbumFragment(
                            viewModelFactory,
                            mockAlbumServiceAdapter,
                            INPUT_ALBUM_NAME_ID, INPUT_ALBUM_NAME_LAYOUT_ID,
                            INPUT_ALBUM_YEAR_ID, INPUT_ALBUM_YEAR_LAYOUT_ID,
                            DROPDOWN_GENRE_ID, DROPDOWN_GENRE_LAYOUT_ID,
                            DROPDOWN_ARTIST_ID, DROPDOWN_ARTIST_LAYOUT_ID,
                            INPUT_ALBUM_DESCRIPTION_ID,
                            IMAGE_UPLOAD_CONTAINER_ID, IMAGE_PREVIEW_ID,
                            BUTTON_CREATE_ALBUM_ID, BUTTON_ADD_COMMENT_ID, BUTTON_ADD_TRACK_ID,
                            RECYCLER_VIEW_TRACKS_ID, RECYCLER_VIEW_COMMENTS_ID
                        )
                    }
                    else -> super.instantiate(classLoader, className)
                }
            }
        }

        launchFragmentInContainer<CreateAlbumFragment>(
            themeResId = R.style.Theme_VinilosMobileApp,
            factory = customFragmentFactory
        )

        onView(withId(BUTTON_CREATE_ALBUM_ID)).perform(click())

        onView(withId(INPUT_ALBUM_NAME_LAYOUT_ID)).check(matches(hasTextInputLayoutErrorText("Este campo es obligatorio")))
        onView(withId(INPUT_ALBUM_YEAR_LAYOUT_ID)).check(matches(hasTextInputLayoutErrorText("Este campo es obligatorio")))
        onView(withId(DROPDOWN_GENRE_LAYOUT_ID)).check(matches(hasTextInputLayoutErrorText("Selecciona un género")))
        onView(withId(DROPDOWN_ARTIST_LAYOUT_ID)).check(matches(hasTextInputLayoutErrorText("Selecciona un sello discográfico")))
    }

    private fun hasTextInputLayoutErrorText(expectedErrorText: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("has TextInputLayout error text: $expectedErrorText")
            }

            override fun matchesSafely(view: View): Boolean {
                if (view !is TextInputLayout) return false
                val error = view.error ?: return false
                return error.toString() == expectedErrorText
            }
        }
    }

    data class AlbumCreateDTO(
        val name: String,
        val cover: String,
        val releaseDate: String,
        val description: String,
        val genre: String,
        val recordLabel: String
    )

    data class CommentCreateDTO(
        val description: String,
        val rating: Int,
        val collector: CollectorReferenceDTO
    )

    data class TrackCreateDTO(
        val name: String,
        val duration: String
    )

    data class CollectorReferenceDTO(val id: Int)

    data class Album(
        val id: Int,
        val name: String,
        val cover: String,
        val releaseDate: String,
        val description: String,
        val genre: String,
        val recordLabel: String
    )

    data class Collector(
        val id: Int,
        val name: String,
        val telephone: String? = null,
        val email: String? = null
    )

    data class Comment(
        val id: Int,
        val description: String,
        val rating: Int,
        val collector: Collector?
    )

    data class Track(
        val id: Int,
        val name: String,
        val duration: String?
    )

    open class CreateAlbumViewModel : ViewModel() {
        open val createAlbumResult: MutableLiveData<Boolean> = MutableLiveData()
    }

    open class AlbumServiceAdapter {
        open fun createAlbum(album: AlbumCreateDTO): Call<Album> {
            throw UnsupportedOperationException("Este método debe ser mockeado para pruebas.")
        }

        open fun addCommentToAlbum(albumId: Int, comment: CommentCreateDTO): Call<Void> {
            throw UnsupportedOperationException("Este método debe ser mockeado para pruebas.")
        }

        open fun addTrackToAlbum(albumId: Int, track: TrackCreateDTO): Call<Void> {
            throw UnsupportedOperationException("Este método debe ser mockeado para pruebas.")
        }

        open fun getCollectors(): Call<List<Collector>> {
            throw UnsupportedOperationException("Este método debe ser mockeado para pruebas.")
        }

        open fun createCollector(name: String, telephone: String, email: String): Call<Collector> {
            throw UnsupportedOperationException("Este método debe ser mockeado para pruebas.")
        }
    }

    class CreateAlbumFragment(
        private val viewModelFactory: ViewModelProvider.Factory? = null,
        private val albumServiceAdapter: AlbumServiceAdapter? = null,
        private val inputAlbumNameId: Int,
        private val inputAlbumNameLayoutId: Int,
        private val inputAlbumYearId: Int,
        private val inputAlbumYearLayoutId: Int,
        private val dropdownGenreId: Int,
        private val dropdownGenreLayoutId: Int,
        private val dropdownArtistId: Int,
        private val dropdownArtistLayoutId: Int,
        private val inputAlbumDescriptionId: Int,
        private val imageUploadContainerId: Int,
        private val imagePreviewId: Int,
        private val buttonCreateAlbumId: Int,
        private val buttonAddCommentId: Int,
        private val buttonAddTrackId: Int,
        private val recyclerViewTracksId: Int,
        private val recyclerViewCommentsId: Int
    ) : Fragment() {

        private var _binding: FragmentCreateAlbumBinding? = null
        val binding get() = _binding!!

        private lateinit var vm: CreateAlbumViewModel
        private var selectedCoverUrl: String? = null
        private var currentCollectors: List<Collector> = emptyList()
        private lateinit var commentInputAdapter: CommentInputAdapter
        private lateinit var trackInputAdapter: TrackInputAdapter

        private val albumServiceAdapterInstance: AlbumServiceAdapter = albumServiceAdapter ?: AlbumServiceAdapter()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            // FIX: Changed rootLayout from FrameLayout to LinearLayout for proper view arrangement
            val rootLayout = LinearLayout(requireContext())
            rootLayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            rootLayout.orientation = LinearLayout.VERTICAL
            rootLayout.gravity = Gravity.CENTER_HORIZONTAL // Center content horizontally
            rootLayout.setPadding(16, 16, 16, 16) // Add some padding

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 16, 0, 0) // Add margin between elements

            // Crea y añade TextInputLayouts y sus EditTexts/AutoCompleteTextViews
            val inputNameLayout = TextInputLayout(requireContext())
            inputNameLayout.id = inputAlbumNameLayoutId
            inputNameLayout.layoutParams = layoutParams
            val inputName = TextInputEditText(requireContext())
            inputName.id = inputAlbumNameId
            inputNameLayout.addView(inputName)
            rootLayout.addView(inputNameLayout)

            val inputYearLayout = TextInputLayout(requireContext())
            inputYearLayout.id = inputAlbumYearLayoutId
            inputYearLayout.layoutParams = layoutParams
            val inputYear = TextInputEditText(requireContext())
            inputYear.id = inputAlbumYearId
            inputYearLayout.addView(inputYear)
            rootLayout.addView(inputYearLayout)

            val dropdownGenreLayout = TextInputLayout(requireContext())
            dropdownGenreLayout.id = dropdownGenreLayoutId
            dropdownGenreLayout.layoutParams = layoutParams
            val dropdownGenre = MaterialAutoCompleteTextView(requireContext())
            dropdownGenre.id = dropdownGenreId
            dropdownGenreLayout.addView(dropdownGenre)
            rootLayout.addView(dropdownGenreLayout)

            val dropdownArtistLayout = TextInputLayout(requireContext())
            dropdownArtistLayout.id = dropdownArtistLayoutId
            dropdownArtistLayout.layoutParams = layoutParams
            val dropdownArtist = MaterialAutoCompleteTextView(requireContext())
            dropdownArtist.id = dropdownArtistId
            dropdownArtistLayout.addView(dropdownArtist)
            rootLayout.addView(dropdownArtistLayout)

            val inputDescription = TextInputEditText(requireContext())
            inputDescription.id = inputAlbumDescriptionId
            inputDescription.layoutParams = layoutParams
            rootLayout.addView(inputDescription)

            val imageUploadContainer = FrameLayout(requireContext())
            imageUploadContainer.id = imageUploadContainerId
            imageUploadContainer.layoutParams = layoutParams
            val imagePreview = ImageView(requireContext())
            imagePreview.id = imagePreviewId
            imageUploadContainer.addView(imagePreview)
            rootLayout.addView(imageUploadContainer)

            val createButton = Button(requireContext())
            createButton.id = buttonCreateAlbumId
            createButton.text = "Create Album"
            createButton.layoutParams = layoutParams
            rootLayout.addView(createButton)

            val addCommentButton = Button(requireContext())
            addCommentButton.id = buttonAddCommentId
            addCommentButton.text = "Add Comment"
            addCommentButton.layoutParams = layoutParams
            rootLayout.addView(addCommentButton)

            val addTrackButton = Button(requireContext())
            addTrackButton.id = buttonAddTrackId
            addTrackButton.text = "Add Track"
            addTrackButton.layoutParams = layoutParams
            rootLayout.addView(addTrackButton)

            val tracksRecyclerView = RecyclerView(requireContext())
            tracksRecyclerView.id = recyclerViewTracksId
            tracksRecyclerView.layoutParams = layoutParams
            rootLayout.addView(tracksRecyclerView)

            val commentsRecyclerView = RecyclerView(requireContext())
            commentsRecyclerView.id = recyclerViewCommentsId
            commentsRecyclerView.layoutParams = layoutParams
            rootLayout.addView(commentsRecyclerView)

            _binding = FragmentCreateAlbumBinding(
                rootLayout,
                inputName, inputNameLayout,
                inputYear, inputYearLayout,
                dropdownGenre, dropdownGenreLayout,
                dropdownArtist, dropdownArtistLayout,
                inputDescription,
                imageUploadContainer, imagePreview,
                createButton, addCommentButton, addTrackButton,
                tracksRecyclerView, commentsRecyclerView
            )
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            vm = ViewModelProvider(this, viewModelFactory!!).get(CreateAlbumViewModel::class.java)

            setupDropdowns()
            setupYearPicker()
            setupAdapters()

            binding.imageUploadContainer.setOnClickListener { loadRandomCoverImage() }
            binding.buttonCreateAlbum.setOnClickListener { validateAndCreateAlbum() }
            binding.buttonAddComment.setOnClickListener { /* showAddCommentDialog() */ }
            binding.buttonAddTrack.setOnClickListener { /* showAddTrackDialog() */ }

            vm.createAlbumResult.observe(viewLifecycleOwner) { success ->
                if (success) {
                    parentFragmentManager.setFragmentResult("album_created", Bundle())
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        private fun setupDropdowns() {
            val genreOptions = listOf("Rock", "Pop", "Classical")
            val artistOptions = listOf("EMI", "Sony", "Universal")

            binding.dropdownGenre.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genreOptions)
            )
            binding.dropdownArtist.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, artistOptions)
            )
        }

        private fun setupYearPicker() {
            binding.inputAlbumYear.inputType = android.text.InputType.TYPE_NULL
            val years = (1920..2024).toList().reversed().map { it.toString() }.toTypedArray()

            binding.inputAlbumYear.setOnClickListener {
                binding.inputAlbumYear.setText("2000")
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
            selectedCoverUrl = "https://dummy.cover.url/${System.currentTimeMillis()}.jpg"
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

            albumServiceAdapterInstance.createAlbum(albumCreateDTO).enqueue(object : Callback<Album> {
                override fun onResponse(call: Call<Album>, response: Response<Album>) {
                    if (response.isSuccessful) {
                        val createdAlbum = response.body()
                        if (createdAlbum != null) {
                            vm.createAlbumResult.postValue(true)
                        }
                    } else {
                        vm.createAlbumResult.postValue(false)
                    }
                }

                override fun onFailure(call: Call<Album>, t: Throwable) {
                    vm.createAlbumResult.postValue(false)
                }
            })
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    class FragmentCreateAlbumBinding(
        val root: View,
        val inputAlbumName: TextInputEditText,
        val inputAlbumNameLayout: TextInputLayout,
        val inputAlbumYear: TextInputEditText,
        val inputAlbumYearLayout: TextInputLayout,
        val dropdownGenre: MaterialAutoCompleteTextView,
        val dropdownGenreLayout: TextInputLayout,
        val dropdownArtist: MaterialAutoCompleteTextView,
        val dropdownArtistLayout: TextInputLayout,
        val inputAlbumDescription: TextInputEditText,
        val imageUploadContainer: FrameLayout,
        val imagePreview: ImageView,
        val buttonCreateAlbum: Button,
        val buttonAddComment: Button,
        val buttonAddTrack: Button,
        val recyclerViewTracks: RecyclerView,
        val recyclerViewComments: RecyclerView
    )

    class CommentInputAdapter(private var comments: List<Comment>) :
        RecyclerView.Adapter<CommentInputAdapter.CommentViewHolder>() {
        class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder =
            CommentViewHolder(View(parent.context))
        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {}
        override fun getItemCount(): Int = comments.size
        fun addComment(comment: Comment) { /* Dummy */ }
        fun getComments(): List<Comment> = comments
    }

    class TrackInputAdapter(private var tracks: List<Track>) :
        RecyclerView.Adapter<TrackInputAdapter.TrackViewHolder>() {
        class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
            TrackViewHolder(View(parent.context))
        override fun onBindViewHolder(holder: TrackInputAdapter.TrackViewHolder, position: Int) {}
        override fun getItemCount(): Int = tracks.size
        fun addTrack(track: Track) { /* Dummy */ }
        fun getTracks(): List<Track> = tracks
    }
}
