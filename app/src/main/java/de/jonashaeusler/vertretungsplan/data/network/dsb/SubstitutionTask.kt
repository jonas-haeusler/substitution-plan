package de.jonashaeusler.vertretungsplan.data.network.dsb

import android.content.Context
import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import de.jonashaeusler.vertretungsplan.data.entities.Event
import de.jonashaeusler.vertretungsplan.data.entities.Timetables
import de.jonashaeusler.vertretungsplan.data.local.getClassShortcut
import de.jonashaeusler.vertretungsplan.data.network.DSB_BASE_URL
import de.jonashaeusler.vertretungsplan.data.network.OnEventsFetched
import de.jonashaeusler.vertretungsplan.data.network.Result
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.lang.ref.WeakReference

/**
 * This class retrieves all available events which match the class filter,
 * parses them to [Event] objects and returns them as a list sorted by [Event.date].
 *
 * Pass the user credentials to [doInBackground].
 */
class SubstitutionTask(
        private val context: WeakReference<Context>,
        private val callback: OnEventsFetched? = null
) : AsyncTask<String, Long, Result>() {

    private val substitutes = mutableListOf<Event>()

    override fun doInBackground(vararg args: String): Result {
        try {
            val response = HttpRequest
                    .post(DSB_BASE_URL)
                    .contentType(HttpRequest.CONTENT_TYPE_JSON, HttpRequest.CHARSET_UTF8)
                    .send(createRequestPayload(args[0], args[1]))
                    .body()

            // The dsb request contains a single json formatted key/value pair where 'd' is always
            // the key for the data
            val compressedResponse = JSONObject(response).getString("d")

            // The response is gzip+base64 compressed
            val decompressedResponse = decompress(compressedResponse)

            val data = JSONObject(decompressedResponse)

            if (data["Resultcode"] == 1) {
                // Login credentials incorrect
                return Result.Failure(data.getString("ResultStatusInfo"))
            }

            val jsonArray = getTimetable(data) ?: JSONArray()

            val timetableList = (0 until jsonArray.length())
                    .map { jsonArray.getJSONObject(it) }
                    .map {
                        val jsonTimetables = it.getJSONArray("Childs")
                        val timetables = (0 until jsonTimetables.length())
                                .map { i -> jsonTimetables.getJSONObject(i) }
                                .map { table ->
                                    Timetables.Timetable(
                                            id = table.getString("Id"),
                                            date = table.getString("Date"),
                                            title = table.getString("Title"),
                                            url = table.getString("Detail"),
                                            previewUrl = table.getString("Preview")
                                    )
                                }

                        Timetables(
                                id = it.getString("Id"),
                                date = it.getString("Date"),
                                title = it.getString("Title"),
                                timetables = timetables
                        )
                    }

            // Let's iterate over all the retrieved timetables, extract the necessary info
            // from the urls and parse them into our event module class
            for (timetable in timetableList[0].timetables) {
                val document = Jsoup.parse(HttpRequest.get(timetable.url).body("iso-8859-1"))
                for (substitutePlan in document.getElementsByTag("center")) {
                    val date = substitutePlan.getElementsByTag("div").text()
                    context.get()?.let { context ->
                        substitutePlan.select("tr")
                                .asSequence()
                                .map { it.select("td") }
                                .filterNot { it.isEmpty() }
                                .filterNot { it.size < 8 }
                                .filter { it[0].text().contains(context.getClassShortcut(), true) }
                                .mapTo(substitutes) {
                                    Event(date = date,
                                            title = String.format(context.getString(de.jonashaeusler.vertretungsplan.R.string.substitution_title),
                                                    it[2].text(), it[1].text()),
                                            text = String.format(context.getString(de.jonashaeusler.vertretungsplan.R.string.substitution_text),
                                                    it[4].text(), it[6].text(), it[3].text(), it[5].text()),
                                            type = Event.EventType.TYPE_SUBSTITUTE)
                                }
                    }
                }
            }

            return Result.Success
        } catch (e: HttpRequest.HttpRequestException) {
            e.printStackTrace()
            return Result.Failure(e.localizedMessage)
        } catch (e: JSONException) {
            e.printStackTrace()
            return Result.Failure(e.localizedMessage)
        }
    }

    override fun onPostExecute(result: Result?) {
        when (result) {
            is Result.Success -> {
                substitutes.sortBy { it.date }
                callback?.onEventFetchSuccess(substitutes)
            }
            is Result.Failure -> {
                callback?.onEventFetchError(result.message)
            }
        }
    }

    /**
     * Finds the first key/value pair which indicates a timetable (MethodName=timetable) and
     * returns an json-array containing a list of all available timetable or null, when no
     * timetables where found.
     */
    private fun getTimetable(jsonObject: JSONObject): JSONArray? {
        val keys = jsonObject.keys()

        for (key in keys) {
            if (key == "MethodName" && jsonObject[key] == "timetable") {
                return jsonObject.getJSONObject("Root").getJSONArray("Childs")
            } else if (jsonObject[key] is JSONObject) {
                val timetables = getTimetable(jsonObject[key] as JSONObject)
                if (timetables != null) return timetables
            } else if (jsonObject[key] is JSONArray) {
                val jsonArray = jsonObject.getJSONArray(key)
                for (i in 0 until jsonArray.length()) {
                    val timetables = getTimetable(jsonArray[i] as JSONObject)
                    if (timetables != null) return timetables
                }
            }
        }
        return null
    }
}
