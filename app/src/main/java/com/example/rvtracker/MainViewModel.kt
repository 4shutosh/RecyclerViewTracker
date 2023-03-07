package com.example.rvtracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rvtracker.list.ListItemOneViewHolder
import com.example.rvtracker.list.ListItemTwoViewHolder
import com.example.rvtracker.logger.AbstractLoggerClass
import com.example.rvtracker.tracker.RecyclerViewTracker
import com.example.rvtracker.utils.BaseListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import logcat.logcat
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recyclerViewTracker: RecyclerViewTracker,
    private val abstractLoggerClass: AbstractLoggerClass,
) : ViewModel() {

    private var listViewTrackerJob: Job? = null

    private val _viewList = MutableLiveData<List<BaseListItem>>(mutableListOf())
    val viewList: LiveData<List<BaseListItem>> = _viewList

    private fun currentUiList() = _viewList.value!!

    private val viewCountEvents by lazy { mutableListOf<AbstractLoggerClass.ItemTrackData>() }

    init {
        populateListData()
    }


    private fun populateListData() {
        viewModelScope.launch {
            _viewList.postValue(mutableListOf<BaseListItem>().apply {
                for (i in 1..10) {
                    add(ListItemOneViewHolder.ListItemOne("$i one"))
                    add(ListItemTwoViewHolder.ListItemTwo(("$i two")))
                }
            })
        }
    }

    fun actionListScrolled(firstItem: Int, lastItem: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recyclerViewTracker.postViewEvent(firstItem, lastItem)
        }
    }

    fun actionPageForegroundChanged(visible: Boolean) {
        viewModelScope.launch {
            if (visible) {
                listViewTrackerJob = viewModelScope.launch(Dispatchers.IO) {
                    recyclerViewTracker.itemsViewedFlow.collect {
                        for (index in it.firstVisibleItem..it.lastVisibleItem) {
                            if (currentUiList().isNotEmpty()) {
                                val item = currentUiList().getOrNull(index)
                                logcat { "found item to be tracked $item" }
                                when (item) {
                                    is ListItemOneViewHolder.ListItemOne -> {
                                        viewCountEvents.add(
                                            AbstractLoggerClass.ItemTrackData(
                                                id = "id", // some uid here from the model class item.id
                                                type = "one"
                                            )
                                        )
                                        if (viewCountEvents.size == RecyclerViewTracker.RECYCLER_VIEW_TRACKER_LIST_LIMIT) sendListTrackingData()
                                    }
                                    is ListItemTwoViewHolder.ListItemTwo -> {
                                        // we don't want to track the second type
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                listViewTrackerJob?.cancelChildren()
                sendListTrackingData()
            }
        }
    }

    private fun sendListTrackingData() {
        if (viewCountEvents.isNotEmpty()) abstractLoggerClass.sendViewItemToAnalytics(viewCountEvents)
    }

}