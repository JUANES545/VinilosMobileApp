package com.example.vinilosmobileapp.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.vinilosmobileapp.R

object ErrorHandler {

    /**
     * Displays an error state by hiding the RecyclerView and showing an error layout
     * with a message, icon, and optional retry button.
     */
    fun showErrorState(
        swipeRefreshLayout: SwipeRefreshLayout,
        recyclerView: View,
        errorLayout: View,
        errorMessage: String,
        errorIconRes: Int,
        showRetryButton: Boolean = true
    ) {
        swipeRefreshLayout.isRefreshing = false
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE

        val errorImage = errorLayout.findViewById<ImageView>(R.id.imageError)
        val errorText = errorLayout.findViewById<TextView>(R.id.textError)
        val retryButton = errorLayout.findViewById<MaterialButton>(R.id.buttonRetry)

        errorImage.setImageResource(errorIconRes)
        errorText.text = errorMessage
        retryButton.visibility = if (showRetryButton) View.VISIBLE else View.GONE
    }

    /**
     * Displays an empty state by hiding the RecyclerView and showing an error layout
     * with a message and icon indicating no data is available.
     */
    fun showEmptyState(
        swipeRefreshLayout: SwipeRefreshLayout,
        recyclerView: View,
        errorLayout: View,
        emptyMessage: String,
        emptyIconRes: Int
    ) {
        swipeRefreshLayout.isRefreshing = false
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE

        val errorImage = errorLayout.findViewById<ImageView>(R.id.imageError)
        val errorText = errorLayout.findViewById<TextView>(R.id.textError)

        errorImage.setImageResource(emptyIconRes)
        errorText.text = emptyMessage
    }

}
