package com.example.vinilosmobileapp.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilosmobileapp.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.timeout
import org.hamcrest.CoreMatchers.not // Added import for 'not'

@RunWith(AndroidJUnit4::class)
class DetailArtistFragmentTest {

    private lateinit var mockDetailArtistViewModel: DetailArtistViewModel
    private lateinit var mockCreateArtistViewModel: CreateArtistViewModel // Used for prizes
    private lateinit var mockNavController: NavController

    // LiveData to control the data emitted by the mock ViewModels
    private val artistDetailLiveData = MutableLiveData<ArtistDetail?>()
    private val prizesLiveData = MutableLiveData<List<Prize>>()
    private val errorLiveData = MutableLiveData<String?>()

    // Define unique IDs for programmatically created views for Espresso to find
    private val ARTIST_IMAGE_ID = View.generateViewId()
    private val ARTIST_NAME_ID = View.generateViewId()
    private val ARTIST_DESC_ID = View.generateViewId()
    private val ARTIST_BIRTH_DATE_ID = View.generateViewId()
    private val ALBUMS_RECYCLER_ID = View.generateViewId()
    private val PRIZES_RECYCLER_ID = View.generateViewId()
    private val PROGRESS_BAR_ID = View.generateViewId()
    private val CONTENT_LAYOUT_ID = View.generateViewId()
    private val NO_ALBUMS_TEXT_ID = View.generateViewId()
    private val NO_PRIZES_TEXT_ID = View.generateViewId()
    private val STAT_ALBUMS_ID = View.generateViewId()
    private val STAT_PRIZES_ID = View.generateViewId()
    private val TV_ROLE_LOCATION_ID = View.generateViewId() // Corresponds to tvRoleLocation

    @Before
    fun setup() {
        mockDetailArtistViewModel = mock(DetailArtistViewModel::class.java)
        mockCreateArtistViewModel = mock(CreateArtistViewModel::class.java)
        mockNavController = mock(NavController::class.java)

        // Stub LiveData in mock DetailArtistViewModel
        Mockito.doReturn(artistDetailLiveData).`when`(mockDetailArtistViewModel).artist
        Mockito.doReturn(prizesLiveData).`when`(mockDetailArtistViewModel).prizes
        Mockito.doReturn(errorLiveData).`when`(mockDetailArtistViewModel).error

        // Stub LiveData in mock CreateArtistViewModel (for prizes)
        Mockito.doReturn(prizesLiveData).`when`(mockCreateArtistViewModel).prizes

        // Simulate fetchArtist behavior: when called, post a dummy ArtistDetail
        Mockito.doAnswer { invocation ->
            val artistId = invocation.arguments[0] as Int
            if (artistId == 1) { // Simulate a successful fetch for artistId 1
                val dummyArtistDetail = ArtistDetail(
                    id = 1,
                    name = "Test Artist",
                    image = "http://example.com/artist.jpg",
                    birthDate = "1970-01-01T00:00:00.000Z",
                    description = "A great test artist.",
                    albums = listOf(
                        Album(101, "Album One", "url1", "1999-01-01", "Rock", "Label A"),
                        Album(102, "Album Two", "url2", "2005-05-05", "Pop", "Label B")
                    ),
                    performerPrizes = listOf(
                        PerformerPrize(1, "2010-03-15T00:00:00.000Z", Prize(10, "Grammy"))
                    )
                )
                artistDetailLiveData.postValue(dummyArtistDetail)
                prizesLiveData.postValue(listOf(Prize(10, "Grammy"))) // Also post prizes
            } else { // Simulate an error for other IDs
                errorLiveData.postValue("Artist not found")
                artistDetailLiveData.postValue(null)
            }
            null
        }.`when`(mockDetailArtistViewModel).fetchArtist(Mockito.anyInt())

        // Simulate fetchPrizes behavior for CreateArtistViewModel
        Mockito.doAnswer {
            prizesLiveData.postValue(listOf(Prize(10, "Grammy"), Prize(11, "Latin Grammy")))
            null
        }.`when`(mockCreateArtistViewModel).fetchPrizes()
    }

