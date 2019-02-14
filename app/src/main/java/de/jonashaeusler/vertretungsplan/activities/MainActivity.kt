package de.jonashaeusler.vertretungsplan.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import de.jonashaeusler.vertretungsplan.BuildConfig
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.adapter.ViewPagerAdapter
import de.jonashaeusler.vertretungsplan.fragments.EventFragment
import de.jonashaeusler.vertretungsplan.fragments.ExamFragment
import de.jonashaeusler.vertretungsplan.fragments.HomeworkFragment
import de.jonashaeusler.vertretungsplan.fragments.SubstitutionFragment
import de.jonashaeusler.vertretungsplan.helpers.*
import de.jonashaeusler.vertretungsplan.models.GitHubRelease
import kotlinx.android.synthetic.main.activty_main.*
import kotlinx.android.synthetic.main.dialog_text_input.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*

private const val REQUEST_CODE_COURSE_ACTIVITY = 7593

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ViewPagerAdapter
    private val updater = GitHubUpdater()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_main)
        setSupportActionBar(toolbar)


        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(SubstitutionFragment(), getString(R.string.tab_substitutes))
        if (isClassSchoolApiEligible()) {
            adapter.addFragment(HomeworkFragment(), getString(R.string.tab_homework))
            adapter.addFragment(ExamFragment(), getString(R.string.tab_exams))
            navigation.visibility = View.VISIBLE
        } else {
            navigation.visibility = View.GONE
        }
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_substitutes -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.action_homework -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.action_exams -> {
                    viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                navigation.selectedItemId = when (position) {
                    1 -> R.id.action_homework
                    2 -> R.id.action_exams
                    else -> R.id.action_substitutes
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        })

        if (savedInstanceState == null && !BuildConfig.DEBUG) {
            checkForUpdates()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.menu_logout -> {
                logout()
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            item.itemId == R.id.menu_change_class -> showClassChangerDialog()
            item.itemId == R.id.menu_licenses -> showLicenseDialog()
            item.itemId == R.id.menu_courses -> startCourseManagerActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkForUpdates() {
        updater.isUpdateAvailable { showDownloadUpdateDialog(it) }
    }

    private fun showDownloadUpdateDialog(release: GitHubRelease) {
        AlertDialog.Builder(this)
                .setTitle(release.name)
                .setMessage(getString(R.string.updater_new_version_available))
                .setPositiveButton(getString(R.string.download)) { _: DialogInterface, _: Int ->
                    updater.downloadAndInstallUpdate(this, release)
                }
                .setNegativeButton(getString(R.string.ignore)) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .create()
                .show()
    }

    private fun showClassChangerDialog() {
        val view = View.inflate(this, R.layout.dialog_text_input, null)
        view.text.setText(getClassShortcut())
        view.text.setHint(R.string.hint_class_shortcut)
        view.text.setSelection(view.text.length())

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.change_class_shortcut)
        builder.setView(view)
        builder.setPositiveButton(R.string.okay) { _, _ ->
            setClassShortcut(view.text.text.toString())
            recreate()
        }
        builder.create().show()
    }

    private fun showLicenseDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.licenses))
                .setView(R.layout.dialog_licenses)
                .setPositiveButton(R.string.okay, null)
                .create()
                .show()
    }

    private fun startCourseManagerActivity() {
        startActivityForResult(
                Intent(this, CourseManagerActivity::class.java),
                REQUEST_CODE_COURSE_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_COURSE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            reloadEvents()
        }
    }

    private fun reloadEvents() {
        adapter.getAllFragments()
                .filterIsInstance<EventFragment>()
                .forEach { it.loadEvents() }
    }
}
