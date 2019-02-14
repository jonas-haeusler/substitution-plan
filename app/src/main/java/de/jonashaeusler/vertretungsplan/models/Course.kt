package de.jonashaeusler.vertretungsplan.models

data class Course(val course: String, var enabled: Boolean, val regex: String)
