package de.jonashaeusler.vertretungsplan.data.entities

data class Course(
        val course: String,
        var enabled: Boolean,
        val regex: String
)
