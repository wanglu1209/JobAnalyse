package com.wanglu.jobanalyse.Utils

import android.support.design.widget.Snackbar
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import com.bumptech.glide.request.RequestOptions.bitmapTransform

/**
 * Created by WangLu on 2018/4/17.
 */

object Utils {

    /**
     * 显示SnackBar
     */
    fun showSnackBar(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * 加载网络图片
     */
    fun displayImg(url: String, iv: ImageView) {
        Glide.with(iv.context).load(url).into(iv)
    }

    fun displayCircleAngleImg(url: String, iv: ImageView) {
        Glide.with(iv.context).load(url).apply(bitmapTransform(RoundedCorners(15))).into(iv)
    }
}
