package de.jonashaeusler.vertretungsplan.data.network.dsb

import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import de.jonashaeusler.vertretungsplan.data.network.DSB_BASE_URL
import org.json.JSONException
import org.json.JSONObject

/**
 * Class to check whether the login credentials are correct.
 */
class LoginTask(private val callback: OnLogin? = null) :
        AsyncTask<String, Long, Boolean>() {

    override fun doInBackground(vararg args: String): Boolean {
        return try {
            val response = HttpRequest
                    .post(DSB_BASE_URL)
                    .contentType(HttpRequest.CONTENT_TYPE_JSON, HttpRequest.CHARSET_UTF8)
                    .send(createRequestPayload(args[0], args[1]))
                    .body()

            val encryptedResponse = JSONObject(response).getString("d")
            val decryptedResponse = decompress(encryptedResponse)

            JSONObject(decryptedResponse).getInt("Resultcode") == 0
        } catch (e: HttpRequest.HttpRequestException) {
            e.printStackTrace()
            false
        } catch (e: JSONException) {
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
