package de.jonashaeusler.vertretrungsplan.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import de.jonashaeusler.vertretrungsplan.R
import de.jonashaeusler.vertretrungsplan.fragments.EventFragment
import de.jonashaeusler.vertretrungsplan.helpers.getClassShortcut
import de.jonashaeusler.vertretrungsplan.helpers.logout
import de.jonashaeusler.vertretrungsplan.helpers.setClassShortcut
import kotlinx.android.synthetic.main.dialog_change_class.view.*

class MainActivity : AppCompatActivity() {
    private var eventFragment: EventFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_main)
        eventFragment = supportFragmentManager
                .findFragmentById(R.id.substituteFragment) as EventFragment?
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
            eventFragment?.loadEvents()
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