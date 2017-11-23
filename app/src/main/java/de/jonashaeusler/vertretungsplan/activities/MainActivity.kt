package de.jonashaeusler.vertretungsplan.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.adapter.ViewPagerAdapter
import de.jonashaeusler.vertretungsplan.fragments.ExamFragment
import de.jonashaeusler.vertretungsplan.fragments.HomeworkFragment
import de.jonashaeusler.vertretungsplan.fragments.SubstitutionFragment
import de.jonashaeusler.vertretungsplan.helpers.getClassShortcut
import de.jonashaeusler.vertretungsplan.helpers.logout
import de.jonashaeusler.vertretungsplan.helpers.setClassShortcut
import kotlinx.android.synthetic.main.activty_main.*
import kotlinx.android.synthetic.main.dialog_change_class.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_main)
        setSupportActionBar(toolbar)

        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(SubstitutionFragment(), getString(R.string.tab_substitutes))
        adapter.addFragment(HomeworkFragment(), getString(R.string.tab_homework))
        adapter.addFragment(ExamFragment(), getString(R.string.tab_exams))
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

        tabLayout.setupWithViewPager(viewPager)
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
            item.itemId == R.id.menu_changeClass -> showClassChangerDialog()
            item.itemId == R.id.menu_licenses -> showLicenseDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showClassChangerDialog() {
        val view = View.inflate(this, R.layout.dialog_change_class, null)
        view.classShortcut.setText(getClassShortcut())
        view.classShortcut.setSelection(view.classShortcut.length())

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.change_class_shortcut)
        builder.setView(view)
        builder.setPositiveButton(R.string.okay, { _, _ ->
            setClassShortcut(view.classShortcut.text.toString())
            (adapter.getFragment(0) as? SubstitutionFragment)?.loadEvents()
        })
        builder.create().show()
    }

    private fun showLicenseDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.licenses))
        builder.setView(R.layout.dialog_licenses)
        builder.setPositiveButton(R.string.okay, null)
        builder.create().show()
    }
}