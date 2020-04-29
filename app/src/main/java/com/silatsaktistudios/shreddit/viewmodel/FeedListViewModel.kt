package com.silatsaktistudios.shreddit.viewmodel

import androidx.lifecycle.ViewModel
import com.silatsaktistudios.shreddit.api.Api
import com.silatsaktistudios.shreddit.model.ChildData
import com.silatsaktistudios.shreddit.model.RedditResponse
import io.reactivex.rxjava3.subjects.ReplaySubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedListViewModel : ViewModel() {

  val listUiState: ReplaySubject<ListUiState> = ReplaySubject.create()
  val dataState: ReplaySubject<RedditResponse> = ReplaySubject.create()
  val selectedState: ReplaySubject<ChildData> = ReplaySubject.create()
  val cachedLocalData: RedditResponse? get() = localDataState

  private var selectedData: ChildData? = null
  private var localDataState: RedditResponse? = null


  fun setSelectedRedditPost(data: ChildData) {
    selectedData = data
    selectedState.onNext(selectedData)
  }

  fun getRedditFeed() {
    listUiState.onNext(ListUiState.GETTING_LIST)

    Api.redditFeedService.getFeed().enqueue(
      object : Callback<RedditResponse> {
        override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
          listUiState.onNext(ListUiState.FAILED_TO_GET_LIST)
        }

        override fun onResponse(
          call: Call<RedditResponse>,
          response: Response<RedditResponse>
        ) {
          if (response.isSuccessful) {
            listUiState.onNext(ListUiState.LIST_OBTAINED)
            localDataState = response.body()
            dataState.onNext(localDataState)
          } else {
            listUiState.onNext(ListUiState.FAILED_TO_GET_LIST)
          }
        }
      }
    )
  }

  enum class ListUiState {
    GETTING_LIST,
    FAILED_TO_GET_LIST,
    LIST_OBTAINED
  }
}