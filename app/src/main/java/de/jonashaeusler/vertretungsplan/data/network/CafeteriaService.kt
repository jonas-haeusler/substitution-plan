package de.jonashaeusler.vertretungsplan.data.network

import com.github.slashrootv200.retrofithtmlconverter.HtmlConverterFactory
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET

interface CafeteriaService {
    @GET("/api/cafeteria.html")
    fun fetch(): Call<Document>

    companion object {

        fun create(): CafeteriaService {
            val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(VERTRETUNGSBOT_BASE_URL)
                    .addConverterFactory(HtmlConverterFactory.create(VERTRETUNGSBOT_BASE_URL))
                    .build()

            return retrofit.create(CafeteriaService::class.java)
        }
    }
}
