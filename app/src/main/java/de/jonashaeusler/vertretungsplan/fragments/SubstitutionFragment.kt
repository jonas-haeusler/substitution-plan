package de.jonashaeusler.vertretungsplan.fragments

import android.os.AsyncTask
import android.view.View
import de.jonashaeusler.vertretungsplan.helpers.isClassSchoolApiEligible
import de.jonashaeusler.vertretungsplan.interfaces.OnInfoResolved
import de.jonashaeusler.vertretungsplan.network.InfoTask
import de.jonashaeusler.vertretungsplan.network.SubstitutionTask
import kotlinx.android.synthetic.main.fragment_events.*
import java.lang.ref.WeakReference

class SubstitutionFragment : EventFragment(), OnInfoResolved {
    override var eventTask: AsyncTask<String, Long, Boolean>? = null
        get() {
            return SubstitutionTask(WeakReference(requireContext()), this)
        }

    override fun onReload() {
        if (requireContext().isClassSchoolApiEligible()) {
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
