package de.jonashaeusler.vertretungsplan.ui.main

import android.os.AsyncTask
import android.view.View
import androidx.appcompat.app.AlertDialog
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.data.entities.Event
import de.jonashaeusler.vertretungsplan.data.local.getPassword
import de.jonashaeusler.vertretungsplan.data.local.getUsername
import de.jonashaeusler.vertretungsplan.data.local.isClassSchoolApiEligible
import de.jonashaeusler.vertretungsplan.data.network.OnEventsFetched
import de.jonashaeusler.vertretungsplan.data.network.api.InfoTask
import de.jonashaeusler.vertretungsplan.data.network.api.OnInfoResolved
import de.jonashaeusler.vertretungsplan.data.network.dsb.SubstitutionTask
import de.jonashaeusler.vertretungsplan.util.isEllipsized
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

    override fun onEventFetchError(message: String) = showErrorView(message)

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
