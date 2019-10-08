package de.jonashaeusler.vertretungsplan.data.entities

/**
 * Basic model to parse basic timetable data from a dsb response
 */
data class Timetables(
        val id: String,
        val date: String,
        val title: String,
        val timetables: List<Timetable>
) {

    data class Timetable(
            val id: String,
            val date: String,
            val title: String,
            val url: String,
            val previewUrl: String
    )
}
