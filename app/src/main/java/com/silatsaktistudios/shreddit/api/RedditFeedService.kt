package com.silatsaktistudios.shreddit.api

import com.silatsaktistudios.shreddit.model.RedditResponse
import retrofit2.Call
import retrofit2.http.GET

interface RedditFeedService {
  @GET("/.json")
  fun getFeed(): Call<RedditResponse>
}