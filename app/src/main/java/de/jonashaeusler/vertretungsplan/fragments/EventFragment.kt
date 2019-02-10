package de.jonashaeusler.vertretungsplan.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.format.DateUtils
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.adapter.EventAdapter
import de.jonashaeusler.vertretungsplan.helpers.getIgnoredCoursesAsRegex
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance = true
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        loadCompletedEvents()
        setupRecyclerView()
        loadEvents()
        reload.setOnClickListener { loadEvents() }
        swipeRefreshLayout.setOnRefreshListener { loadEvents() }
    }

    override fun onEventFetchSuccess(events: List<Event>) {
        val ignoredCourses = requireContext().getIgnoredCoursesAsRegex()
        adapter.addAll(events
                .filter {
                    it.getDateInMs() + DateUtils.DAY_IN_MILLIS > System.currentTimeMillis()
                }.filterNot {
                    it.type != Event.EventType.TYPE_SUBSTITUTE && it.title.matches(ignoredCourses)
                }.filterNot {
                    it.type == Event.EventType.TYPE_SUBSTITUTE && it.text.contains(ignoredCourses)
                            && ignoredCourses.toString().isNotEmpty()
                }.onEach {
                    it.completed = completedEvents.contains(it.hashCode().toString())
                            && it.type == Event.EventType.TYPE_HOMEWORK
                })

        showRecyclerView()
    }

    override fun onEventFetchError() {
        showErrorView()
    }

    override fun onStop() {
        super.onStop()
        eventTask?.cancel(true)
    }

    abstract fun onReload()

    fun loadEvents() {
        showLoadingView()
        adapter.events.clear()
        eventTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                requireContext().getUsername(), requireContext().getPassword())

        onReload()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter((emptyList<Event>()).toMutableList())

        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
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
                .getString("events_completed", "")!!
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
        val dialog = AlertDialog.Builder(requireContext())
                .setTitle(event.title)
                .setMessage("${event.date}\n\n${event.text}")
                .setPositiveButton(R.string.okay) { _, _ -> }
                .create()
        dialog.show()

        Linkify.addLinks(dialog.findViewById<TextView>(android.R.id.message),
                Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES)
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
