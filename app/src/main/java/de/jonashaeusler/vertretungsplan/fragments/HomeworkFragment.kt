package de.jonashaeusler.vertretungsplan.fragments

import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.network.HomeworkTask

class HomeworkFragment : EventFragment() {
    override var eventTask: AsyncTask<String, Long, Boolean>? = null
        get() {
            return HomeworkTask(this)
        }
}
