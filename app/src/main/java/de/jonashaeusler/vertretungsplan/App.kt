package de.jonashaeusler.vertretungsplan

import android.app.Application
import de.jonashaeusler.vertretungsplan.data.notifications.SubstitutionUpdater

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SubstitutionUpdater(this)
    }
}
