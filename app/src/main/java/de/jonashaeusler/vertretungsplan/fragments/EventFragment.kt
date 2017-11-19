package de.jonashaeusler.vertretungsplan.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.adapter.EventAdapter
import de.jonashaeusler.vertretungsplan.helpers.getPassword
import de.jonashaeusler.vertretungsplan.helpers.getUsername
import de.jonashaeusler.vertretungsplan.interfaces.OnEventsFetched
import de.jonashaeusler.vertretungsplan.models.Event
import de.jonashaeusler.vertretungsplan.widgets.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_substitutes.*

abstract class EventFragment : Fragment(), OnEventsFetched {
    abstract var eventTask: AsyncTask<String, Long, Boolean>?
    private lateinit var adapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_substitutes, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadEvents()
        reload.setOnClickListener { loadEvents() }
    }

    override fun onEventFetchSuccess(events: List<Event>) {
        adapter.addAll(events.filter { it.getDateInMs() + DateUtils.DAY_IN_MILLIS > System.currentTimeMillis() })
        showRecyclerView()
    }

    override fun onEventFetchError() {
        showErrorView()
    }

    override fun onStop() {
        super.onStop()
        eventTask?.cancel(true)
    }

    fun loadEvents() {
        showLoadingView()
        adapter.events.clear()
        eventTask?.executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, activity.getUsername(), activity.getPassword())
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter((emptyList<Event>()).toMutableList())

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity))
        adapter.itemClickListener = { showSubstituteInfo(it) }
    }

    private fun showSubstituteInfo(event: Event) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(event.title)
        builder.setMessage("${event.date}\n\n${event.text}")
        builder.setPositiveButton(R.string.okay) { _, _ -> }
        builder.create().show()
    }

    private fun showErrorView() {
        containerConnectionError.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showRecyclerView() {
        recyclerView.visibility = View.VISIBLE
        containerConnectionError.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showLoadingView() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        containerConnectionError.visibility = View.GONE
    }
}
