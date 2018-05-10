package de.jonashaeusler.vertretungsplan.adapter

import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.helpers.reverseStrikeThroughAnimation
import de.jonashaeusler.vertretungsplan.helpers.startStrikeThroughAnimation
import de.jonashaeusler.vertretungsplan.models.Event
import kotlinx.android.synthetic.main.item_event.view.*


class EventAdapter(val events: MutableList<Event>) :
        RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    var itemClickListener: ((event: Event) -> Unit)? = null
    var checkedChangedListener: ((event: Event, value: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(parent.context, R.layout.item_event, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]

        holder.title.text = event.title
        holder.text.text = event.text
        if (event.getDateInMs() == -1L) {
            holder.date.setText(R.string.error_no_date_provided)
        } else {
            holder.date.text = DateUtils.getRelativeTimeSpanString(
                    event.getDateInMs(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS)
        }
        holder.itemView.setOnClickListener { itemClickListener?.invoke(event) }
        holder.completed.setOnCheckedChangeListener { _: CompoundButton, value: Boolean ->
            checkedChangedListener?.invoke(event, value)
            if (value) {
                holder.title.startStrikeThroughAnimation()
                holder.text.startStrikeThroughAnimation()
                holder.date.startStrikeThroughAnimation()
            } else {
                holder.title.reverseStrikeThroughAnimation()
                holder.text.reverseStrikeThroughAnimation()
                holder.date.reverseStrikeThroughAnimation()
            }
        }

        holder.completed.isChecked = event.completed
        if (event.completed) {
            holder.title.startStrikeThroughAnimation()
            holder.text.startStrikeThroughAnimation()
            holder.date.startStrikeThroughAnimation()
        }

        holder.text.visibility =
                if (event.text.isBlank()) View.GONE else View.VISIBLE
        holder.completed.visibility =
                if (event.type == Event.EventType.TYPE_HOMEWORK) View.VISIBLE else View.GONE

    }

    override fun getItemCount(): Int = events.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = itemView.title
        val text: TextView = itemView.text
        val date: TextView = itemView.date
        val completed: CheckBox = itemView.completed
    }

    fun addAll(events: List<Event>) {
        this.events.addAll(events)
        this.events.sortBy { it.getDateInMs() }
        notifyDataSetChanged()
    }
}