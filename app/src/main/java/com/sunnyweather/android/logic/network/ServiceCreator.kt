//package com.sunnyweather.android.logic.network
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object ServiceCreator {
//
//    private const val BASE_URL="https://api.caiyunapp.com/"
//
//    private val retrofit=Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    fun <T> create(serviceClass: Class<T>): T =retrofit.create(serviceClass)
//
//    inline fun <reified T> create():T =create(T::class.java)
//}看看这个！！
package com.sunnyweather.android.logic.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private const val BASE_URL = "https://api.caiyunapp.com/"

    private class QpsLimitInterceptor : Interceptor {

        private val lock = Any()
        private var lastRequestTime = 0L

        override fun intercept(chain: Interceptor.Chain): Response {
            var attempt = 0
            while (true) {
                synchronized(lock) {
                    val wait = MIN_INTERVAL_MILLIS - (System.currentTimeMillis() - lastRequestTime)
                    if (wait > 0) Thread.sleep(wait)
                    lastRequestTime = System.currentTimeMillis()
                }
                val response = chain.proceed(chain.request())
                // OkHttp 3.x 是 code() 方法，写成 .code 会像上次那样报 package-private
                if (response.code() != HTTP_TOO_MANY_REQUESTS || attempt >= MAX_RETRY) {
                    return response
                }
                response.close()
                attempt++
                Log.w("QpsLimitInterceptor", "429 限流，第 $attempt 次重试")
            }
        }

        companion object {
            private const val MIN_INTERVAL_MILLIS = 1500L
            private const val HTTP_TOO_MANY_REQUESTS = 429
            private const val MAX_RETRY = 2
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(QpsLimitInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}