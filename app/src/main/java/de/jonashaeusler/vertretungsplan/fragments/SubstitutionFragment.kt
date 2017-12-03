package de.jonashaeusler.vertretungsplan.fragments

import android.os.AsyncTask
import android.view.View
import de.jonashaeusler.vertretungsplan.helpers.getClassShortcut
import de.jonashaeusler.vertretungsplan.interfaces.OnInfoResolved
import de.jonashaeusler.vertretungsplan.network.InfoTask
import de.jonashaeusler.vertretungsplan.network.SubstitutionTask
import kotlinx.android.synthetic.main.fragment_events.*
import java.lang.ref.WeakReference

class SubstitutionFragment : EventFragment(), OnInfoResolved {
    override var eventTask: AsyncTask<String, Long, Boolean>? = null
        get() {
            return SubstitutionTask(WeakReference(activity), this)
        }

    override fun onReload() {
        if (activity.getClassShortcut().contains(Regex("t?g?(i11|11/?4)", RegexOption.IGNORE_CASE))) {
            InfoTask(this).execute()
        } else {
            card.visibility = View.GONE
        }
    }

    override fun onInfoResolved(info: String) {
        card.visibility = if (info.isNotBlank() && info != "-") View.VISIBLE else View.GONE
        cardText?.text = info
    }
}
