package de.jonashaeusler.vertretungsplan.data.network.api

import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import de.jonashaeusler.vertretungsplan.data.entities.Event
import de.jonashaeusler.vertretungsplan.data.network.OnEventsFetched
import de.jonashaeusler.vertretungsplan.data.network.Result
import de.jonashaeusler.vertretungsplan.data.network.VERTRETUNGSBOT_BASE_URL

/**
 * AsyncTask to get events from the "schulbot"-API.
 */
class HomeworkTask(private val callback: OnEventsFetched? = null) :
        AsyncTask<String, Long, Result>() {

    private val events = mutableListOf<Event>()
    override fun doInBackground(vararg p0: String?): Result {
        return try {
            events.addAll((
                    HttpRequest.get("$VERTRETUNGSBOT_BASE_URL/api/ha.php").body())
                    .split("--..--..--")
                    .map { it.split("--..--") }
                    .filter { it.size >= 2 }
                    .map {
                        Event(date = it[0], title = it[1], text = if (it.size > 2) it[2] else "",
                                type = Event.EventType.TYPE_HOMEWORK)
                    })

            Result.Success
        } catch (e: HttpRequest.HttpRequestException) {
            e.printStackTrace()
            Result.Failure(e.localizedMessage)
        }
        // TODO: What about exception thrown by parsing errors?
    }

    override fun onPostExecute(result: Result) {
        super.onPostExecute(result)

        when (result) {
            is Result.Success -> callback?.onEventFetchSuccess(events)
            is Result.Failure -> callback?.onEventFetchError(result.message)
        }
    }
}
