package de.jonashaeusler.vertretungsplan.models

import java.text.SimpleDateFormat
import java.util.*

data class Substitute(var classes: String = "", var period: String = "", var type: String = "",
                      var subjectInstead: String = "", var subject: String = "", var roomInstead: String = "",
                      var room: String = "", var text: String = "", var date: String = "") {

    fun getDateInMs(): Long {
        val dateFormat = if (date.length == 8) {
            SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        } else {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        }

        return dateFormat.parse(this.date).time
    }
}