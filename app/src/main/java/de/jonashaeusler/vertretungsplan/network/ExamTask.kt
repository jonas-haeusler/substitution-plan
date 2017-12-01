package de.jonashaeusler.vertretungsplan.network

import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import de.jonashaeusler.vertretungsplan.interfaces.OnEventsFetched
import de.jonashaeusler.vertretungsplan.models.Event

/**
 * AsyncTask to get events from the "schulbot"-API.
 */
class ExamTask(private val callback: OnEventsFetched? = null) :
        AsyncTask<String, Long, Boolean>() {

    private val events = mutableListOf<Event>()

    override fun doInBackground(vararg p0: String?): Boolean {
        return try {
            events.addAll((
                    HttpRequest.get("https://schulbot.000webhostapp.com/public/ka.php").body())
                    .replace("\n", ", ")
                    .split("--..--..--")
                    .map { it.split("--..--") }
                    .filter { it.size >= 2 }
                    .map {
                        Event(date = it[0], title = it[1], text = if (it.size > 2) it[2] else "",
                                type = Event.EventType.TYPE_EXAM)
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
