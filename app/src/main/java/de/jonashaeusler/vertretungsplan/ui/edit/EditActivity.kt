package de.jonashaeusler.vertretungsplan.ui.edit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import de.jonashaeusler.vertretungsplan.R
import de.jonashaeusler.vertretungsplan.data.network.api.SchulbotApi
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditActivity : AppCompatActivity(R.layout.activity_edit) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val schulbotApi = SchulbotApi.create(this)

        buttonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            save.isEnabled = buttonGroup.checkedButtonId != View.NO_ID
            if (isChecked) {
                val call = when (checkedId) {
                    R.id.homework -> schulbotApi.getPlaintextHomework()
                    R.id.exams -> schulbotApi.getPlaintextExams()
                    R.id.info -> schulbotApi.getPlaintextInfo()
                    else -> schulbotApi.getPlaintextHomework()
                }

                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) response.body()?.let {
                            val formattedText = it
                                    .replace("--..--..--", "\n\n")
                                    .replace("--..--", "\n")
                            text.setText(formattedText)
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Snackbar.make(container, R.string.error_no_connection, Snackbar.LENGTH_LONG).show()
                    }
                })
            } else {
                text.text = null
            }
        }

        save.setOnClickListener {
            save.isEnabled = false
            val type = when (buttonGroup.checkedButtonId) {
                R.id.homework -> "homework"
                R.id.exams -> "exam"
                R.id.info -> "info"
                else -> return@setOnClickListener
            }

            val newText = if (type == "homework" || type == "exam") {
                text.text.toString()
                        .replace("\n\n", "--..--..--")
                        .replace("\n", "--..--")
            } else {
                text.text.toString()
            }

            schulbotApi.edit(
                    type,
                    newText
            ).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Snackbar.make(container, "${response.code()} ${response.message()}", Snackbar.LENGTH_LONG).show()
                    save.isEnabled = true
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    t.printStackTrace()
                    Snackbar.make(container, t.localizedMessage, Snackbar.LENGTH_LONG).show()
                    save.isEnabled = true
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(ARG_CHECKED_BUTTON, buttonGroup.checkedButtonId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        buttonGroup.check(savedInstanceState?.getInt(ARG_CHECKED_BUTTON) ?: return)
    }

    companion object {
        private const val ARG_CHECKED_BUTTON = "ARG_CHECKED_BUTTON"
    }
}
