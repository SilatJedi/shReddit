package com.silatsaktistudios.shreddit.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.silatsaktistudios.shreddit.R
import com.silatsaktistudios.shreddit.supers.ReactiveFragment
import com.silatsaktistudios.shreddit.viewmodel.FeedListViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_feed_list.*

class FeedListFragment : ReactiveFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed_list, container, false)

        initSubs()
        (viewModel as? FeedListViewModel)?.getRedditFeed()

        return view
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
                                FeedListViewModel.UiState.GETTING_LIST -> {}
                                FeedListViewModel.UiState.FAILED_TO_GET_LIST -> displayRetryListRetrievalDialog()
                                FeedListViewModel.UiState.LIST_OBTAINED -> {}
                                else -> {}
                            }

                        },
                        {
                            displayRetryListRetrievalDialog(it)
                        }
                    ),
                it.dataState
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            reddit_feed_list.adapter = FeedListAdapter(it.data.children)
                        },
                        {

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

    private fun displayRetryListRetrievalDialog(throwable: Throwable? = null) {
        throwable?.let {  }
    }

//    companion object {
//        @JvmStatic
//        fun newInstance() = FeedListFragment()
//    }
}
