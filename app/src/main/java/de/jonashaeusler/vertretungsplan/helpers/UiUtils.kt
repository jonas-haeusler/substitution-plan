package de.jonashaeusler.vertretungsplan.helpers

import android.content.Context
import android.support.v4.app.Fragment
import android.util.DisplayMetrics

/**
 * Calculates Pixel sizes on the current Display given DP dimensions.
 *
 * @param dp The size in dp to convert
 * @return How many pixels represent the given dp at the current dpi
 */
fun Context.dpToPx(dp: Int): Int {
    val dm = this.resources.displayMetrics
    return Math.round(dp * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

/**
 * Calculates DP dimensions for the given Pixel sizes on the current Display.
 *
 * @param px The size in pixels to convert
 * @return How many dp represent the given px at the current dpi
 */
fun Context.pxToDp(px: Int): Int {
    val dm = this.resources.displayMetrics
    return Math.round(px / (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

/**
 * See [dpToPx] for more information.
 */
fun Fragment.dpToPx(dp: Int) = this.activity.dpToPx(dp)

/**
 * See [pxToDp] for more information.
 */
fun Fragment.pxToDp(px: Int) = this.activity.pxToDp(px)