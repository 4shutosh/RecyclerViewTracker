package com.example.rvtracker

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rvtracker.databinding.ActivityMainBinding
import com.example.rvtracker.list.MainListAdapter
import com.example.rvtracker.tracker.RecyclerViewTracker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel by viewModels<MainViewModel>()

    private val listAdapter by lazy {
        MainListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvMain.apply {
            adapter = listAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                    // based on complete visibility of the item
                    val firstItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                    if (firstItem >= 0 && lastItem >= 0) mainViewModel.actionListScrolled(firstItem, lastItem)


                    // based on height percentage
//                    val firstItem = layoutManager.findFirstVisibleItemPosition()
//                    val lastItem = layoutManager.findLastVisibleItemPosition()
//                    analyzeListView(firstItem, lastItem)

                }
            })
        }
    }

    private fun setupObservers() {
        mainViewModel.viewList.observe(this) {
            listAdapter.submitList(it)
        }
    }

    private fun analyzeListView(first: Int, last: Int) {
        val layoutManager = binding.rvMain.layoutManager as? LinearLayoutManager
        var firstVisible: Int? = null
        var lastVisible: Int? = null
        if (layoutManager != null) {
            for ((i, j) in (first..last).zip(last downTo first)) {

                if (firstVisible != null && lastVisible != null) {
                    mainViewModel.actionListScrolled(firstVisible, lastVisible)
                    break
                }

                if (firstVisible == null) if (isItemVisibleOrNot(layoutManager.findViewByPosition(i))) firstVisible = i
                if (lastVisible == null) if (isItemVisibleOrNot(layoutManager.findViewByPosition(j))) lastVisible = j
            }
        }
    }

    private fun isItemVisibleOrNot(view: View?) =
        (view?.getVisibleHeightPercentage() ?: 0) >= RecyclerViewTracker.ITEM_VISIBILITY_THRESHOLD_PERCENTAGE


    private fun View.getVisibleHeightPercentage(): Int {
        val itemRect = Rect()
        getLocalVisibleRect(itemRect)

        val visibleHeight = itemRect.height()
        val height = measuredHeight

        return ((visibleHeight / height) * 100)
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.actionPageForegroundChanged(false)
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.actionPageForegroundChanged(true)
    }
}