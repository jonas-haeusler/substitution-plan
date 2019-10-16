package de.jonashaeusler.vertretungsplan.data.notifications

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.text.format.DateUtils

private const val JOB_ID = 1

class SubstitutionUpdater(context: Context) {

    private val jobScheduler by lazy {
        context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    }

    init {
        if (!isRunning()) {
            val jobInfo = JobInfo.Builder(
                    JOB_ID,
                    ComponentName(context, SubstitutionUpdateService::class.java)
            ).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // require any type of network
                    .setPeriodic(10 * DateUtils.MINUTE_IN_MILLIS) // repeat every 10min
                    .setPersisted(true)
                    .build()

            jobScheduler.schedule(jobInfo)
        }
    }

    /**
     * Whether the job is currently pending to be executed
     */
    fun isRunning() = jobScheduler.allPendingJobs.any { it.id == JOB_ID }
}
