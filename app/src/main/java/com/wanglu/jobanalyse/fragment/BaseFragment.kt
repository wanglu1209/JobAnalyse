package com.wanglu.jobanalyse.fragment

import android.support.v4.app.Fragment

import com.orhanobut.hawk.Hawk
import com.wanglu.jobanalyse.activity.MainActivity

/**
 * Created by WangLu on 2018/4/19.
 */

open class BaseFragment : Fragment() {

    var mSelectedCity: String
    var mSelectedJob: String

    init {
        mSelectedCity = Hawk.get<String>(MainActivity.SELECTED_AREA)
        mSelectedJob = Hawk.get<String>(MainActivity.SELECTED_JOB)
    }




}
