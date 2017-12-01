package de.jonashaeusler.vertretungsplan.network

import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import de.jonashaeusler.vertretungsplan.interfaces.OnServerStatusResolved

class ServerStatusTask(private val callback: OnServerStatusResolved? = null) : AsyncTask<String, Long, String>() {

    override fun doInBackground(vararg args: String?): String {
        return try {
            HttpRequest.get("https://schulbot.000webhostapp.com/public/status.php").body()
        } catch (e: HttpRequest.HttpRequestException) {
            e.printStackTrace()
            ""
        }
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        val serverStatus = result.split("--..--")
        if (serverStatus.size >= 2) {
            callback?.onServerStatusResolved(serverStatus)
        }
    }
}