package de.jonashaeusler.vertretungsplan.fragments

import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.interfaces.OnEventsFetched
import de.jonashaeusler.vertretungsplan.models.Event
import de.jonashaeusler.vertretungsplan.network.HomeworkTask

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

    override fun onEventFetchError() {
        showErrorView()
    }

    override fun onStop() {
        super.onStop()
        homeworkTask?.cancel(true)
    }
}
