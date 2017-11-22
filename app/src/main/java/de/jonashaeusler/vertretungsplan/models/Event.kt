package de.jonashaeusler.vertretungsplan.models

import java.text.SimpleDateFormat
import java.util.*

/**
 * An upcoming event.
 * Can be almost anything, eg. homework or class tests.
 */
data class Event(val date: String, val title: String, val text: String, val type: EventType) {

    enum class EventType {
        TYPE_HOMEWORK,
        TYPE_SUBSTITUTE,
        TYPE_EXAM
    }

    fun getDateInMs(): Long {
        val dateFormat = if (date.length == 8) {
            SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        } else {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        }

        return dateFormat.parse(this.date).time
    }
}