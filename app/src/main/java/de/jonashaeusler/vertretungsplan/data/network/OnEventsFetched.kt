package de.jonashaeusler.vertretungsplan.data.network

import de.jonashaeusler.vertretungsplan.data.entities.Event

interface OnEventsFetched {
    fun onEventFetchSuccess(events: List<Event>)
    fun onEventFetchError()
}
