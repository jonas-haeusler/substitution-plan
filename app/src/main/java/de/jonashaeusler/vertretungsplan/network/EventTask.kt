package de.jonashaeusler.vertretungsplan.network

import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.models.Event

/**
 * AsyncTask to get events from the "schulbot"-API.
 */
private const val RESULT_SUCCESS: String = "RESULT_SUCCESS"
private const val RESULT_NETWORK_ERROR: String = "RESULT_NETWORK_ERROR"
private const val RESULT_PARSE_ERROR: String = "RESULT_PARSE_ERROR"

class EventTask(private val callback: OnEventsFetched? = null) :
        AsyncTask<String, Long, String>() {

    private val homeworkUrl = "http://schulbot.000webhostapp.com/public/ha.php"
    private val classTestUrl = "http://schulbot.000webhostapp.com/public/ka.php"

    private val events = mutableListOf<Event>()

    override fun doInBackground(vararg p0: String?): String {
        return try {
            events.addAll((
                    HttpRequest.get(homeworkUrl).body())
                    .replace("\n", ", ")
                    .split("--..--..--")
                    .map { it.split("--..--") }
                    .filter { it.size == 3 }
                    .map {
                        Event(date = it[0], title = it[1], text = it[2],
                                type = Event.EventType.TYPE_HOMEWORK)
                    })

            events.addAll((
                    HttpRequest.get(classTestUrl).body())
                    .replace("\n", ", ")
                    .split("--..--..--")
                    .map { it.split("--..--") }
                    .filter { it.size == 3 }
                    .map {
                        Event(date = it[0], title = it[1], text = it[2],
                                type = Event.EventType.TYPE_CLASS_TEST)
                    })

            RESULT_SUCCESS
        } catch (e: HttpRequest.HttpRequestException) {
            e.printStackTrace()
            RESULT_NETWORK_ERROR
        } catch (e: Exception) {
            e.printStackTrace()
            RESULT_NETWORK_ERROR
        }
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        when (result) {
            RESULT_SUCCESS -> callback?.onEventsFetched(events)
            RESULT_NETWORK_ERROR -> callback?.onEventNetworkError()
            RESULT_PARSE_ERROR -> callback?.onEventParseError()
        }
    }

    interface OnEventsFetched {
        fun onEventsFetched(events: List<Event>)
        fun onEventParseError()
        fun onEventNetworkError()
    }
}
