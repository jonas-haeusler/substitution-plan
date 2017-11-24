package de.jonashaeusler.vertretungsplan.models

import java.text.SimpleDateFormat
import java.util.*

/**
 * An upcoming event.
 * Can be almost anything, eg. homework or class tests.
 */
data class Event(val date: String, val title: String, val text: String,
                 val type: EventType, var completed: Boolean = false) {

    enum class EventType {
        TYPE_HOMEWORK,
        TYPE_SUBSTITUTE,
        TYPE_EXAM
    }

    override fun hashCode() = "$date$title$text$".hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (date != other.date) return false
        if (title != other.title) return false
        if (text != other.text) return false
        if (type != other.type) return false
        if (completed != other.completed) return false

        return true
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