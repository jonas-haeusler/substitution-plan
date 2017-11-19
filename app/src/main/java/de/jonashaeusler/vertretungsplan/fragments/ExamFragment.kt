package de.jonashaeusler.vertretungsplan.fragments

import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.network.ExamTask

class ExamFragment : EventFragment() {
    override var eventTask: AsyncTask<String, Long, Boolean>? = null
        get() {
            return ExamTask(this)
        }
}
