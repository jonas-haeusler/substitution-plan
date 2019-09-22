package de.jonashaeusler.vertretungsplan.ui.courses

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.data.local.getIgnoredCourses
import de.jonashaeusler.vertretungsplan.data.local.setIgnoredCourses
import de.jonashaeusler.vertretungsplan.data.entities.Course
import kotlinx.android.synthetic.main.activity_course_manager.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class CourseManagerActivity : AppCompatActivity() {
    private lateinit var courses: List<Course>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_manager)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val ignoredCourses = getIgnoredCourses()
        val coursesRegex = resources.getStringArray(R.array.courses_values)
        courses = resources.getStringArray(R.array.courses).mapIndexed { index, course ->
            Course(course, !ignoredCourses.contains(coursesRegex[index]), coursesRegex[index])
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        val adapter = CourseAdapter(courses)

        recyclerView.adapter = adapter

        adapter.checkedChangedListener = { index, value ->
            courses[index].enabled = value
        }
    }

    override fun onPause() {
        super.onPause()
        saveIgnoredCourses()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveIgnoredCourses() {
        setIgnoredCourses(courses.filter { !it.enabled }.map { it.regex })
    }
}
