package de.jonashaeusler.vertretungsplan.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.jonashaeusler.vertretungsplan.R

class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable = checkNotNull(
            ContextCompat.getDrawable(context, R.drawable.item_divider))
    private var mLeftPadding = 0
    private var mRightPadding = 0

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + mLeftPadding
        val right = parent.width - parent.paddingRight + mRightPadding

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight

            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}
