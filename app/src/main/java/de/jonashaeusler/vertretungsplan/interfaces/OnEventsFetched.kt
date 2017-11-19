package de.jonashaeusler.vertretungsplan.interfaces

import de.jonashaeusler.vertretungsplan.models.Event

interface OnEventsFetched {
    fun onEventFetchSuccess(events: List<Event>)
    fun onEventFetchError()
}