package com.rork.sergolfandroid.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

/**
 * Singleton manager for persisting booked event IDs across app restarts.
 * Uses SharedPreferences so the booked state survives app close/reopen cycles.
 */
object BookedEventsManager {

    private const val PREFS_NAME = "ser_golf_prefs"
    private const val KEY_BOOKED_IDS = "booked_event_ids"

    private lateinit var prefs: SharedPreferences

    private val bookedIds: MutableSet<String> = mutableSetOf()

    /**
     * Must be called once from [MainActivity.onCreate] before any screen reads state.
     * Loads previously persisted booked IDs into memory.
     */
    fun init(context: Context) {
        if (this::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFromDisk()
    }

    fun isBooked(eventId: String): Boolean = bookedIds.contains(eventId)

    fun addBooked(eventId: String) {
        if (bookedIds.add(eventId)) persist()
    }

    fun removeBooked(eventId: String) {
        if (bookedIds.remove(eventId)) persist()
    }

    private fun loadFromDisk() {
        val raw = prefs.getString(KEY_BOOKED_IDS, null) ?: return
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val id = arr.optString(i)
                if (id.isNotBlank()) bookedIds.add(id)
            }
        } catch (_: Exception) {
            // ignore corrupt data
        }
    }

    private fun persist() {
        val arr = JSONArray()
        bookedIds.forEach { arr.put(it) }
        prefs.edit().putString(KEY_BOOKED_IDS, arr.toString()).apply()
    }
}
