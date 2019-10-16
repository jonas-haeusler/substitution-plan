package de.jonashaeusler.vertretungsplan.ui.main

import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.data.entities.Event
import de.jonashaeusler.vertretungsplan.data.network.OnEventsFetched
import de.jonashaeusler.vertretungsplan.data.network.api.HomeworkTask

class HomeworkFragment : EventFragment(), OnEventsFetched {
    override val useInfoCard = false
    private var homeworkTask: HomeworkTask? = null

    override fun loadEvents() {
        homeworkTask = HomeworkTask(this)
        homeworkTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    override fun onEventFetchSuccess(events: List<Event>) {
        postEvents(events)
    }

    override fun onEventFetchError(message: String) = showErrorView(message)

    override fun onStop() {
        super.onStop()
        homeworkTask?.cancel(true)
    }
}
