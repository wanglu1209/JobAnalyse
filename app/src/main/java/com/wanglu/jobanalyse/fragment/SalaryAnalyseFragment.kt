package com.wanglu.jobanalyse.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.Utils.ChartUtils
import com.wanglu.jobanalyse.adapter.SalaryAnalyseTop10Adapter
import com.wanglu.jobanalyse.model.AnalyseModel
import com.wanglu.jobanalyse.model.MainJob
import com.wanglu.jobanalyse.network.CustomRequestCallback
import com.wanglu.jobanalyse.network.RetrofitFactory
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_salary_analyse.*
import kotlinx.android.synthetic.main.view_loading.*
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber

/**
 * Created by WangLu on 2018/5/2.
 */

class SalaryAnalyseFragment : BaseFragment() {


    private lateinit var mAnalyseTop10Adapter: SalaryAnalyseTop10Adapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_salary_analyse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)

        init()
    }

    private fun init() {

        rv_salary_top.layoutManager = LinearLayoutManager(context)
        mAnalyseTop10Adapter = SalaryAnalyseTop10Adapter()
        mAnalyseTop10Adapter.setHeaderView(LayoutInflater.from(context).inflate(R.layout.view_salary_top_header, null))
        rv_salary_top.adapter = mAnalyseTop10Adapter
        rv_salary_top.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).size(1).build())
        rv_salary_top.setHasFixedSize(true)
        rv_salary_top.isNestedScrollingEnabled = false


        getPieSalaryAnalyse()
        getSalaryWeekGraph()
        getSalaryTop10()
    }


    // 获取饼图数据 工资分布情况
    private fun getPieSalaryAnalyse() {
        RetrofitFactory
                .requestApi
                .getSalaryDistributing(mSelectedCity, mSelectedJob)
                .enqueue(object : CustomRequestCallback<AnalyseModel> {
                    override fun onParse(data: AnalyseModel) {
                        ChartUtils.setPieDataAndStyle(pie_chart_salary, data, "工资分布情况")
                    }
                })
    }

    // 获取折线图数据 一周的工资分析   最高 最低 平均值
    private fun getSalaryWeekGraph() {
        RetrofitFactory
                .requestApi
                .getSalaryWeekGraph(mSelectedCity, mSelectedJob)
                .enqueue(object : CustomRequestCallback<AnalyseModel> {
                    override fun onParse(data: AnalyseModel) {
                        ChartUtils.setMultiLineChartData(line_chart_salary, data)
                    }
                })
    }


    // 获取工资排行10
    private fun getSalaryTop10() {
        RetrofitFactory
                .requestApi
                .getSalaryTop10(mSelectedCity, mSelectedJob)
                .enqueue(object : CustomRequestCallback<List<MainJob>> {
                    override fun onParse(data: List<MainJob>) {
                        mAnalyseTop10Adapter.setNewData(data)
                        loading_view.visibility = View.GONE
                    }
                })
    }

    @Subscriber(tag = "change_city")
    private fun onChangeCityEvent(city: String) {
        mSelectedCity = city
        loading_view.visibility = View.VISIBLE

        getPieSalaryAnalyse()
        getSalaryWeekGraph()
        getSalaryTop10()
    }

    @Subscriber(tag = "change_job")
    private fun onChangeJobEvent(job: String) {
        mSelectedJob = job
        loading_view.visibility = View.VISIBLE

        getPieSalaryAnalyse()
        getSalaryWeekGraph()
        getSalaryTop10()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
}
