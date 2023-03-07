package com.example.rvtracker.utils

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Custom Implementation of [LinearLayoutManager]
 * Which stores height of all children and updates them regularly onLayout
 * added functions with help calculate better first and last visible items
 */
class LinearLayoutManagerAccurateOffset : LinearLayoutManager {

    constructor(context: Context) : super(context)

    constructor(
        context: Context,
        @RecyclerView.Orientation orientation: Int,
        reverseLayout: Boolean,
    ) : super(context, orientation, reverseLayout)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    // map of child adapter position to its height.
    private val childSizesMap = mutableMapOf<Int, Int>()

    private var heightChangeListeners = mutableListOf<Pair<Int, ((Int) -> Unit)?>>()

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        for (i in 0 until childCount) {
            val child = getChildAt(i)!!
            childSizesMap[getPosition(child)] = child.height
        }
        if (heightChangeListeners.isNotEmpty()) {
            heightChangeListeners.forEach {
                it.second?.invoke(getHeightTillIndex(it.first))
            }
        }
    }

    private fun getHeightTillIndex(index: Int): Int =
        if (index > childSizesMap.values.size) -1 else childSizesMap.values.take(index + 1).sum()

    fun addHeightChangeForIndexListener(index: Int, observer: ((Int) -> Unit)?) {
        heightChangeListeners.add(Pair(index, observer))
    }

    override fun findFirstVisibleItemPosition(): Int {
        val item: View? = findVisibleItem(0, childCount, false)
        return if (item == null) -1 else getPosition(item)
    }

    override fun findFirstCompletelyVisibleItemPosition(): Int {
        val item: View? = findVisibleItem(0, childCount, true)
        return if (item == null) -1 else getPosition(item)
    }

    override fun findLastVisibleItemPosition(): Int {
        val item: View? = findVisibleItem(childCount - 1, -1, false)
        return if (item == null) -1 else getPosition(item)
    }

    override fun findLastCompletelyVisibleItemPosition(): Int {
        val item: View? = findVisibleItem(childCount - 1, -1, true)
        return if (item == null) -1 else getPosition(item)
    }

    private fun findVisibleItem(
        fromIndex: Int,
        toIndex: Int,
        isCompletely: Boolean,
    ): View? {
        val next = if (toIndex > fromIndex) 1 else -1
        var i = fromIndex
        while (i != toIndex) {
            val child: View? = getChildAt(i)
            child?.let {
                if (checkIsVisible(child, isCompletely)) {
                    return child
                }
                i += next
            }
        }
        return null
    }

    private fun checkIsVisible(child: View, isCompletely: Boolean): Boolean {
        val location = IntArray(2)
        child.getLocationOnScreen(location)
        val parent: View = child.parent as View
        val parentRect = Rect()
        parent.getGlobalVisibleRect(parentRect)
        return if (orientation == HORIZONTAL) {
            val childLeft = location[0]
            val childRight: Int = location[0] + child.width
            if (isCompletely) {
                childLeft >= parentRect.left && childRight <= parentRect.right
            } else {
                childLeft <= parentRect.right && childRight >= parentRect.left
            }
        } else {
            val childTop = location[1]
            val childBottom: Int = location[1] + child.height
            if (isCompletely) {
                childTop >= parentRect.top && childBottom <= parentRect.bottom
            } else {
                childTop <= parentRect.bottom && childBottom >= parentRect.top
            }
        }
    }

}