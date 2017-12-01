package de.jonashaeusler.vertretungsplan.network

import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest

/**
 * Class to check whether the login credentials are correct.
 */
class LoginTask(private val callback: OnLogin? = null) :
        AsyncTask<String, Long, Boolean>() {
    override fun doInBackground(vararg args: String?): Boolean {
        return try {
            val authId = HttpRequest
                    .get("https://iphone.dsbcontrol.de/iPhoneService.svc/DSB/authid/${args[0]}/${args[1]}")
                    .body()
                    .removeSurrounding("\"")

            //TODO: Can we save the autoId for further usage?

            authId != "00000000-0000-0000-0000-000000000000"
        } catch (e: HttpRequest.HttpRequestException) {
            e.printStackTrace()
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        if (result) {
            callback?.onLoginSucceeded()
        } else {
            callback?.onLoginFailed()
        }
    }

    interface OnLogin {
        fun onLoginSucceeded()
        fun onLoginFailed()
    }
}