package de.jonashaeusler.vertretungsplan.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.helpers.isLoggedIn
import de.jonashaeusler.vertretungsplan.helpers.setClassShortcut
import de.jonashaeusler.vertretungsplan.helpers.setPassword
import de.jonashaeusler.vertretungsplan.helpers.setUsername
import de.jonashaeusler.vertretungsplan.network.LoginTask
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_toolbar.*

/**
 * Activity to get the user logged in.
 *
 * @see LoginTask
 */

class LoginActivity : AppCompatActivity(), LoginTask.OnLogin {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        setTitle(R.string.login)

        login.setOnClickListener {
            when {
                username.text.isBlank() -> username.error = getString(R.string.error_blank_field)
                password.text.isBlank() -> password.error = getString(R.string.error_blank_field)
                else -> {
                    LoginTask(this).execute(username.text.toString(), password.text.toString())
                }
            }
        }
    }

    override fun onLoginSucceeded() {
        setUsername(username.text.toString())
        setPassword(password.text.toString())
        setClassShortcut(text.text.toString())
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onLoginFailed() {
        Toast.makeText(this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show()
    }
}