package de.jonashaeusler.vertretungsplan.ui.main

import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.data.entities.Event
import de.jonashaeusler.vertretungsplan.data.network.OnEventsFetched
import de.jonashaeusler.vertretungsplan.data.network.api.ExamTask

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

    override fun onEventFetchError(message: String) = showErrorView(message)

    override fun onStop() {
        super.onStop()
        examTask?.cancel(true)
    }
}
