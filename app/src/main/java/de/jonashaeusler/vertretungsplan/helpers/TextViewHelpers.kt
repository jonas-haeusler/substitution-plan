package de.jonashaeusler.vertretungsplan.helpers

import android.animation.ValueAnimator
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.TextView

/**
 * Created by jonas on 24.11.17.
 */
fun TextView.startStrikeThroughAnimation(): ValueAnimator {
    val span = SpannableString(text)
    val strikeSpan = StrikethroughSpan()
    val animator = ValueAnimator.ofInt(text.length)
    animator.addUpdateListener {
        span.setSpan(strikeSpan, 0, it.animatedValue as Int, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = span
        invalidate()
    }
    animator.start()
    return animator
}

fun TextView.reverseStrikeThroughAnimation(): ValueAnimator {
    val span = SpannableString(text.toString())
    val strikeSpan = StrikethroughSpan()
    val animator = ValueAnimator.ofInt(text.length, 0)
    animator.addUpdateListener {
        span.setSpan(strikeSpan, 0, it.animatedValue as Int, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = span
        invalidate()
    }
    animator.start()
    return animator
}

fun TextView.isEllipsized(): Boolean {
    layout?.let {
        if (layout.lineCount > 0) {
            if (layout.getEllipsisCount(layout.lineCount - 1) > 0) {
                return true
            }
        }
    }
    return false
}
