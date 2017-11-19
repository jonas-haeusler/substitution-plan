package de.jonashaeusler.vertretungsplan.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.models.Event
import kotlinx.android.synthetic.main.item_event.view.*
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(val events: MutableList<Event>) :
        RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    var itemClickListener: ((Event) -> Unit)? = null

    private var dateDateFormat = SimpleDateFormat("dd. MMMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(parent.context, R.layout.item_event, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]

        holder.title.text = event.title
        holder.text.text = event.text
        holder.date.text = dateDateFormat.format(event.getDateInMs())
        holder.itemView.setOnClickListener { itemClickListener?.invoke(event) }

        holder.text.visibility = if (event.text.isBlank()) View.GONE else View.VISIBLE
    }

    override fun getItemCount(): Int = events.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = itemView.title
        val text: TextView = itemView.text
        val date: TextView = itemView.date
    }

    fun addAll(events: List<Event>) {
        this.events.addAll(events)
        this.events.sortBy { it.getDateInMs() }
        notifyDataSetChanged()
    }
}