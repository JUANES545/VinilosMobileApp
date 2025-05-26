package com.example.vinilosmobileapp.ui.artist

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import org.mockito.Mockito // Import Mockito explicitly
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.timeout
import androidx.recyclerview.widget.RecyclerView
// import androidx.test.espresso.contrib.RecyclerViewActions // UNRESOLVED REFERENCE: Add 'espresso-contrib' dependency to build.gradle
import android.widget.TextView // For checking text in RecyclerView items
import android.view.LayoutInflater // For dummy fragment
import android.view.ViewGroup // For dummy fragment
import android.os.Bundle // For dummy fragment
import android.widget.FrameLayout // For programmatically creating layout

@RunWith(AndroidJUnit4::class)
class ArtistListFragmentTest {

    // Mock the ArtistViewModel
    private lateinit var mockArtistViewModel: ArtistViewModel

    // LiveData to control the artists returned by the mock ViewModel
    private val artistsLiveData = MutableLiveData<List<Artist>?>()

    // Define a constant ID for the RecyclerView for testing purposes.
    // In your actual project, this would be R.id.recyclerViewArtists.
    private val RECYCLER_VIEW_ID = View.generateViewId() // Generate a unique ID for the test

    @Before
    fun setup() {
        // Initialize the mock ViewModel
        mockArtistViewModel = mock(ArtistViewModel::class.java)

        // FIX: Use doReturn().when() for stubbing LiveData property getter
        Mockito.doReturn(artistsLiveData).`when`(mockArtistViewModel).artists

        // Stub the fetchArtists() method to do nothing, as we control the LiveData directly
        // Or, if you want to test that fetchArtists is called, you can verify it later.
        // For this test, we'll directly post values to artistsLiveData.
    }

