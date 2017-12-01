package de.jonashaeusler.vertretungsplan.interfaces

interface OnServerStatusResolved {
    fun onServerStatusResolved(status: List<String>)
}