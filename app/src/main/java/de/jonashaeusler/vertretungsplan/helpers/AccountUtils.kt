package de.jonashaeusler.vertretungsplan.helpers

import android.content.Context
import android.preference.PreferenceManager

/**
 * Utility class for basic "account" management.
 */

private val userUsername = "USER_USERNAME"
private val userPassword = "USER_PASSWORD"
private val userClassShortcut = "USER_CLASS_SHORTCUT"
private val userIgnoredCourses = "USER_FILTER"

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

fun Context.getIgnoredCourses(): List<String> {
    return PreferenceManager.getDefaultSharedPreferences(this)
            .getString(userIgnoredCourses, "")
            .split(", ")
}

fun Context.getIgnoredCoursesAsRegex(): Regex {
    return getIgnoredCourses().joinToString("|").toRegex(RegexOption.IGNORE_CASE)
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

fun Context.setIgnoredCourses(courses: List<String>) {
    PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString(userIgnoredCourses, courses.joinToString(", "))
            .apply()
}

// todo: don't hardcode the class shortcut
fun Context.isClassSchoolApiEligible(): Boolean =
        getClassShortcut().contains(Regex("t?g?(i12|12/?4)", RegexOption.IGNORE_CASE))
