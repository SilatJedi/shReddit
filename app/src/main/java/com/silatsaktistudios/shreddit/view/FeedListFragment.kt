package com.silatsaktistudios.shreddit.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.silatsaktistudios.shreddit.R
import com.silatsaktistudios.shreddit.adapter.FeedListAdapter
import com.silatsaktistudios.shreddit.model.ChildData
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
    (viewModel as? FeedListViewModel)?.getRedditFeed()
  }

  override fun initSubs() {
    (viewModel as? FeedListViewModel)?.let { it ->
      disposables.addAll(

        it.uiState
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            { state ->
              when (state) {
                FeedListViewModel.UiState.GETTING_LIST -> {
                  reddit_feed_swipe_to_refresh.isRefreshing = true
                }
                FeedListViewModel.UiState.FAILED_TO_GET_LIST -> {
                  reddit_feed_swipe_to_refresh.isRefreshing = false
                  displayRetryListRetrievalDialog()
                }
                FeedListViewModel.UiState.LIST_OBTAINED -> {
                  reddit_feed_swipe_to_refresh.isRefreshing = false
                }
                else -> {
                }
              }

            },
            {
              displayRetryListRetrievalDialog()
            }
          ),

        it.dataState
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
                reddit_feed_list.apply {
                    adapter =
                      FeedListAdapter(
                        it.data.children,
                        ::goToWebView
                      )
                    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                }
            },
            {
              displayRetryListRetrievalDialog()
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

  private fun goToWebView(data: ChildData) {
    //TODO: go to webview and pass data
  }

  private fun displayRetryListRetrievalDialog() {
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
}
