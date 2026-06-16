package com.rork.sergolfandroid.data

enum class EventType { WEEKLY, SPECIAL }

data class GolfEvent(
    val id: String,
    val title: String,
    val date: String,
    val dayOfWeek: String,
    val time: String,
    val location: String,
    val description: String,
    val type: EventType,
    val spotsInfo: String,
    val imageUrl: String,
    val bookingUrl: String,
    val cancelled: Boolean = false,
)
