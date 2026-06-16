package com.rork.sergolfandroid.data

import android.util.Base64
import com.rork.sergolfandroid.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private const val BOOKWHEN_API_URL = "https://api.bookwhen.com/v2/events"
private const val ACCOUNT_SLUG = "ser-golf"

private val COURSE_IMAGES = listOf(
    "https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=800&q=80",
    "https://images.unsplash.com/photo-1535131749006-b7f58c99034b?w=800&q=80",
    "https://images.unsplash.com/photo-1592919505780-303950717480?w=800&q=80",
    "https://images.unsplash.com/photo-1600005082646-28a8e9e1f498?w=800&q=80",
    "https://images.unsplash.com/photo-1593111774240-d529f12cf4bb?w=800&q=80",
    "https://images.unsplash.com/photo-1622396636133-b43e6f94c42a?w=800&q=80",
    "https://images.unsplash.com/photo-1580126755789-85101eba6306?w=800&q=80",
)

private const val SPECIAL_IMAGE =
    "https://images.unsplash.com/photo-1611374243147-44a702c2d44c?w=800&q=80"

const val FALLBACK_IMAGE =
    "https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=800&q=80"

private val MONTHS = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

private val DAYS = listOf(
    "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
)

@Serializable
private data class BookwhenResponse(
    val data: List<BookwhenEvent>? = null,
)

@Serializable
private data class BookwhenEvent(
    val id: String,
    val attributes: BookwhenAttributes? = null,
)

@Serializable
private data class BookwhenAttributes(
    val title: String? = null,
    val details: String? = null,
    @SerialName("details_html") val detailsHtml: String? = null,
    val location: String? = null,
    @SerialName("location_text") val locationText: String? = null,
    @SerialName("start_at") val startAt: String? = null,
    @SerialName("end_at") val endAt: String? = null,
    @SerialName("all_day") val allDay: Boolean? = null,
    val tags: List<String>? = null,
    val cancelled: Boolean? = null,
    val status: String? = null,
    @SerialName("cancelled_at") val cancelledAt: String? = null,
)

class BookwhenService {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun fetchEvents(): List<GolfEvent> {
        val apiKey = Config.allValues["EXPO_PUBLIC_BOOKWHEN_API_KEY"]
            ?: throw IllegalStateException("Missing Bookwhen API key")

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val from = String.format(
            Locale.US,
            "%04d%02d%02d%02d%02d%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            cal.get(Calendar.SECOND),
        )
        val url = "$BOOKWHEN_API_URL?filter[from]=$from&page[size]=100"

        val token = Base64.encodeToString("$apiKey:".toByteArray(), Base64.NO_WRAP)

        val response: HttpResponse = client.get(url) {
            header("Authorization", "Basic $token")
            header("Accept", "application/vnd.api+json")
        }

        if (!response.status.isSuccess()) {
            val body = response.bodyAsText().take(200)
            throw IllegalStateException("Bookwhen request failed: ${response.status.value} $body")
        }

        val json = response.body<BookwhenResponse>()
        val items = json.data ?: emptyList()

        return items
            .mapIndexedNotNull { index, event -> mapToGolfEvent(event, index) }
            .sortedBy { parseDisplayDate(it.date) }
    }

    private fun mapToGolfEvent(ev: BookwhenEvent, index: Int): GolfEvent? {
        val a = ev.attributes ?: return null
        val startStr = a.startAt ?: return null
        val start = parseIso(startStr) ?: return null

        val rawTitle = a.title?.trim().orEmpty().ifEmpty { "SER Event" }
        val cancelled = isCancelled(a, rawTitle)
        val title = rawTitle
            .replace(Regex("\\s*\\(?cancelled\\)?\\s*", RegexOption.IGNORE_CASE), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .ifEmpty { rawTitle }
        val type = classify(title)
        val allDay = a.allDay == true
        val location = (a.location ?: a.locationText)?.trim().orEmpty().ifEmpty {
            when {
                title.contains("killermont", true) -> "Killermont"
                title.contains("gailes", true) -> "Gailes"
                else -> "Glasgow Golf Club"
            }
        }

        val description = stripHtml(a.detailsHtml ?: a.details).ifEmpty {
            "Join the Sunday Early Risers for this event. Tap Book Now for full details on Bookwhen."
        }

        return GolfEvent(
            id = ev.id,
            title = title,
            date = formatDateDisplay(start),
            dayOfWeek = DAYS[start.get(Calendar.DAY_OF_WEEK) - 1],
            time = formatTime(start, allDay),
            location = location,
            description = description,
            type = type,
            spotsInfo = when {
                cancelled -> "This event has been cancelled"
                type == EventType.SPECIAL -> "Limited places available"
                else -> "Open to all SER members"
            },
            imageUrl = pickImage(type, index, title),
            bookingUrl = "https://bookwhen.com/$ACCOUNT_SLUG/e/${ev.id}",
            cancelled = cancelled,
        )
    }

    private fun classify(title: String): EventType {
        val lower = title.lowercase()
        return if (lower.contains("sunday") && lower.contains("killermont")) {
            EventType.WEEKLY
        } else {
            EventType.SPECIAL
        }
    }

    private fun pickImage(type: EventType, index: Int, title: String): String {
        if (type == EventType.SPECIAL) return SPECIAL_IMAGE
        if (title.contains("gailes", true)) return SPECIAL_IMAGE
        return COURSE_IMAGES[index % COURSE_IMAGES.size]
    }

    private fun isCancelled(a: BookwhenAttributes, title: String): Boolean {
        if (!a.cancelledAt.isNullOrBlank()) return true
        if (a.cancelled == true) return true
        if (a.status?.lowercase() == "cancelled") return true
        if (a.tags?.any { it.lowercase().contains("cancel") } == true) return true
        val lower = title.lowercase()
        return lower.contains("cancelled") || lower.contains("canceled")
    }

    private fun parseIso(value: String): Calendar? {
        // Bookwhen start_at format e.g. 2026-06-21T08:00:00.000Z or with offset
        val regex = Regex("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2})")
        val match = regex.find(value) ?: return null
        return try {
            val (y, mo, d, h, mi) = match.destructured
            Calendar.getInstance().apply {
                clear()
                set(y.toInt(), mo.toInt() - 1, d.toInt(), h.toInt(), mi.toInt())
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDateDisplay(c: Calendar): String =
        "${c.get(Calendar.DAY_OF_MONTH)} ${MONTHS[c.get(Calendar.MONTH)]} ${c.get(Calendar.YEAR)}"

    private fun formatTime(c: Calendar, allDay: Boolean): String {
        if (allDay) return "Full Day Event"
        val h = c.get(Calendar.HOUR_OF_DAY)
        val m = c.get(Calendar.MINUTE)
        val period = if (h >= 12) "PM" else "AM"
        val hour12 = if (h % 12 == 0) 12 else h % 12
        val minStr = if (m == 0) "" else ":${String.format(Locale.US, "%02d", m)}"
        return "$hour12$minStr $period Tee-off"
    }

    private fun stripHtml(html: String?): String {
        if (html.isNullOrBlank()) return ""
        return html
            .replace(Regex("<[^>]+>"), " ")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun parseDisplayDate(display: String): Long {
        val parts = display.split(" ")
        return try {
            val day = parts[0].toInt()
            val month = MONTHS.indexOf(parts[1])
            val year = parts[2].toInt()
            Calendar.getInstance().apply {
                clear()
                set(year, month, day)
            }.timeInMillis
        } catch (e: Exception) {
            0L
        }
    }
}
