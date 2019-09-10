package de.jonashaeusler.vertretungsplan.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by jonas on 8/2/17.
 *
 * RecyclerView with support for adding an empty view.
 */
class EmptyRecyclerView : RecyclerView {
    private var emptyView: View? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun setAdapter(adapter: Adapter<*>?) {
        getAdapter()?.unregisterAdapterDataObserver(observer)
        adapter?.registerAdapterDataObserver(observer)
        super.setAdapter(adapter)
        checkEmptyState()
    }

    override fun swapAdapter(adapter: Adapter<*>?, removeAndRecycleExistingViews: Boolean) {
        getAdapter()?.unregisterAdapterDataObserver(observer)
        adapter?.registerAdapterDataObserver(observer)
        super.swapAdapter(adapter, removeAndRecycleExistingViews)
        checkEmptyState()
    }

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            checkEmptyState()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            checkEmptyState()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            checkEmptyState()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            checkEmptyState()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            checkEmptyState()
        }

        override fun onChanged() {
            super.onChanged()
            checkEmptyState()
        }
    }

    fun setEmptyView(emptyView: View?) {
        this.emptyView = emptyView
        emptyView?.visibility = View.GONE
        checkEmptyState()
    }

    private fun checkEmptyState() {
        if (emptyView == null) {
            return
        }
        adapter?.let {
            if (it.itemCount > 0) {
                emptyView?.visibility = View.GONE
                this.visibility = View.VISIBLE
            } else {
                emptyView?.visibility = View.VISIBLE
                this.visibility = View.GONE
            }
        }
    }
}
