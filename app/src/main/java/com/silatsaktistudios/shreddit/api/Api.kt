package com.silatsaktistudios.shreddit.api

import com.google.gson.GsonBuilder
import com.silatsaktistudios.shreddit.model.RedditResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object Api {

  val redditFeedService get() = createService(RedditFeedService::class.java)

  private const val BASE_URL = "https://www.reddit.com"

  private val httpClient = OkHttpClient.Builder()
  private val builder = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(
      GsonConverterFactory.create(
        GsonBuilder().setLenient().create()
      )
    )

  private var retrofit = builder.build()

  //function to instantiate any service
  private fun <S> createService(serviceClass: Class<S>, token: String? = null): S {
    //Just in case we end up having to add oauth calls to any service
    if (token != null) {
      httpClient.interceptors().clear()
      httpClient.addInterceptor { chain: Interceptor.Chain ->
        val og = chain.request()
        val request = og.newBuilder()
          .header("Authorization", "Bearer $token")
          .header("Content-Type", "application/json")
          .build()
        chain.proceed(request)
      }
      builder.client(httpClient.build())
      retrofit = builder.build()
    }

    return retrofit.create(serviceClass)
  }
}

interface RedditFeedService {
  @GET("/.json")
  fun getFeed(): Call<RedditResponse>
}