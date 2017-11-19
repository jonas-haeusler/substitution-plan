package de.jonashaeusler.vertretungsplan.fragments

import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.network.SubstitutionTask
import java.lang.ref.WeakReference

class SubstitutionFragment : EventFragment() {
    override var eventTask: AsyncTask<String, Long, Boolean>? = null
        get() {
            return SubstitutionTask(WeakReference(activity), this)
        }
}
