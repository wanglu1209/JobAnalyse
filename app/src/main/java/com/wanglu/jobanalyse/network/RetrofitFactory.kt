package com.wanglu.jobanalyse.network

import com.orhanobut.logger.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by WangLu on 2018/4/18.
 */

object RetrofitFactory {

//     private const val BASE_URL = "http://192.168.164.120:5020" // 2.4
    private const val BASE_URL = "http://192.168.1.3:5020" // 家

    private val mRetrofit: Retrofit

    val requestApi: RequestApi

    init {
        val interceptor = HttpLoggingInterceptor { message -> Logger.e(message) }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(25, TimeUnit.SECONDS)
                .build()
        mRetrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

        requestApi = mRetrofit.create(RequestApi::class.java)
    }

}