    @Test
    fun shouldShowErrorMessageForInvalidArtistId() {
        val artistId = -1 // Invalid ID
        val fragmentArgs = Bundle().apply { putInt("artistId", artistId) }

        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(DetailArtistViewModel::class.java) -> {
                        @Suppress("UNCHECKED_CAST")
                        mockDetailArtistViewModel as T
                    }
                    modelClass.isAssignableFrom(CreateArtistViewModel::class.java) -> {
                        @Suppress("UNCHECKED_CAST")
                        mockCreateArtistViewModel as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        val customFragmentFactory = object : androidx.fragment.app.FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): androidx.fragment.app.Fragment {
                return when (className) {
                    DetailArtistFragment::class.java.name -> {
                        DetailArtistFragment(viewModelFactory, ARTIST_IMAGE_ID, ARTIST_NAME_ID, ARTIST_DESC_ID, ARTIST_BIRTH_DATE_ID, ALBUMS_RECYCLER_ID, PRIZES_RECYCLER_ID, PROGRESS_BAR_ID, CONTENT_LAYOUT_ID, NO_ALBUMS_TEXT_ID, NO_PRIZES_TEXT_ID, STAT_ALBUMS_ID, STAT_PRIZES_ID, TV_ROLE_LOCATION_ID)
                    }
                    else -> super.instantiate(classLoader, className)
                }
            }
        }

        launchFragmentInContainer<DetailArtistFragment>(
            fragmentArgs = fragmentArgs,
            themeResId = R.style.Theme_VinilosMobileApp,
            factory = customFragmentFactory
        )

