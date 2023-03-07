package com.example.rvtracker.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * An Custom Implementation Of ListAdapter For Recycler View
 **/
abstract class BaseListAdapter<T : BaseListItem> :
    RecyclerView.Adapter<BaseViewHolder<T>>() {

    private var listDiffer: AsyncListDiffer<T> = AsyncListDiffer(
        AdapterListUpdateCallback(this),
        AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return isItemsSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return isContentSame(oldItem, newItem)
            }
        }).build()
    )

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return getViewHolder(
            LayoutInflater.from(parent.context).inflate(viewType, parent, false), viewType
        ) as BaseViewHolder<T>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bindData(listDiffer.currentList[position])
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<T>,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isNotEmpty()) {
            holder.onBindWithPayload(listDiffer.currentList[position], payloads)
        } else {
            holder.bindData(listDiffer.currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int = listDiffer.currentList[position].layoutRes

    override fun getItemCount(): Int {
        return listDiffer.currentList.size
    }

    fun getCurrentList(): List<T> = listDiffer.currentList

    fun submitList(newList: List<T>?) {
        // since submitList consider only null and not empty list to clear data
        if (newList.isNullOrEmpty()) {
            listDiffer.submitList(null)
        } else {
            listDiffer.submitList(newList)
        }
    }

    /**
     * Due to An Bug in Linear Layout Manager Not Able to retain anchor positions
     * Adding this double check
     * This Helps Maintain the Anchor Positions more accurately
     * https://issuetracker.google.com/issues/73050491
     */
    open fun isItemsSame(oldItem: T, newItem: T): Boolean = isContentSame(oldItem, newItem)

    open fun isContentSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    open fun getPayloadDiff(oldItem: T, newItem: T): Any? {
        return null
    }

    abstract fun getViewHolder(view: View, viewType: Int): BaseViewHolder<*>
}

abstract class BaseViewHolder<T : BaseListItem>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    val containerView: View = itemView

    abstract fun bindData(model: T)
    open fun onBindWithPayload(model: T, payloads: List<Any>) {
        bindData(model)
    }
}

interface BaseListItem {
    val layoutRes: Int
}

