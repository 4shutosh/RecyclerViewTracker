package com.example.rvtracker.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.rvtracker.R
import com.example.rvtracker.databinding.ListItemOneBinding
import com.example.rvtracker.databinding.ListItemTwoBinding
import com.example.rvtracker.utils.BaseListItem
import com.example.rvtracker.utils.BaseViewHolder

class ListItemTwoViewHolder(view: View) : BaseViewHolder<ListItemTwoViewHolder.ListItemTwo>(view) {

    private val binding = ListItemTwoBinding.bind(view)

    data class ListItemTwo(
        val id: String,
    ) : BaseListItem {
        override val layoutRes: Int = R.layout.list_item_two
    }

    override fun bindData(model: ListItemTwo) {
        binding.tvTextMain.text = "Type TWO ${model.id}"
    }
}

