package com.silatsaktistudios.shreddit.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.silatsaktistudios.shreddit.R
import com.silatsaktistudios.shreddit.api.Api
import com.silatsaktistudios.shreddit.supers.ReactiveFragment
import com.silatsaktistudios.shreddit.viewmodel.FeedListViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_view_reddit_post.*

class ViewRedditPostFragment : ReactiveFragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_view_reddit_post, container, false)
  }


  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    view_post_web_view.apply {

      @SuppressLint("SetJavaScriptEnabled")
      settings.javaScriptEnabled = true

      webViewClient = object: WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
          view_post_progress_bar_container?.visibility = View.VISIBLE
          super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
          view_post_progress_bar_container?.visibility = View.GONE
          super.onPageFinished(view, url)
        }
      }

      arguments?.getString("url")?.let {
        loadUrl(it)
      }
    }
  }

  override fun initSubs() {
    (viewModel as? FeedListViewModel)?.let { it ->
      disposables.add(
        it.selectedState
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe {
            view_post_web_view.loadUrl("${Api.BASE_URL}${it.permalink}")
          }
      )
    }
  }

  override fun initViewModel() {
    viewModel = ViewModelProvider(
      viewModelStore,
      ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    ).get(FeedListViewModel::class.java)
  }

}
