package de.jonashaeusler.vertretungsplan.fragments

import android.os.AsyncTask
import android.view.View
import androidx.appcompat.app.AlertDialog
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.helpers.getPassword
import de.jonashaeusler.vertretungsplan.helpers.getUsername
import de.jonashaeusler.vertretungsplan.helpers.isClassSchoolApiEligible
import de.jonashaeusler.vertretungsplan.helpers.isEllipsized
import de.jonashaeusler.vertretungsplan.interfaces.OnEventsFetched
import de.jonashaeusler.vertretungsplan.interfaces.OnInfoResolved
import de.jonashaeusler.vertretungsplan.models.Event
import de.jonashaeusler.vertretungsplan.network.InfoTask
import de.jonashaeusler.vertretungsplan.network.SubstitutionTask
import kotlinx.android.synthetic.main.fragment_events.*
import java.lang.ref.WeakReference

class SubstitutionFragment : EventFragment(), OnInfoResolved, OnEventsFetched {
    override val useInfoCard = false
    private var substitutionTask: SubstitutionTask? = null
    private var infoTask: InfoTask? = null

    override fun loadEvents() {
        if (requireContext().isClassSchoolApiEligible()) {
            infoTask = InfoTask(this)
            infoTask?.execute()
        }

        substitutionTask = SubstitutionTask(WeakReference(requireContext()), this)
        substitutionTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                requireContext().getUsername(), requireContext().getPassword())
    }

    override fun onEventFetchSuccess(events: List<Event>) {
        postEvents(events)
    }

    override fun onEventFetchError() {
        showErrorView()
    }

    override fun onInfoResolved(info: String) {
        card.visibility = if (info.isNotBlank() && info != "-") View.VISIBLE else View.GONE
        cardText.text = info
        cardText.post { showMore.visibility = if (cardText.isEllipsized()) View.VISIBLE else View.GONE }
        showMore.setOnClickListener {
            AlertDialog.Builder(requireContext())
                    .setTitle(R.string.info)
                    .setMessage(info)
                    .setPositiveButton(R.string.okay) { _, _ -> }
                    .create()
                    .show()
        }
    }

    override fun onStop() {
        super.onStop()
        substitutionTask?.cancel(true)
        infoTask?.cancel(true)
    }
}
