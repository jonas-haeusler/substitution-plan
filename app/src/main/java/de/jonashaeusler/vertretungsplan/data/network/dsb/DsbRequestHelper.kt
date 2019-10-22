package de.jonashaeusler.vertretungsplan.data.network.dsb

import android.util.Base64
import android.util.Base64InputStream
import android.util.Base64OutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/* Today's date with the time set to 0, formatted in a way to go along DSB's date format */
private val today = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssSSSSS", Locale.US).format(
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
)

/* The dsb request payload template */
private val requestPayload =
        """
                {
                    "req": {
                        "Data": '%s',
                        "DataType": 1
                    }
                }
        """.trimIndent()

/* The data string that has to be substituted in [requestPayload] */
private val requestPayloadDataString = """
    {
        "AppId": "${UUID.randomUUID()}",
        "UserId": "%s",
        "UserPw": "%s",
        "BundleId": "de.heinekingmedia.dsbmobile",
        "Device": "Pixel",
        "OsVersion": "28 9",
        "Language": "en",
        "Date": "$today",
        "LastUpdate": "$today",
        "AppVersion": "2.5.9"
    }
    """.trimIndent()

/**
 * Creates the correctly formatted and encrypted payload for a request towards the dsb endpoint.
 */
fun createRequestPayload(username: String, password: String) =
        requestPayload.format(compress(requestPayloadDataString.format(username, password)))

/**
 * Compress [data] using gzip+base64
 */
fun compress(data: String): String {
    ByteArrayOutputStream(data.length).use { byteArrayOutputStream ->
        Base64OutputStream(byteArrayOutputStream, Base64.NO_WRAP).use { base64OutputStream ->
            GZIPOutputStream(base64OutputStream).use { gzipOutputStream ->
                gzipOutputStream.write(data.toByteArray())
            }
        }
        return byteArrayOutputStream.toString()
    }
}

/**
 * Decompress [data] using base64+gzip
 */
fun decompress(data: String): String {
    ByteArrayInputStream(data.toByteArray()).use { byteArrayInputStream ->
        Base64InputStream(byteArrayInputStream, Base64.NO_WRAP).use { base64InputStream ->
            GZIPInputStream(base64InputStream).use { gzipInputStream ->
                return gzipInputStream.readBytes().toString(Charsets.UTF_8)
            }
        }
    }
}
