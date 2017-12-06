package de.jonashaeusler.vertretungsplan.helpers

import android.content.Context
import android.preference.PreferenceManager

/**
 * Utility class for basic "account" management.
 */

private val userUsername = "USER_USERNAME"
private val userPassword = "USER_PASSWORD"
private val userClassShortcut = "USER_CLASS_SHORTCUT"

fun Context.isLoggedIn(): Boolean =
        getUsername().isNotBlank() && getPassword().isNotBlank()

fun Context.logout() {
    PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .remove(userUsername)
            .remove(userPassword)
            .remove(userClassShortcut)
            .apply()
}

fun Context.getUsername(): String {
    return PreferenceManager.getDefaultSharedPreferences(this)
            .getString(userUsername, "")
}

fun Context.getPassword(): String {
    return PreferenceManager.getDefaultSharedPreferences(this)
            .getString(userPassword, "")
}

fun Context.getClassShortcut(): String {
    return PreferenceManager.getDefaultSharedPreferences(this)
            .getString(userClassShortcut, "")
}

fun Context.setUsername(username: String) {
    PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString(userUsername, username)
            .apply()
}

fun Context.setPassword(password: String) {
    PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString(userPassword, password)
            .apply()
}

fun Context.setClassShortcut(classShort: String) {
    PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString(userClassShortcut, classShort)
            .apply()
}

fun Context.isTgi11(): Boolean =
        getClassShortcut().contains(Regex("t?g?(i11|11/?4)", RegexOption.IGNORE_CASE))