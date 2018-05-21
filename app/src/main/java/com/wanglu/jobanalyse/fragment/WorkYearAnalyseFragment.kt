package com.wanglu.jobanalyse.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.Utils.ChartUtils
import com.wanglu.jobanalyse.model.AnalyseModel
import com.wanglu.jobanalyse.network.CustomRequestCallback
import com.wanglu.jobanalyse.network.RetrofitFactory
import kotlinx.android.synthetic.main.fragment_work_year_analyse.*
import kotlinx.android.synthetic.main.view_loading.*
import org.simple.eventbus.EventBus

/**
 * Created by WangLu on 2018/5/21.
 */
class WorkYearAnalyseFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return inflater.inflate(R.layout.fragment_work_year_analyse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestData()
    }

    private fun requestData() {
        RetrofitFactory
                .requestApi
                .getWorkYearDistributing(mSelectedCity, mSelectedJob)
                .enqueue(object : CustomRequestCallback<AnalyseModel> {
                    override fun onParse(data: AnalyseModel) {
                        ChartUtils.setBarChartDataAndStyle(workYearDistributingBarChart, data)
                    }
                })

        RetrofitFactory
                .requestApi
                .getWorkYearGraph(mSelectedCity, mSelectedJob)
                .enqueue(object : CustomRequestCallback<AnalyseModel> {
                    override fun onParse(data: AnalyseModel) {
                        ChartUtils.setMultiBarChartDataAndStyle(workYearContrastBarChart, data)
                        loading_view.visibility = View.GONE
                    }
                })
    }


}