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

    private const val BASE_URL = "http://www.d3collection.cn:5020"

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
