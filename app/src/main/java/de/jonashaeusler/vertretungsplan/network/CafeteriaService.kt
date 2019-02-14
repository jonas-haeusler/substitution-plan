package de.jonashaeusler.vertretungsplan.network

import com.github.slashrootv200.retrofithtmlconverter.HtmlConverterFactory
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET

interface CafeteriaService {
    @GET("/index.php/de/tagesmenue")
    fun fetch(): Call<Document>

    companion object {
        private const val baseUrl = "https://www.kinzer-partyservice.de/"

        fun create(): CafeteriaService {
            val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(HtmlConverterFactory.create(baseUrl))
                    .build()

            return retrofit.create(CafeteriaService::class.java)
        }
    }
}
