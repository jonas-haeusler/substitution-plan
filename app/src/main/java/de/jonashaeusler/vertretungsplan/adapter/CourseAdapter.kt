package de.jonashaeusler.vertretungsplan.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.models.Course
import kotlinx.android.synthetic.main.item_course.view.*


class CourseAdapter(private val courseList: List<Course>) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    var checkedChangedListener: ((index: Int, value: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseAdapter.ViewHolder {
        val view = View.inflate(parent.context, R.layout.item_course, null)
        return CourseAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseAdapter.ViewHolder, position: Int) {
        holder.title.text = courseList[position].course
        holder.courseEnabled.isChecked = courseList[holder.adapterPosition].enabled

        holder.courseEnabled.setOnCheckedChangeListener { _, newValue ->
            checkedChangedListener?.invoke(holder.adapterPosition, newValue)
        }
    }

    override fun getItemCount() = courseList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.courseTitle
        val courseEnabled: Switch = view.courseEnabled
    }

}
