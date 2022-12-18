package com.example.sitemateexampleapi.network

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

object Network {
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(LoggingInterceptor()).build()

    private const val SEARCH_KEY = "search"

    @JvmStatic
    fun fetchHttp(artist: String, song: String): String {
        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.lyrics.ovh")
            .addPathSegment("v1")
            .addPathSegment(artist)
            .addPathSegment(song)
            .build()
        val request = Request.Builder().get().url(httpUrl).build()
        val response = httpClient.newCall(request).execute()
        return if (response.isSuccessful) {
            val raw = response.body?.string()
            val result = Gson().fromJson(raw, Model.Result::class.java)
            result.lyrics
        } else {
            if (response.code == 404) {
                "We couldn't found lyrics for this song"
            } else {
                val raw = response.body?.string().orEmpty()
                val result = Gson().fromJson(raw, Model.Error::class.java)
                result.error
            }
        }
    }
}