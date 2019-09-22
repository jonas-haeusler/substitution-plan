package de.jonashaeusler.vertretungsplan.ui.main

import de.jonashaeusler.vertretungsplan.data.entities.Event
import de.jonashaeusler.vertretungsplan.data.network.api.CafeteriaService
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class CafeteriaFragment : EventFragment() {
    override val useInfoCard = false

    override fun loadEvents() {
        CafeteriaService.create().fetch().enqueue(object : Callback<Document> {
            override fun onResponse(call: Call<Document>, response: Response<Document>) {
                response.body()?.let {
                    try {
                        println(parseMenus(it))
                        postEvents(parseMenus(it))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showErrorView()
                    }
                }
            }

            override fun onFailure(call: Call<Document>, t: Throwable) {
                t.printStackTrace()
                showErrorView()
            }

        })
    }

    private fun parseMenus(document: Document): List<Event> {
        val menus = mutableListOf<Event>()

        for (menu in document.select("div.tagesmenu div.col-md-4")) {
            val menuDate = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val menuTitle = menu.select("h5").text()
            val matcher = Pattern.compile("\\d{2}.\\d{2}.\\d{4}").matcher(menuTitle)
            matcher.find()
            menuDate.time = simpleDateFormat.parse(matcher.group())

            val tableRows = menu.select("table")[0].select("tr")
            for (row in tableRows) {
                menus.add(Event(simpleDateFormat.format(menuDate.timeInMillis),
                        row.getElementsByClass("menu").text().replace("\\s+".toRegex(), " "),
                        "", Event.EventType.TYPE_CAFETERIA_MENU))
                menuDate.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        return menus
    }
}
