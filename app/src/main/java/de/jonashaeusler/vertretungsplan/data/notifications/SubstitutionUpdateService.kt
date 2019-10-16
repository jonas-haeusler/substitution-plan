package de.jonashaeusler.vertretungsplan.data.notifications

import android.app.job.JobParameters
import android.app.job.JobService
import android.text.format.DateUtils
import de.jonashaeusler.vertretungsplan.data.entities.Event
import de.jonashaeusler.vertretungsplan.data.local.SubstitutionCache
import de.jonashaeusler.vertretungsplan.data.local.getIgnoredCoursesAsRegex
import de.jonashaeusler.vertretungsplan.data.local.getPassword
import de.jonashaeusler.vertretungsplan.data.local.getUsername
import de.jonashaeusler.vertretungsplan.data.network.OnEventsFetched
import de.jonashaeusler.vertretungsplan.data.network.dsb.SubstitutionTask
import java.lang.ref.WeakReference

class SubstitutionUpdateService : JobService() {

    private var substitutionTask: SubstitutionTask? = null

    override fun onStartJob(params: JobParameters): Boolean {
        substitutionTask = SubstitutionTask(WeakReference(this), object : OnEventsFetched {
            override fun onEventFetchSuccess(events: List<Event>) {
                val filteredEvents = filterSubstitutions(events)

                val substitutionCache = SubstitutionCache(this@SubstitutionUpdateService)
                val diff = substitutionCache.getNewSubstitutes(filteredEvents)
                substitutionCache.updateSubstitutes(filteredEvents)

                if (diff.isNotEmpty()) {
                    SubstitutionNotification(this@SubstitutionUpdateService)
                            .setEvents(diff)
                            .show()
                }

                jobFinished(params, false)
            }

            override fun onEventFetchError(message: String) {
                jobFinished(params, true)
            }

        })

        substitutionTask?.execute(getUsername(), getPassword())

        return true
    }

    private fun filterSubstitutions(substitutions: List<Event>): List<Event> {
        return substitutions.filter {
            it.getDateInMs() + DateUtils.DAY_IN_MILLIS > System.currentTimeMillis()
        }.filterNot {
            it.text.contains(getIgnoredCoursesAsRegex())
                    && getIgnoredCoursesAsRegex().toString().isNotEmpty()
        }
    }


    override fun onStopJob(params: JobParameters): Boolean {
        substitutionTask?.cancel(true)
        return true
    }
}
