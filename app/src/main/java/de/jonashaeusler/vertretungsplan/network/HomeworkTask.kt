package de.jonashaeusler.vertretungsplan.network

import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.interfaces.OnEventsFetched
import de.jonashaeusler.vertretungsplan.models.Event

/**
 * AsyncTask to get events from the "schulbot"-API.
 */
class HomeworkTask(private val callback: OnEventsFetched? = null) :
        AsyncTask<String, Long, Boolean>() {

    private val events = mutableListOf<Event>()
    override fun doInBackground(vararg p0: String?): Boolean {
        return try {
            events.addAll((
                    HttpRequest.get("http://schulbot.000webhostapp.com/public/ha.php").body())
                    .replace("\n", ", ")
                    .split("--..--..--")
                    .map { it.split("--..--") }
                    .map {
                        if (it.size < 3) {
                            Event(date = it[0], title = it[1], text = "",
                                    type = Event.EventType.TYPE_HOMEWORK)
                        } else {
                            Event(date = it[0], title = it[1], text = it[2],
                                    type = Event.EventType.TYPE_HOMEWORK)
                        }
                    })

            true
        } catch (e: HttpRequest.HttpRequestException) {
            e.printStackTrace()
            false
        }
        // TODO: What about exception thrown by parsing errors?
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        if (result) {
            callback?.onEventFetchSuccess(events)
        } else {
            callback?.onEventFetchError()
        }
    }
}
