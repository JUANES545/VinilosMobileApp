package com.example.vinilosmobileapp.utils

import android.content.Context
import android.content.SharedPreferences

object FavoritesManager {
    private const val PREFS = "artist_prefs"
    private const val KEY_FAVS = "favorite_artists"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isFavorite(ctx: Context, artistId: Int): Boolean =
        prefs(ctx).getStringSet(KEY_FAVS, emptySet())!!.contains(artistId.toString())

    fun toggleFavorite(ctx: Context, artistId: Int) {
        val set = prefs(ctx).getStringSet(KEY_FAVS, mutableSetOf())!!.toMutableSet()
        val key = artistId.toString()
        if (set.contains(key)) set.remove(key) else set.add(key)
        prefs(ctx).edit().putStringSet(KEY_FAVS, set).apply()
    }
}
