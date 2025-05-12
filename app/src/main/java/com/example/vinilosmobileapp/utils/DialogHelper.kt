package com.example.vinilosmobileapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.example.vinilosmobileapp.R
import com.example.vinilosmobileapp.model.PerformerPrize
import com.example.vinilosmobileapp.ui.artist.CreateArtistViewModel
import com.example.vinilosmobileapp.ui.artist.adapter.PrizeInputAdapter
import com.example.vinilosmobileapp.ui.home.adapter.AlbumAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.text.SimpleDateFormat
import java.util.*

object DialogHelper {

    fun hideKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = (context as? android.app.Activity)?.currentFocus
        currentFocus?.windowToken?.let { token ->
            inputMethodManager.hideSoftInputFromWindow(token, 0)
        }
    }

    @SuppressLint("SetTextI18n")
    fun showAddAlbumDialog(
        layoutInflater: LayoutInflater,
        lifecycleOwner: LifecycleOwner,
        viewModel: CreateArtistViewModel,
        albumAdapter: AlbumAdapter
    ) {
        val context = layoutInflater.context
        val albumsLiveData = viewModel.albums
        val errorMessageLiveData = viewModel.errorMessage


        val dialogView = layoutInflater.inflate(R.layout.dialog_add_album, null)
        val dropdownAlbum =
            dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.dropdown_album).apply {
                isFocusable = false
                isFocusableInTouchMode = false
            }
        val guestHint = dialogView.findViewById<TextView>(R.id.guest_hint)

        val builder = MaterialAlertDialogBuilder(context)
            .setTitle("Selecciona un álbum")
            .setView(dialogView)
            .setPositiveButton("Agregar", null)
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()

        hideKeyboard(context)
        viewModel.fetchAlbums()

        albumsLiveData.observe(lifecycleOwner) { albums ->
            if (albums.isNullOrEmpty()) {
                guestHint.text = "No hay álbumes disponibles"
                guestHint.visibility = View.VISIBLE
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
            } else {
                guestHint.visibility = View.GONE
                val filteredList = albums.filter { album ->
                    albumAdapter.albumList.none { it.id == album.id }
                }
                val names = filteredList.map { it.name }
                val adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    names
                )
                dropdownAlbum.setAdapter(adapter)
                if (names.isNotEmpty()) {
                    dropdownAlbum.setText(names.random(), false)
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = true
                } else {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
                }

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val selected = dropdownAlbum.text.toString().trim()
                    if (selected.isEmpty()) {
                        dropdownAlbum.error = "Selecciona un álbum"
                        return@setOnClickListener
                    }
                    val index = names.indexOf(selected)
                    if (index >= 0) {
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

        errorMessageLiveData.observe(lifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                guestHint.text = "Error cargando álbumes"
                guestHint.visibility = View.VISIBLE
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
            }
        }
    }

    fun showAddPrizeDialog(
        layoutInflater: LayoutInflater,
        lifecycleOwner: LifecycleOwner,
        viewModel: CreateArtistViewModel,
        prizeAdapter: PrizeInputAdapter
    ) {
        val context = layoutInflater.context
        val prizesLiveData = viewModel.prizes
        val errorMessageLiveData = viewModel.errorMessage

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_prize, null)
        val prizeDropdown =
            dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.dropdownPrize).apply {
                isFocusable = false
                isFocusableInTouchMode = false
            }
        val inputDate =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputPrizeDate)
                .apply {
                    val randomCalendar = Calendar.getInstance().apply {
                        set((1950..2023).random(), (0..11).random(), (1..28).random())
                    }
                    setText(
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(randomCalendar.time)
                    )
                }

        val builder = MaterialAlertDialogBuilder(context)
            .setTitle("Agregar Premio")
            .setView(dialogView)
            .setPositiveButton("Agregar", null)
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()

        hideKeyboard(context)
        viewModel.fetchPrizes()

        prizesLiveData.observe(lifecycleOwner) { prizes ->
            if (prizes.isNullOrEmpty()) {
                prizeDropdown.setAdapter(
                    ArrayAdapter(
                        context,
                        android.R.layout.simple_dropdown_item_1line,
                        listOf("No hay premios disponibles")
                    )
                )
            } else {
                val names = prizes.map { it.name }
                prizeDropdown.setAdapter(
                    ArrayAdapter(
                        context,
                        android.R.layout.simple_dropdown_item_1line,
                        names
                    )
                )
                prizeDropdown.setText(names.random(), false)
            }
        }

        errorMessageLiveData.observe(lifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val prizeName = prizeDropdown.text.toString().trim()
            val date = inputDate.text.toString().trim()

            var valid = true
            if (prizeName.isEmpty()) {
                prizeDropdown.error = "Selecciona un premio"
                valid = false
            }
            if (date.isEmpty()) {
                inputDate.error = "Fecha requerida"
                valid = false
            }

            if (!valid) return@setOnClickListener

            val selectedPrize = prizesLiveData.value?.find { it.name == prizeName }
            if (selectedPrize != null) {
                val performerPrize = PerformerPrize(
                    id = selectedPrize.id,
                    premiationDate = date
                )
                prizeAdapter.addPrize(performerPrize)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Premio no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
