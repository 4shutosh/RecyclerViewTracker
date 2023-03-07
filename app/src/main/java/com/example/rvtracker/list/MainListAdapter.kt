package com.example.rvtracker.list

import android.view.View
import com.example.rvtracker.R
import com.example.rvtracker.utils.BaseListAdapter
import com.example.rvtracker.utils.BaseListItem
import com.example.rvtracker.utils.BaseViewHolder

class MainListAdapter : BaseListAdapter<BaseListItem>() {

    override fun getViewHolder(view: View, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            R.layout.list_item_one -> ListItemOneViewHolder(view)
            R.layout.list_item_two -> ListItemTwoViewHolder(view)
            else -> throw Exception("View holder not found for this view type $viewType")
        }
    }

}






