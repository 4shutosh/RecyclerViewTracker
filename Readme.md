
# Recycler View Tracker

#### Calculate time spent on each item by user of a recycler view list, to serve as an engagement/analytics metric.


- Key files - [MainActivity](https://github.com/4shutosh/RecyclerViewTracker/blob/main/app/src/main/java/com/example/rvtracker/MainActivity.kt), [MainViewModel](https://github.com/4shutosh/RecyclerViewTracker/blob/main/app/src/main/java/com/example/rvtracker/MainViewModel.kt), [RecyclerViewTracker](https://github.com/4shutosh/RecyclerViewTracker/blob/main/app/src/main/java/com/example/rvtracker/tracker/RecyclerViewTracker.kt)
- Based on Kotlin Coroutines and Flow
- Filter out view types that are not required to be tracked
- Tracking should stopped/resumed based on activity/fragment lifecycle
- Two types of implementation - Complete visibility & Height percentage based visibility
- In some cases, the current implementation will not respect the loading time required to populate the list for the very first time.
- With the help of these great blogs: [krtkush](https://krtkush.com/2017/03/29/android-recyclerview-tracking-view-time.html) & [ProAndroidDev](https://proandroiddev.com/detecting-list-items-perceived-by-user-8f164dfb1d05)




