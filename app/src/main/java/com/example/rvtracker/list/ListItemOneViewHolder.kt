package com.example.rvtracker.list

import android.view.View
import com.example.rvtracker.R
import com.example.rvtracker.databinding.ListItemOneBinding
import com.example.rvtracker.utils.BaseListItem
import com.example.rvtracker.utils.BaseViewHolder

class ListItemOneViewHolder(view: View) : BaseViewHolder<ListItemOneViewHolder.ListItemOne>(view) {

    private val binding = ListItemOneBinding.bind(view)

    data class ListItemOne(
        val id: String,
    ) : BaseListItem {
        override val layoutRes: Int = R.layout.list_item_one
    }

    override fun bindData(model: ListItemOne) {
        binding.tvTextMain.text = "Type ONE ${model.id}"
    }
}

