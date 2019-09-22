package de.jonashaeusler.vertretungsplan.data.network.dsb

import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import de.jonashaeusler.vertretungsplan.data.network.DSB_BASE_URL
import de.jonashaeusler.vertretungsplan.data.network.DSB_INVALID_AUTH_ID

/**
 * Class to check whether the login credentials are correct.
 */
class LoginTask(private val callback: OnLogin? = null) :
        AsyncTask<String, Long, Boolean>() {
    override fun doInBackground(vararg args: String?): Boolean {
        return try {
            val authId = HttpRequest
                    .get("$DSB_BASE_URL/iPhoneService.svc/DSB/authid/${args[0]}/${args[1]}")
                    .body()
                    .removeSurrounding("\"")

            //TODO: Can we save the autoId for further usage?

            authId != DSB_INVALID_AUTH_ID
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