    @Test
    fun shouldDisplayArtistsInRecyclerView() {
        // Create a list of dummy artists
        val dummyArtists = listOf(
            Artist(1, "Artist A", "url_a", "1990-01-01", "Description A"),
            Artist(2, "Artist B", "url_b", "1985-05-10", "Description B"),
            Artist(3, "Artist C", "url_c", "2000-11-20", "Description C")
        )

        // 1. Define a ViewModelProvider.Factory that provides the mock ViewModel.
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ArtistViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return mockArtistViewModel as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        // 2. Define a FragmentFactory to inject the ViewModelFactory into the dummy fragment.
        val customFragmentFactory = object : androidx.fragment.app.FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): androidx.fragment.app.Fragment {
                return when (className) {
                    ArtistListFragment::class.java.name -> {
                        // Pass the generated ID to the dummy fragment
                        ArtistListFragment(viewModelFactory, RECYCLER_VIEW_ID)
                    }
                    else -> super.instantiate(classLoader, className)
                }
            }
        }

        // Launch the fragment in the test container
        val scenario = launchFragmentInContainer<ArtistListFragment>(
            themeResId = R.style.Theme_VinilosMobileApp,
            factory = customFragmentFactory
        )

        // Post the dummy artists to the LiveData. This will trigger the fragment's observer
        // and update the RecyclerView.
        artistsLiveData.postValue(dummyArtists)

        // Verify that the RecyclerView is displayed using the generated ID
        onView(withId(RECYCLER_VIEW_ID)).check(matches(isDisplayed()))

        // Verify that each artist's name is displayed in the RecyclerView
        // To enable RecyclerViewActions, add 'androidx.test.espresso:espresso-contrib' to your build.gradle
        // Example: debugImplementation 'androidx.test.espresso:espresso-contrib:3.5.1'
        // onView(withId(RECYCLER_VIEW_ID)) // Use the generated ID here
        //     .perform(RecyclerViewActions.scrollToPosition<ArtistAdapter.ArtistViewHolder>(0))
        //     .check(matches(hasDescendant(withText("Artist A"))))

        // Alternative check without RecyclerViewActions (less robust for large lists)
        onView(withText("Artist A")).check(matches(isDisplayed()))
        onView(withText("Artist B")).check(matches(isDisplayed()))
        onView(withText("Artist C")).check(matches(isDisplayed()))


        // Optionally, verify that fetchArtists was called on the ViewModel
        // FIX: Added .atLeastOnce() for more robust asynchronous verification
        verify(mockArtistViewModel, timeout(2000).atLeastOnce()).fetchArtists()
    }


    /**
     * Custom Matcher to check the number of items in a RecyclerView.
     */
    // --- DUMMY CLASSES FOR COMPILATION ---
    // IMPORTANT: Replace these dummy classes with your actual classes.
    // Ensure your real ArtistViewModel is 'open'.

    // Dummy Artist data class
    data class Artist(
        val id: Int,
        val name: String,
        val image: String,
        val birthDate: String,
        val description: String
    )

    // Dummy ArtistViewModel (must be 'open' for Mockito)
    open class ArtistViewModel : ViewModel() {
        // FIX: Made LiveData properties 'open' so Mockito can stub them.
        open val artists: LiveData<List<Artist>?> = MutableLiveData()
        open val errorMessage: LiveData<String?> = MutableLiveData()
        open val prizeSeedingResult: LiveData<Boolean> = MutableLiveData()

        fun fetchArtists() {
            // Dummy implementation, actual behavior is mocked
        }

        fun seedPrizes() {
            // Dummy implementation
        }
    }

    // Dummy ArtistListFragment
    class ArtistListFragment(
        private val viewModelFactory: ViewModelProvider.Factory? = null,
        private val recyclerViewId: Int = View.NO_ID // Accept RecyclerView ID in constructor
    ) : androidx.fragment.app.Fragment() { // Removed fixed layout ID here

        private var _binding: FragmentArtistListBinding? = null
        val binding get() = _binding!!

        private lateinit var vm: ArtistViewModel
        private lateinit var artistAdapter: ArtistAdapter

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            // Programmatically create a FrameLayout and add a RecyclerView to it.
            // This ensures the RecyclerView exists in the view hierarchy for Espresso.
            val frameLayout = FrameLayout(requireContext())
            val recyclerView = RecyclerView(requireContext())
            recyclerView.id = recyclerViewId // Assign the ID passed from the test
            frameLayout.addView(recyclerView, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ))

            _binding = FragmentArtistListBinding(frameLayout, recyclerViewId) // Pass the FrameLayout and ID
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            vm = if (viewModelFactory != null) {
                ViewModelProvider(this, viewModelFactory).get(ArtistViewModel::class.java)
            } else {
                ViewModelProvider(this).get(ArtistViewModel::class.java)
            }

            setupRecyclerView()

            // Observe the artists LiveData from the ViewModel
            vm.artists.observe(viewLifecycleOwner) { artistList ->
                artistList?.let {
                    artistAdapter.updateArtists(it)
                }
            }

            // Trigger the fetch when the fragment is created
            vm.fetchArtists()
        }

        private fun setupRecyclerView() {
            artistAdapter = ArtistAdapter(emptyList()) // Initialize with empty list
            binding.recyclerViewArtists.adapter = artistAdapter
            binding.recyclerViewArtists.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    // Dummy Adapter for the RecyclerView
    class ArtistAdapter(private var artists: List<Artist>) :
        RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

        class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            // FIX: Using generic Android TextView ID
            val artistName: TextView = view.findViewById(android.R.id.text1)
            // Add other views if needed
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
            // FIX: Inflate a generic Android item layout for the RecyclerView
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ArtistViewHolder(view)
        }

        override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
            val artist = artists[position]
            holder.artistName.text = artist.name
            // Bind other data if necessary
        }

        override fun getItemCount(): Int = artists.size

        fun updateArtists(newArtists: List<Artist>) {
            artists = newArtists
            notifyDataSetChanged()
        }
    }

    // Dummy Binding for ArtistListFragment
    // This dummy binding now finds the RecyclerView from the programmatically created view.
    class FragmentArtistListBinding(private val rootView: View, recyclerViewId: Int) {
        val recyclerViewArtists: RecyclerView = rootView.findViewById(recyclerViewId)
        val root: View
            get() = rootView
    }
}
