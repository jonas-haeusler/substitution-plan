package de.jonashaeusler.vertretungsplan.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.adapter.ViewPagerAdapter
import de.jonashaeusler.vertretungsplan.fragments.EventFragment
import de.jonashaeusler.vertretungsplan.fragments.ExamFragment
import de.jonashaeusler.vertretungsplan.fragments.HomeworkFragment
import de.jonashaeusler.vertretungsplan.fragments.SubstitutionFragment
import de.jonashaeusler.vertretungsplan.helpers.*
import de.jonashaeusler.vertretungsplan.interfaces.OnServerStatusResolved
import de.jonashaeusler.vertretungsplan.network.ServerStatusTask
import kotlinx.android.synthetic.main.activty_main.*
import kotlinx.android.synthetic.main.dialog_text_input.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnServerStatusResolved {
    private lateinit var adapter: ViewPagerAdapter
    private var serverStatusDialog: AlertDialog? = null
    private val dateFormat = SimpleDateFormat("dd. MMMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_main)
        setSupportActionBar(toolbar)

        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(SubstitutionFragment(), getString(R.string.tab_substitutes))
        if (isTgi11()) {
            adapter.addFragment(HomeworkFragment(), getString(R.string.tab_homework))
            adapter.addFragment(ExamFragment(), getString(R.string.tab_exams))
            tabLayout.visibility = View.VISIBLE
        } else {
            tabLayout.visibility = View.GONE
        }
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.menu_server_status).isVisible = isTgi11()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.menu_logout -> {
                logout()
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            item.itemId == R.id.menu_server_status -> showServerStatusDialog()
            item.itemId == R.id.menu_change_class -> showClassChangerDialog()
            item.itemId == R.id.menu_licenses -> showLicenseDialog()
            item.itemId == R.id.menu_filter -> showFilterDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onServerStatusResolved(status: List<String>) {
        @Suppress("DEPRECATION")
        serverStatusDialog?.setMessage(Html.fromHtml(String.format(getString(R.string.server_status_message),
                status[1], dateFormat.format(Date(status[0].toLong() * 1000)))))
    }

    private fun showClassChangerDialog() {
        val view = View.inflate(this, R.layout.dialog_text_input, null)
        view.text.setText(getClassShortcut())
        view.text.setHint(R.string.hint_class_shortcut)
        view.text.setSelection(view.text.length())

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.change_class_shortcut)
        builder.setView(view)
        builder.setPositiveButton(R.string.okay, { _, _ ->
            setClassShortcut(view.text.text.toString())
            recreate()
        })
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

    private fun showServerStatusDialog() {
        if (serverStatusDialog == null) {
            serverStatusDialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.server_status))
                    .create()
        }

        serverStatusDialog?.setMessage(getString(R.string.loading))
        serverStatusDialog?.show()
        ServerStatusTask(this).execute()
    }

    private fun showFilterDialog() {
        val view = View.inflate(this, R.layout.dialog_text_input, null)
        view.text.setText(getFilter())
        view.text.setHint(R.string.hint_regex_filter)
        view.text.setSelection(view.text.length())

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.filter)
        builder.setView(view)
        builder.setPositiveButton(R.string.okay, { _, _ ->
            setFilter(view.text.text.toString())
            adapter.getAllFragments()
                    .filterIsInstance<EventFragment>()
                    .forEach { it.loadEvents() }
        })
        builder.create().show()
    }
}