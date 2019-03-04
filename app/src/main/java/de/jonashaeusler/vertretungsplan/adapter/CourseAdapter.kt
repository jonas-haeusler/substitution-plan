package de.jonashaeusler.vertretungsplan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.models.Course
import kotlinx.android.synthetic.main.item_course.view.*


class CourseAdapter(private val courseList: List<Course>) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    var checkedChangedListener: ((index: Int, value: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseAdapter.ViewHolder, position: Int) {
        holder.course.text = courseList[position].course
        holder.course.isChecked = courseList[holder.adapterPosition].enabled

        holder.course.setOnCheckedChangeListener { _, newValue ->
            checkedChangedListener?.invoke(holder.adapterPosition, newValue)
        }
    }

    override fun getItemCount() = courseList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val course: Switch = view.courseEnabled
    }

}
