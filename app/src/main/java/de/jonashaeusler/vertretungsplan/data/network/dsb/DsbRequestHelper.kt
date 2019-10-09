package de.jonashaeusler.vertretungsplan.data.network.dsb

import android.util.Base64
import android.util.Base64InputStream
import android.util.Base64OutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

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
private const val requestPayloadDataString = """{"UserId": "%s", "UserPw": "%s", "BundleId": ""}"""

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
