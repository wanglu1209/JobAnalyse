package com.wanglu.jobanalyse.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.hawk.Hawk
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.Utils.ChartUtils
import com.wanglu.jobanalyse.activity.MainActivity
import com.wanglu.jobanalyse.model.AnalyseModel
import com.wanglu.jobanalyse.network.CustomRequestCallback
import com.wanglu.jobanalyse.network.RetrofitFactory
import kotlinx.android.synthetic.main.fragment_district_analyse.*
import kotlinx.android.synthetic.main.view_loading.*
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber

/**
 * Created by WangLu on 2018/5/2.
 */
class DistrictAnalyseFragment : BaseFragment() {

    private lateinit var mSelectedCity: String
    private lateinit var mSelectedJob: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return layoutInflater.inflate(R.layout.fragment_district_analyse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mSelectedCity = Hawk.get<String>(MainActivity.SELECTED_AREA)
        mSelectedJob = Hawk.get<String>(MainActivity.SELECTED_JOB)

        getDistrictJobDistributing()
        getDistrictSalaryAvg()
    }


    /**
     * 获取区域工作分布
     */
    private fun getDistrictJobDistributing(){
        RetrofitFactory
                .requestApi
                .getDistrictJobDistributing(mSelectedCity, mSelectedJob)
                .enqueue(object : CustomRequestCallback<AnalyseModel>{
                    override fun onParse(data: AnalyseModel) {
                        ChartUtils.setPieDataAndStyle(districtPieChart, data, "各区域工作数量")
                        loading_view.visibility = View.GONE
                    }
                })
    }


    /**
     * 获取区域平均工资
     */
    private fun getDistrictSalaryAvg(){
        RetrofitFactory
                .requestApi
                .getDistrictSalaryAvg(mSelectedCity, mSelectedJob)
                .enqueue(object : CustomRequestCallback<AnalyseModel>{
                    override fun onParse(data: AnalyseModel) {
                        ChartUtils.setBarChartDataAndStyle(districtBarChart, data)
                        loading_view.visibility = View.GONE
                    }
                })
    }


    @Subscriber(tag = "change_city")
    private fun onChangeCityEvent(city: String) {
        mSelectedCity = city
        loading_view.visibility = View.VISIBLE
        getDistrictJobDistributing()
        getDistrictSalaryAvg()
    }

    @Subscriber(tag = "change_job")
    private fun onChangeJobEvent(job: String) {
        mSelectedJob = job
        loading_view.visibility = View.VISIBLE
        getDistrictJobDistributing()
        getDistrictSalaryAvg()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
}