package de.jonashaeusler.vertretungsplan.fragments

import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.interfaces.OnEventsFetched
import de.jonashaeusler.vertretungsplan.models.Event
import de.jonashaeusler.vertretungsplan.network.ExamTask

class ExamFragment : EventFragment(), OnEventsFetched {
    override val useInfoCard = false
    private var examTask: ExamTask? = null

    override fun loadEvents() {
        examTask = ExamTask(this)
        examTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    override fun onEventFetchSuccess(events: List<Event>) {
        postEvents(events)
    }

    override fun onEventFetchError() {
        showErrorView()
    }

    override fun onStop() {
        super.onStop()
        examTask?.cancel(true)
    }
}
