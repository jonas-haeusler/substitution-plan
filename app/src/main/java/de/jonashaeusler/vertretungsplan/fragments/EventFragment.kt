package de.jonashaeusler.vertretungsplan.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
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
import kotlinx.android.synthetic.main.fragment_events.*

abstract class EventFragment : Fragment(), OnEventsFetched {
    abstract var eventTask: AsyncTask<String, Long, Boolean>?
    private lateinit var adapter: EventAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var completedEvents: MutableSet<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_events, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        loadCompletedEvents()
        setupRecyclerView()
        loadEvents()
        reload.setOnClickListener { loadEvents() }
        swipeRefreshLayout.setOnRefreshListener { loadEvents() }
    }

    override fun onEventFetchSuccess(events: List<Event>) {
        adapter.addAll(events
                .filterNot {
                    it.type == Event.EventType.TYPE_SUBSTITUTE
                            && it.getDateInMs() + DateUtils.DAY_IN_MILLIS > System.currentTimeMillis()
                }
                .onEach { it.completed = completedEvents.contains(it.hashCode().toString()) })
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
        eventTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                activity.getUsername(), activity.getPassword())
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter((emptyList<Event>()).toMutableList())

        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity))
        adapter.itemClickListener = { showSubstituteInfo(it) }
        adapter.checkedChangedListener = { event: Event, value: Boolean ->
            if (value) {
                addCompletedEvent(event)
            } else {
                removeCompletedEvent(event)
            }
        }
    }

    private fun loadCompletedEvents() {
        completedEvents = sharedPreferences
                .getString("events_completed", "")
                .split(", ")
                .toMutableSet()
    }

    private fun addCompletedEvent(event: Event) {
        completedEvents.add(event.hashCode().toString())

        sharedPreferences
                .edit()
                .putString("events_completed", completedEvents.joinToString())
                .apply()
    }

    private fun removeCompletedEvent(event: Event) {
        completedEvents.remove(event.hashCode().toString())

        sharedPreferences
                .edit()
                .putString("events_completed", completedEvents.joinToString())
                .apply()
    }

    private fun showSubstituteInfo(event: Event) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(event.title)
        builder.setMessage("${event.date}\n\n${event.text}")
        builder.setPositiveButton(R.string.okay) { _, _ -> }
        builder.create().show()
    }

    private fun showErrorView() {
        swipeRefreshLayout.isRefreshing = false
        containerConnectionError.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun showRecyclerView() {
        swipeRefreshLayout.isRefreshing = false
        recyclerView.visibility = View.VISIBLE
        containerConnectionError.visibility = View.GONE
    }

    private fun showLoadingView() {
        swipeRefreshLayout.isRefreshing = true
        recyclerView.visibility = View.GONE
        containerConnectionError.visibility = View.GONE
        emptyView.visibility = View.GONE
    }
}
