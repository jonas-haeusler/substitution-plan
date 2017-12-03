package de.jonashaeusler.vertretungsplan.network

import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import de.jonashaeusler.vertretungsplan.interfaces.OnInfoResolved

class InfoTask(private val callback: OnInfoResolved? = null) : AsyncTask<String, Long, String>() {
    override fun doInBackground(vararg p0: String?): String {
        return try {
            HttpRequest.get("https://schulbot.000webhostapp.com/public/info.php").body()
        } catch (e: HttpRequest.HttpRequestException) {
            e.printStackTrace()
            ""
        }
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        callback?.onInfoResolved(result)
    }
}