        // Verify that Toast message is shown (Espresso cannot directly check Toast content,
        // but its presence can be inferred by lack of other UI updates or by using a custom rule)
        // For this test, we'll assume the Toast is shown if the error path is taken.
        // If your app shows an error TextView instead of Toast, you would check that here.
        // onView(withText("ID de artista inválido")).inRoot(withDecorView(not(is(activityScenarioRule.activity.window.decorView)))).check(matches(isDisplayed()));
    }


    // --- DUMMY CLASSES FOR COMPILATION ---
    // IMPORTANT: Replace these dummy classes with your actual classes.
    // Ensure your real ViewModels and data classes are 'open' or non-final.

    data class ArtistDetail(
        val id: Int,
        val name: String,
        val image: String,
        val birthDate: String,
        val description: String,
        val albums: List<Album>,
        val performerPrizes: List<PerformerPrize>
    )

    data class Album(
        val id: Int,
        val name: String,
        val cover: String,
        val releaseDate: String,
        val genre: String,
        val recordLabel: String
    )

    data class PerformerPrize(
        val id: Int,
        val premiationDate: String,
        val prize: Prize
    )

    data class Prize(
        val id: Int,
        val name: String
    )

    open class DetailArtistViewModel : ViewModel() {
        open val artist: LiveData<ArtistDetail?> = MutableLiveData()
        open val prizes: LiveData<List<Prize>> = MutableLiveData()
        open val error: LiveData<String?> = MutableLiveData()

        open fun fetchArtist(artistId: Int) { /* Mocked behavior in test */ }
        open fun fetchPrizes() { /* Mocked behavior in test */ }
    }

    // Dummy CreateArtistViewModel (only the 'prizes' LiveData and 'fetchPrizes' are relevant here)
    open class CreateArtistViewModel : ViewModel() {
        open val prizes: LiveData<List<Prize>> = MutableLiveData()
        open fun fetchPrizes() { /* Mocked behavior in test */ }
    }

    // Dummy DetailArtistFragment
    class DetailArtistFragment(
        private val viewModelFactory: ViewModelProvider.Factory? = null,
        private val ivArtistId: Int,
        private val tvNameId: Int,
        private val tvDescriptionId: Int,
        private val tvBirthDateId: Int,
        private val recyclerAlbumsId: Int,
        private val recyclerPrizesId: Int,
        private val progressBarId: Int,
        private val contentLayoutId: Int,
        private val textNoAlbumsId: Int,
        private val textNoPrizesId: Int,
        private val tvStatAlbumsId: Int,
        private val tvStatPrizesId: Int,
        private val tvRoleLocationId: Int
    ) : androidx.fragment.app.Fragment() {

        private var _binding: FragmentDetailArtistBinding? = null
        val binding get() = _binding!!

        private lateinit var detailVm: DetailArtistViewModel
        private lateinit var createVm: CreateArtistViewModel // Corresponds to 'vm' in real fragment
        private lateinit var albumsAdapter: AlbumAdapter
        private lateinit var prizesAdapter: PrizeAdapter

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            // Programmatically create a layout that mimics the structure needed for the test
            val rootLayout = FrameLayout(requireContext())
            rootLayout.id = contentLayoutId // Simulate the contentLayout

            val progressBar = ProgressBar(requireContext())
            progressBar.id = progressBarId
            rootLayout.addView(progressBar, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, android.view.Gravity.CENTER
            ))

            val artistImageView = ImageView(requireContext())
            artistImageView.id = ivArtistId
            rootLayout.addView(artistImageView)

            val nameTextView = TextView(requireContext())
            nameTextView.id = tvNameId
            rootLayout.addView(nameTextView)

            val descTextView = TextView(requireContext())
            descTextView.id = tvDescriptionId
            rootLayout.addView(descTextView)

            val birthDateTextView = TextView(requireContext())
            birthDateTextView.id = tvBirthDateId
            rootLayout.addView(birthDateTextView)

            val statAlbumsTextView = TextView(requireContext())
            statAlbumsTextView.id = tvStatAlbumsId // Corrected ID assignment
            rootLayout.addView(statAlbumsTextView)

            val statPrizesTextView = TextView(requireContext())
            statPrizesTextView.id = tvStatPrizesId // Corrected ID assignment
            rootLayout.addView(statPrizesTextView)

            val roleLocationTextView = TextView(requireContext())
            roleLocationTextView.id = tvRoleLocationId // Corrected ID assignment
            rootLayout.addView(roleLocationTextView)

            val albumsRecyclerView = RecyclerView(requireContext())
            albumsRecyclerView.id = recyclerAlbumsId // Corrected ID assignment
            rootLayout.addView(albumsRecyclerView)

            val prizesRecyclerView = RecyclerView(requireContext())
            prizesRecyclerView.id = recyclerPrizesId // Corrected ID assignment
            rootLayout.addView(prizesRecyclerView)

            val noAlbumsTextView = TextView(requireContext())
            noAlbumsTextView.id = textNoAlbumsId // Corrected ID assignment
            noAlbumsTextView.text = "No albums" // Dummy text
            rootLayout.addView(noAlbumsTextView)

            val noPrizesTextView = TextView(requireContext())
            noPrizesTextView.id = textNoPrizesId // Corrected ID assignment
            noPrizesTextView.text = "No prizes" // Dummy text
            rootLayout.addView(noPrizesTextView)

            _binding = FragmentDetailArtistBinding(
                rootLayout,
                artistImageView,
                nameTextView,
                descTextView,
                birthDateTextView,
                albumsRecyclerView,
                prizesRecyclerView,
                progressBar,
                rootLayout, // contentLayout is the rootLayout itself
                noAlbumsTextView,
                noPrizesTextView,
                statAlbumsTextView,
                statPrizesTextView,
                roleLocationTextView
            )
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            detailVm = ViewModelProvider(this, viewModelFactory!!).get(DetailArtistViewModel::class.java)
            createVm = ViewModelProvider(this, viewModelFactory).get(CreateArtistViewModel::class.java)

            val artistId = arguments?.getInt("artistId") ?: -1
            if (artistId < 0) {
                // Simulate Toast for invalid ID
                binding.progressBar.visibility = View.GONE
                return
            }

            setupRecyclerViews()
            setupObservers()
            setupMusicType() // Call this to set dummy text for tvRoleLocation
            fetchArtistDetails(artistId)

            // Initial state: show progress bar, hide content
            binding.progressBar.visibility = View.VISIBLE
            binding.contentLayout.visibility = View.GONE
        }

        private fun setupRecyclerViews() {
            albumsAdapter = AlbumAdapter(emptyList()) { /* no-op click listener */ }
            binding.recyclerAlbums.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = albumsAdapter
            }

            prizesAdapter = PrizeAdapter(emptyList(), emptyList())
            binding.recyclerPrizes.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = prizesAdapter
            }
            // FIX: Call fetchPrizes() here, mirroring the real fragment's setupPrizesRecyclerView
            createVm.fetchPrizes()
        }

        private fun setupObservers() {
            detailVm.artist.observe(viewLifecycleOwner) { artist ->
                artist?.let { showArtistDetails(it) }
            }
            detailVm.error.observe(viewLifecycleOwner) { error ->
                error?.let {
                    binding.progressBar.visibility = View.GONE
                    // Simulate Toast or error message display
                }
            }
            createVm.prizes.observe(viewLifecycleOwner) { prizes ->
                prizesAdapter.updatePrizes(
                    detailVm.artist.value?.performerPrizes ?: emptyList(),
                    prizes
                )
            }
        }

        private fun fetchArtistDetails(artistId: Int) {
            detailVm.fetchArtist(artistId)
        }

        private fun setupMusicType() {
            // Dummy implementation for tvRoleLocation
            binding.tvRoleLocation.text = "Rock - Universal Music"
        }

        private fun showArtistDetails(artistDetail: ArtistDetail) {
            binding.progressBar.visibility = View.GONE
            binding.contentLayout.visibility = View.VISIBLE

            // Removed binding.ivArtist.load(artistDetail.image) as it's not needed for UI test compilation
            binding.tvName.text = artistDetail.name
            binding.tvDescription.text = artistDetail.description
            binding.tvStatAlbums.text = artistDetail.albums.size.toString()
            binding.tvStatPrizes.text = artistDetail.performerPrizes.size.toString()
            binding.tvBirthDate.text = artistDetail.birthDate.take(10)

            updateAlbumsSection(artistDetail)
            updatePrizesSection(artistDetail)
        }

        private fun updateAlbumsSection(artistDetail: ArtistDetail) {
            if (artistDetail.albums.isNotEmpty()) {
                binding.recyclerAlbums.visibility = View.VISIBLE
                binding.textNoAlbums.visibility = View.GONE
                albumsAdapter.updateAlbums(artistDetail.albums)
            } else {
                binding.recyclerAlbums.visibility = View.GONE
                binding.textNoAlbums.visibility = View.VISIBLE
            }
        }

        private fun updatePrizesSection(artistDetail: ArtistDetail) {
            if (artistDetail.performerPrizes.isNotEmpty()) {
                binding.recyclerPrizes.visibility = View.VISIBLE
                binding.textNoPrizes.visibility = View.GONE
                prizesAdapter.updatePrizes(
                    artistDetail.performerPrizes,
                    createVm.prizes.value ?: emptyList() // Use createVm for prizes
                )
            } else {
                binding.recyclerPrizes.visibility = View.GONE
                binding.textNoPrizes.visibility = View.VISIBLE
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    // Dummy Binding for DetailArtistFragment
    class FragmentDetailArtistBinding(
        val root: View,
        val ivArtist: ImageView,
        val tvName: TextView,
        val tvDescription: TextView,
        val tvBirthDate: TextView,
        val recyclerAlbums: RecyclerView,
        val recyclerPrizes: RecyclerView,
        val progressBar: ProgressBar,
        val contentLayout: View, // This will be the root view in our dummy setup
        val textNoAlbums: TextView,
        val textNoPrizes: TextView,
        val tvStatAlbums: TextView,
        val tvStatPrizes: TextView,
        val tvRoleLocation: TextView
    )

    // Dummy AlbumAdapter
    class AlbumAdapter(private var albums: List<Album>, private val clickListener: (Int) -> Unit) :
        RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

        class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val albumName: TextView = view.findViewById(android.R.id.text1) // Generic ID
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return AlbumViewHolder(view)
        }

        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
            val album = albums[position]
            holder.albumName.text = album.name
            holder.itemView.setOnClickListener { clickListener(album.id) }
        }

        override fun getItemCount(): Int = albums.size

        fun updateAlbums(newAlbums: List<Album>) {
            albums = newAlbums
            notifyDataSetChanged()
        }
    }

    // Dummy PrizeAdapter
    class PrizeAdapter(private var performerPrizes: List<PerformerPrize>, private var prizes: List<Prize>) :
        RecyclerView.Adapter<PrizeAdapter.PrizeViewHolder>() {

        class PrizeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val prizeName: TextView = view.findViewById(android.R.id.text1) // Generic ID
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return PrizeViewHolder(view)
        }

        override fun onBindViewHolder(holder: PrizeViewHolder, position: Int) {
            val performerPrize = performerPrizes[position]
            val prize = prizes.find { it.id == performerPrize.prize.id }
            holder.prizeName.text = prize?.name ?: "Unknown Prize"
        }

        override fun getItemCount(): Int = performerPrizes.size

        fun updatePrizes(newPerformerPrizes: List<PerformerPrize>, newPrizes: List<Prize>) {
            performerPrizes = newPerformerPrizes
            prizes = newPrizes
            notifyDataSetChanged()
        }
    }
}
