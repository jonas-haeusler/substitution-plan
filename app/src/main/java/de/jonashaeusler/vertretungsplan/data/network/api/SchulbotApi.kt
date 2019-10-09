package de.jonashaeusler.vertretungsplan.data.network.api

import android.content.Context
import de.jonashaeusler.vertretungsplan.data.local.getApiPassword
import de.jonashaeusler.vertretungsplan.data.local.getApiUsername
import de.jonashaeusler.vertretungsplan.data.network.VERTRETUNGSBOT_BASE_URL
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SchulbotApi {

    @GET("api/ha.php")
    fun getPlaintextHomework(): Call<String>

    @GET("api/ka.php")
    fun getPlaintextExams(): Call<String>

    @GET("api/infos.php")
    fun getPlaintextInfo(): Call<String>

    @POST("api/edit.php")
    fun edit(
            @Header("type") type: String,
            @Body replace: String
    ): Call<String>

    companion object {
        fun create(context: Context): SchulbotApi {

            val retrofit: Retrofit = Retrofit.Builder()
                    .client(httpClient(context))
                    .baseUrl(VERTRETUNGSBOT_BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()


            return retrofit.create(SchulbotApi::class.java)
        }

        private fun httpClient(context: Context): OkHttpClient {
            return OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request()

                        val newRequest = request.newBuilder()
                                .method(request.method(), request.body())
                                .headers(Headers.Builder()
                                        .addAll(request.headers())
                                        .addUnsafeNonAscii("user", context.getApiUsername())
                                        .addUnsafeNonAscii("password", context.getApiPassword())
                                        .build()
                                )
                                .build()

                        chain.proceed(newRequest)
                    }
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
        }
    }
}
