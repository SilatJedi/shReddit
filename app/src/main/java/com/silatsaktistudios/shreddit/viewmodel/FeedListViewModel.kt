package com.silatsaktistudios.shreddit.viewmodel

import androidx.lifecycle.ViewModel
import com.silatsaktistudios.shreddit.api.Api
import com.silatsaktistudios.shreddit.model.RedditResponse
import io.reactivex.rxjava3.subjects.ReplaySubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedListViewModel : ViewModel() {

  val uiState: ReplaySubject<UiState> = ReplaySubject.create()
  val dataState: ReplaySubject<RedditResponse> = ReplaySubject.create()
  private var localDataState: RedditResponse? = null

  init {
    localDataState?.let {
      dataState.onNext(it)
    }
  }

  fun getRedditFeed() {
    uiState.onNext(UiState.GETTING_LIST)

    Api.redditFeedService.getFeed().enqueue(
      object : Callback<RedditResponse> {
        override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
          uiState.onNext(UiState.FAILED_TO_GET_LIST)
        }

        override fun onResponse(
          call: Call<RedditResponse>,
          response: Response<RedditResponse>
        ) {
          if (response.isSuccessful) {
            uiState.onNext(UiState.LIST_OBTAINED)
            localDataState = response.body()
            dataState.onNext(localDataState)
          } else {
            uiState.onNext(UiState.FAILED_TO_GET_LIST)
          }
        }
      }
    )
  }

  enum class UiState {
    GETTING_LIST,
    FAILED_TO_GET_LIST,
    LIST_OBTAINED
  }
}