package de.jonashaeusler.vertretungsplan.network

import android.content.Context
import android.os.AsyncTask
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.helpers.getClassShortcut
import de.jonashaeusler.vertretungsplan.helpers.logout
import de.jonashaeusler.vertretungsplan.interfaces.OnEventsFetched
import de.jonashaeusler.vertretungsplan.models.Event
import de.jonashaeusler.vertretungsplan.models.Timetable
import org.json.JSONArray
import org.jsoup.Jsoup
import java.lang.ref.WeakReference

/**
 * This class retrieves all available events which match the class filter,
 * parses them to [Event] objects and returns them as a list sorted by [Event.date].
 *
 * Pass the user credentials to [doInBackground].
 */
class SubstitutionTask(private val context: WeakReference<Context>, private val callback: OnEventsFetched? = null) :
        AsyncTask<String, Long, Boolean>() {

    private val substitutes = mutableListOf<Event>()

    override fun doInBackground(vararg args: String): Boolean {
        try {
            // TODO: Can the authId be saved for further use?
            val authIdRequest = HttpRequest
                    .get("https://iphone.dsbcontrol.de/iPhoneService.svc/DSB/authid/${args[0]}/${args[1]}")
                    .body()
                    .removeSurrounding("\"")

            // This is the authId for a failed login.
            // Maybe the account got deactivated? Who knows.
            // We'll just log the user out and call it a day.
            if (authIdRequest == "00000000-0000-0000-0000-000000000000") {
                context.get()?.logout() // TODO: We should probably show the login activity.
                return false
            }

            // This request will contain a json file holding one or more "timetables"
            val timetableRequest = HttpRequest
                    .get("https://iphone.dsbcontrol.de/iPhoneService.svc/DSB/timetables/$authIdRequest")
                    .body()

            // We'll convert that json file into our timetable model class
            val jsonArray = JSONArray(timetableRequest)
            val timetableList = (0 until jsonArray.length())
                    .map { jsonArray.getJSONObject(it) }
                    .map {
                        Timetable(
                                it.getBoolean("ishtml"),
                                it.getString("timetabledate"),
                                it.getString("timetablegroupname"),
                                it.getString("timetabletitle"),
                                it.getString("timetableurl"))
                    }

            // Let's iterate over all the retrieved timetables, extract the necessary info
            // from the urls and parse them into our event module class
            for (timetable in timetableList) {
                val document = Jsoup.parse(HttpRequest.get(timetable.timetableUrl).body())
                for (substitutePlan in document.getElementsByTag("center")) {
                    val date = substitutePlan.getElementsByTag("div").text()
                    context.get()?.let { context ->
                        substitutePlan.select("tr")
                                .map { it.select("td") }
                                .filterNot { it.isEmpty() }
                                .filterNot { it.size < 8 }
                                .filter { it[0].text().contains(context.getClassShortcut(), true) }
                                .mapTo(substitutes) {
                                    Event(date = date,
                                            title = String.format(context.getString(R.string.substitution_title),
                                                    it[2].text(), it[1].text()),
                                            text = String.format(context.getString(R.string.substitution_text),
                                                    it[4].text(), it[6].text(), it[3].text(), it[5].text()),
                                            type = Event.EventType.TYPE_SUBSTITUTE)
                                }
                    }
                }
            }

            return true
        } catch (e: HttpRequest.HttpRequestException) {
            e.printStackTrace()
            return false
        }
    }

    override fun onPostExecute(result: Boolean) {
        if (result) {
            substitutes.sortBy { it.date }
            callback?.onEventFetchSuccess(substitutes)
        } else {
            callback?.onEventFetchError()
        }
    }
}