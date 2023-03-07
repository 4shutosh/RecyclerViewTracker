package com.example.rvtracker.tracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RecyclerViewTracker @Inject constructor(

) {

    companion object {
        const val RECYCLER_VIEW_TRACKER_LIST_LIMIT: Int = 15
        const val ITEM_VISIBILITY_THRESHOLD_PERCENTAGE: Int = 50

        // we can make this config driven to give control to the product owners/analysts
        private const val VIEW_TRACKER_THRESHOLD: Long = 5000L
    }

    private val _itemsViewedFlow = MutableSharedFlow<VisibleListItemState>(
        replay = 0, extraBufferCapacity = 1, BufferOverflow.DROP_OLDEST
    )

    val itemsViewedFlow = _itemsViewedFlow
        .asSharedFlow()
        .distinctUntilChanged()
        .debounce(VIEW_TRACKER_THRESHOLD)
        .flowOn(Dispatchers.IO)


    data class VisibleListItemState(
        val firstVisibleItem: Int,
        val lastVisibleItem: Int,
    )

    suspend fun postViewEvent(firstItem: Int, lastItem: Int) {
        _itemsViewedFlow.emit(VisibleListItemState(firstItem, lastItem))
    }

}