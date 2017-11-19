package de.jonashaeusler.vertretungsplan.adapter

import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.models.Event
import kotlinx.android.synthetic.main.item_substitute.view.*
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(val events: MutableList<Event>) :
        RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    var itemClickListener: ((Event) -> Unit)? = null

    private var dayDateFormat = SimpleDateFormat("d", Locale.getDefault())
    private var monthDateFormat = SimpleDateFormat("MMM", Locale.getDefault())
    private val currentMonth = monthDateFormat.format(Calendar.getInstance().timeInMillis)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(parent.context, R.layout.item_substitute, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]

        val month = monthDateFormat.format(event.getDateInMs())
        val day = dayDateFormat.format(event.getDateInMs()).toInt()
        val lastDay = if (position == 0) -1 else dayDateFormat.format(events[position - 1].getDateInMs()).toInt()

        holder.title.text = event.title
        holder.text.text = event.text
        holder.day.text = day.toString()
        holder.month.text = month
        holder.itemView.setOnClickListener { itemClickListener?.invoke(event) }

        holder.type.setImageResource(when (event.type) {
            Event.EventType.TYPE_HOMEWORK -> R.drawable.ic_assignment_black
            Event.EventType.TYPE_CLASS_TEST -> R.drawable.ic_class_black
            Event.EventType.TYPE_SUBSTITUTE -> R.drawable.ic_swap_horiz_black
        })

        // show day only on first occurrent, hide the center stepper when the day is visible
        holder.day.visibility = if (lastDay == day) View.INVISIBLE else View.VISIBLE
        holder.stepLineCenter.visibility = if (lastDay == day) View.VISIBLE else View.INVISIBLE
        // show month if its not current month and if the day is visible
        holder.month.visibility = if (day != lastDay && month != currentMonth) View.VISIBLE else View.INVISIBLE
        // when the month is visible or we are at the last item, hide the bottom stepper
        holder.stepLineBottom.visibility = if (day != lastDay && month != currentMonth || position == events.size - 1) View.INVISIBLE else View.VISIBLE
        // hide first step line
        holder.stepLineTop.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        // highlight first occurrent of the current day
        holder.dayHighlight.visibility = if (DateUtils.isToday(event.getDateInMs()) && day != lastDay) View.VISIBLE else View.INVISIBLE
    }

    override fun getItemCount(): Int = events.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = itemView.title
        val text: TextView = itemView.text
        val day: TextView = itemView.day
        val type: ImageView = itemView.type
        val month: TextView = itemView.month
        val stepLineTop: View = itemView.stepLineTop
        val stepLineCenter: View = itemView.stepLineCenter
        val stepLineBottom: View = itemView.stepLineBottom
        val dayHighlight: View = itemView.dayHighlight
    }

    fun add(event: Event) {
        events.add(event)
        events.sortBy { it.getDateInMs() }
        val index = events.indexOf(event)
        notifyItemInserted(index)

        if (index > 0) {
            notifyItemChanged(index - 1)
        }
    }

    fun addAll(events: List<Event>) {
        this.events.addAll(events)
        this.events.sortBy { it.getDateInMs() }
        notifyDataSetChanged()
    }
}