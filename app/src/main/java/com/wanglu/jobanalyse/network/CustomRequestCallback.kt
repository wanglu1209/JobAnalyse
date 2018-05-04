package com.wanglu.jobanalyse.network

import android.util.Log

import com.wanglu.jobanalyse.model.BaseModule

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by WangLu on 2018/4/18.
 */

interface CustomRequestCallback<T> : Callback<BaseModule<T>> {
    override fun onResponse(call: Call<BaseModule<T>>, response: Response<BaseModule<T>>) {
        val b = response.body()
        if (b?.data == null) {
            onFailure(call, Throwable("Null"))
        } else {
            onParse(b.data!!)
        }
    }

    override fun onFailure(call: Call<BaseModule<T>>, t: Throwable) {
        Log.e("error", t.toString())
    }

    fun onParse(data: T)

}
