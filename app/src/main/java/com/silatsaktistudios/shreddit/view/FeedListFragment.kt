package com.silatsaktistudios.shreddit.view

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.silatsaktistudios.shreddit.R
import com.silatsaktistudios.shreddit.adapter.FeedListAdapter
import com.silatsaktistudios.shreddit.api.Api
import com.silatsaktistudios.shreddit.model.RedditResponse
import com.silatsaktistudios.shreddit.supers.ReactiveFragment
import com.silatsaktistudios.shreddit.viewmodel.FeedListViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_feed_list.*


class FeedListFragment : ReactiveFragment(), SwipeRefreshLayout.OnRefreshListener {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_feed_list, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    reddit_feed_swipe_to_refresh.setOnRefreshListener(this)

    (viewModel as? FeedListViewModel)?.apply {
      cachedLocalData?.let {
        setListAdapter(it)
      }
      getRedditFeed()
    }
  }

  override fun initSubs() {
    (viewModel as? FeedListViewModel)?.let { it ->
      disposables.addAll(

        it.listUiState
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            { state ->
              when (state) {
                FeedListViewModel.ListUiState.GETTING_LIST -> {
                  reddit_feed_swipe_to_refresh.isRefreshing = true
                }
                FeedListViewModel.ListUiState.FAILED_TO_GET_LIST -> {
                  reddit_feed_swipe_to_refresh.isRefreshing = false
                  displayRetryDialog()
                }
                FeedListViewModel.ListUiState.LIST_OBTAINED -> {
                  reddit_feed_swipe_to_refresh.isRefreshing = false
                }
                else -> {
                }
              }

            },
            {
              displayRetryDialog()
            }
          ),

        it.dataState
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              setListAdapter(it)
            },
            {
              displayRetryDialog()
            }
          )
      )
    }
  }

  override fun initViewModel() {
    viewModel = ViewModelProvider(
      viewModelStore,
      ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    ).get(FeedListViewModel::class.java)
  }

  override fun onRefresh() {
    (viewModel as? FeedListViewModel)?.getRedditFeed()
  }

  private fun displayRetryDialog() {
    if (isResumed)
      AlertDialog.Builder(requireContext())
        .setTitle("Could Not Connect")
        .setCancelable(false)
        .setIcon(R.drawable.ic_error_outline_black_24dp)
        .setNegativeButton("Quit") { _: DialogInterface, _: Int ->
          requireActivity().finish()
        }
        .setPositiveButton("Retry") { _: DialogInterface, _: Int ->
          onRefresh()
        }
        .create()
        .show()
  }

  private fun setListAdapter(redditResponse: RedditResponse) {
    reddit_feed_list.apply {
      adapter =
        FeedListAdapter(
          redditResponse.data.children
        ) {
          if (canScreenHandleListDetailView()) {
            (viewModel as? FeedListViewModel)?.setSelectedRedditPost(it)
          } else {
            findNavController().navigate(
              FeedListFragmentDirections.actionFeedListFragmentToViewRedditPostFragment(
                "${Api.BASE_URL}${it.permalink}"
              )
            )
          }
        }
      addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }
  }

  private fun canScreenHandleListDetailView(): Boolean {
    val isTablet = requireContext().resources.getBoolean(R.bool.isTablet)
    val orientation = requireContext().resources.configuration.orientation

    return isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE
  }
}
