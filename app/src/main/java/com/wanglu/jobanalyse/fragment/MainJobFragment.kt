package com.wanglu.jobanalyse.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.hawk.Hawk
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.Utils.Utils
import com.wanglu.jobanalyse.activity.CommonWebActivity
import com.wanglu.jobanalyse.activity.MainActivity
import com.wanglu.jobanalyse.adapter.MainJobAdapter
import com.wanglu.jobanalyse.model.BaseModule
import com.wanglu.jobanalyse.model.MainJob
import com.wanglu.jobanalyse.network.CustomRequestCallback
import com.wanglu.jobanalyse.network.RetrofitFactory
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_job_list.*
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber
import retrofit2.Call
import java.util.*


/**
 * Created by WangLu on 2018/4/18.
 */

class MainJobFragment : BaseFragment() {


    private lateinit var mJobAdapter: MainJobAdapter
    private var page = 1

    private val mJobs = ArrayList<MainJob>()

    private var mEmptyView: View? = null

    private var mSelectedSalary: String = Hawk.get(MainActivity.SELECTED_SALARY, "")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)

        init()
    }

    private fun init() {
        mJobAdapter = MainJobAdapter()
        mEmptyView = LayoutInflater.from(context).inflate(R.layout.view_empty, null)
        mJobAdapter.emptyView = mEmptyView

        rvJob.layoutManager = LinearLayoutManager(context)
        rvJob.adapter = mJobAdapter
        toggleRefreshing(true)

        rvJob.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).size(1).build())

        getCityJobInfo()
        mJobAdapter.setOnLoadMoreListener({
            if (mJobs.size < 20) {
                mJobAdapter.loadMoreEnd()
            }
            page += 1
            getCityJobInfo()
        }, rvJob)

        refresh_layout.setOnRefreshListener {
            page = 1
            mJobs.clear()
            mJobAdapter.setEnableLoadMore(true)
            getCityJobInfo()
        }

        mJobAdapter.setOnItemClickListener { _, _, position ->
            val toWebIntent = Intent(activity, CommonWebActivity::class.java)
            toWebIntent.putExtra("title", mJobs[position].companyName + " - " + mJobs[position].positionName)
            toWebIntent.putExtra("url", "https://m.lagou.com/jobs/" + mJobs[position].positionId + ".html")
            startActivity(toWebIntent)
        }

    }

    private fun getCityJobInfo() {
        RetrofitFactory
                .requestApi
                .getCityJobInfoList(mSelectedCity, mSelectedJob, mSelectedSalary, page)
                .enqueue(object : CustomRequestCallback<List<MainJob>> {
                    override fun onParse(data: List<MainJob>) {
                        toggleRefreshing(false)
                        if (data.size < 20) {
                            mJobAdapter.loadMoreEnd()
                        }
                        mJobs.addAll(data)
                        mJobAdapter.setNewData(mJobs)
                    }


                    override fun onFailure(call: Call<BaseModule<List<MainJob>>>, t: Throwable) {
                        super.onFailure(call, t)
                        toggleRefreshing(false)
                        Utils.showSnackBar(refresh_layout, "获取失败")
                    }
                })
    }

    private fun toggleRefreshing(isShow: Boolean) {
        refresh_layout.post { refresh_layout.isRefreshing = isShow }
    }


    @Subscriber(tag = "back_top")
    fun onEventBackUp(s: String) {
        rvJob.scrollToPosition(0)
        reRequestJobInfo()
    }


    @Subscriber(tag = "selected_city")
    fun onEventSelectedCity(city: String) {
        mSelectedCity = city
        reRequestJobInfo()
    }


    @Subscriber(tag = "selected_job")
    fun onEventSelectedJob(job: String) {
        mSelectedJob = job
        reRequestJobInfo()
    }

    @Subscriber(tag = "change_salary")
    fun onEventChangedSalary(salary: String) {
        mSelectedSalary = salary
        reRequestJobInfo()
    }

    private fun reRequestJobInfo() {
        toggleRefreshing(true)
        page = 1
        mJobs.clear()
        mJobAdapter.setEnableLoadMore(true)
        getCityJobInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }


}
