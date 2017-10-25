package de.jonashaeusler.vertretrungsplan.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import de.jonashaeusler.vertretrungsplan.R

class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.item_divider)
    private var mLeftPadding = 0
    private var mRightPadding = 0

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
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

    fun setPadding(left: Int, right: Int) {
        mLeftPadding = left
        mRightPadding = right
    }
}