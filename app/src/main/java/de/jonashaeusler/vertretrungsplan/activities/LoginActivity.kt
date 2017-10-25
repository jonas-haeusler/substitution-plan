package de.jonashaeusler.vertretrungsplan.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import de.jonashaeusler.vertretrungsplan.R
import de.jonashaeusler.vertretrungsplan.helpers.isLoggedIn
import de.jonashaeusler.vertretrungsplan.helpers.setClassShortcut
import de.jonashaeusler.vertretrungsplan.helpers.setPassword
import de.jonashaeusler.vertretrungsplan.helpers.setUsername
import de.jonashaeusler.vertretrungsplan.network.LoginTask
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Activity to get the user logged in.
 *
 * @see LoginTask
 */

class LoginActivity : AppCompatActivity(), LoginTask.OnLogin {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLoggedIn()) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
            return
        }

        setContentView(R.layout.activity_login)
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
        setClassShortcut(classShortcut.text.toString())
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onLoginFailed() {
        Toast.makeText(this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show()
    }
}