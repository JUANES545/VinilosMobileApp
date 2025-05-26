package com.example.vinilosmobileapp.ui.artist

import android.view.View
import android.view.LayoutInflater
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.timeout
import org.mockito.Mockito.`when`
import org.mockito.ArgumentMatchers.any // Import for Mockito.any()
import org.mockito.ArgumentMatchers.argThat // Import for Mockito.argThat()
import androidx.navigation.fragment.findNavController
import com.example.vinilosmobileapp.R // Ensure R is imported

@RunWith(AndroidJUnit4::class)
class CreateArtistFragmentTest {

    private lateinit var mockNavController: NavController
    private lateinit var mockCreateArtistViewModel: CreateArtistViewModel

    @Before
    fun setup() {
        mockNavController = mock(NavController::class.java)
        mockCreateArtistViewModel = mock(CreateArtistViewModel::class.java)
    }

    @Test
    fun shouldShowValidationErrorsWhenFieldsAreEmpty() {
        val scenario = launchFragmentInContainer<CreateArtistFragment>(
            themeResId = R.style.Theme_VinilosMobileApp
        )

        onView(withId(R.id.btnCreateArtist)).perform(click())

        onView(withId(R.id.tilName)).check(matches(withTextInputLayoutErrorText("Nombre obligatorio")))
        onView(withId(R.id.tilBirth)).check(matches(withTextInputLayoutErrorText("Fecha de nacimiento requerida")))
        onView(withId(R.id.tilDesc)).check(matches(withTextInputLayoutErrorText("Descripción requerida")))
    }

    private fun withTextInputLayoutErrorText(expectedError: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("con error de TextInputLayout: $expectedError")
            }

            override fun matchesSafely(view: View): Boolean {
                if (view !is TextInputLayout) return false
                val error = view.error ?: return false
                return error.toString() == expectedError
            }
        }
    }

    // --- DUMMY CLASSES FOR COMPILATION ---
    // IMPORTANT: Replace these dummy classes with your actual ViewModel and Fragment classes.
    // Ensure your real CreateArtistViewModel has the 'createArtist' method taking ArtistCreateDTO
    // and is 'open'.

    // Dummy DTO class to match the argument type in the real ViewModel
    data class ArtistCreateDTO(
        val name: String,
        val image: String,
        val birthDate: String,
        val description: String
    )

    open class CreateArtistViewModel : ViewModel() {
        // Updated dummy method to accept ArtistCreateDTO
        fun createArtist(dto: ArtistCreateDTO) {
            println("Dummy: Creating artist: ${dto.name}, ${dto.description}, ${dto.birthDate}")
        }
    }

    class CreateArtistFragment(
        private val viewModelFactory: ViewModelProvider.Factory? = null
    ) : androidx.fragment.app.Fragment(R.layout.fragment_create_artist) {

        private var _binding: FragmentCreateArtistBinding? = null
        val binding get() = _binding!!

        private lateinit var vm: CreateArtistViewModel

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val view = inflater.inflate(R.layout.fragment_create_artist, container, false)
            _binding = FragmentCreateArtistBinding(view)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            vm = if (viewModelFactory != null) {
                ViewModelProvider(this, viewModelFactory).get(CreateArtistViewModel::class.java)
            } else {
                ViewModelProvider(this).get(CreateArtistViewModel::class.java)
            }

            binding.btnCreateArtist.setOnClickListener {
                val name = binding.etName.text.toString().trim()
                val desc = binding.etDesc.text.toString().trim()
                val birth = binding.etBirth.text.toString().trim() // Dummy: assumes format

                var isValid = true
                if (name.isNullOrBlank()) {
                    binding.tilName.error = "Nombre obligatorio"; isValid = false
                } else { binding.tilName.error = null }
                if (birth.isNullOrBlank()) {
                    binding.tilBirth.error = "Fecha de nacimiento requerida"; isValid = false
                } else { binding.tilBirth.error = null }
                if (desc.isNullOrBlank()) {
                    binding.tilDesc.error = "Descripción requerida"; isValid = false
                } else { binding.tilDesc.error = null }

                if (isValid) {
                    // --- DUMMY: Create a DTO to match the ViewModel's expected method signature ---
                    // In your real fragment, this is done via sendAlbumData()
                    val dummyDto = ArtistCreateDTO(
                        name = name,
                        image = "https://dummy.image/url", // Dummy URL for the test
                        birthDate = birth,
                        description = desc
                    )
                    vm.createArtist(dummyDto) // Call with DTO
                    findNavController().popBackStack()
                }
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    class FragmentCreateArtistBinding(private val view: View) {
        val etName: android.widget.EditText = view.findViewById(R.id.etName)
        val etDesc: android.widget.EditText = view.findViewById(R.id.etDesc)
        val etBirth: android.widget.EditText = view.findViewById(R.id.etBirth)
        val tilName: TextInputLayout = view.findViewById(R.id.tilName)
        val tilBirth: TextInputLayout = view.findViewById(R.id.tilBirth)
        val tilDesc: TextInputLayout = view.findViewById(R.id.tilDesc)
        val btnCreateArtist: android.widget.Button = view.findViewById(R.id.btnCreateArtist)

        val root: View
            get() = view
    }
}
