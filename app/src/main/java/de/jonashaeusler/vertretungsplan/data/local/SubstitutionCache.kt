package de.jonashaeusler.vertretungsplan.data.local

import android.content.Context
import android.preference.PreferenceManager
import de.jonashaeusler.vertretungsplan.data.entities.Event

/**
 * Holds a copy of the last fetched substitutions.
 * To update the local copy call [updateSubstitutes]. A list of all new substitutes can be
 * retrieved by calling [getNewSubstitutes].
 */
class SubstitutionCache(private val context: Context) {

    private var substitutionStore: List<String>
        get() {
            return PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("substitution-store", "")!!
                    .split(", ")
        }
        set(value) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString("substitution-store", value.joinToString(", "))
                    .apply()
        }

    /**
     * Updates the locally stored substitutes with [newSubstitutes].
     */
    fun updateSubstitutes(newSubstitutes: List<Event>) {
        substitutionStore = newSubstitutes.map { it.hashCode().toString() }
    }

    /**
     * Compares the locally stored substitutions with [newSubstitutes] and returns a list of the
     * substitutes which are available in [newSubstitutes] but not the local copy.
     */
    fun getNewSubstitutes(newSubstitutes: List<Event>): List<Event> {
        return newSubstitutes.filterNot {
            substitutionStore.contains(it.hashCode().toString())
        }
    }
}
