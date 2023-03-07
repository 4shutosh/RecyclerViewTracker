package com.example.rvtracker.logger

import logcat.logcat
import javax.inject.Inject


class AbstractLoggerClass @Inject constructor(
// analytics, API, etc here to log the results
) {


    fun sendViewItemToAnalytics(items: List<ItemTrackData>) {
        logcat { "Items logged $items" }
    }

    data class ItemTrackData(
        val id: String,
        val type: String,
    )

}