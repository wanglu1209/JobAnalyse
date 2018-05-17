package com.wanglu.jobanalyse.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.afollestad.materialdialogs.MaterialDialog
import com.orhanobut.hawk.Hawk
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.fragment.JobAnalyseFragment
import com.wanglu.jobanalyse.fragment.JobSetFragment
import com.wanglu.jobanalyse.fragment.MainJobFragment
import com.wanglu.jobanalyse.model.Job
import com.wanglu.jobanalyse.model.JobCategory
import com.wanglu.jobanalyse.network.CustomRequestCallback
import com.wanglu.jobanalyse.network.RetrofitFactory
import kotlinx.android.synthetic.main.activity_main.*
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber
import java.util.stream.Collectors

class MainActivity : BaseActivity() {


    private var mSelectedArea: String? = "" // 选中的地区

    private var mViewCustomChoiceJobDialog: View? = null

    private var mSpinnerCategoryChoice: Spinner? = null
    private var mSpinnerJobChoice: Spinner? = null
    private var mJobCategoryList: List<JobCategory>? = null
    private var mHandler: Handler? = null
    private var mJobList: List<Job>? = null
    private var mSelectedJobId: String? = null
    private var mSelectedJobName: String? = null
    private var mTitleClickTime: Long = 0
    private var mMainJobFragment: MainJobFragment? = null
    private var mJobAnalyseFragment: JobAnalyseFragment? = null
    private var mJobSettingFragment: JobSetFragment? = null


    @SuppressLint("ResourceType", "HandlerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)


        navigation.setOnNavigationItemSelectedListener({ item ->
            val transaction = supportFragmentManager.beginTransaction()
            hideFragment(transaction)

            app_bar_layout.setExpanded(true)
            val id = item.itemId
            if (id == R.id.navigation_home) {
                iv_search.visibility = View.VISIBLE
                tv_title.text = mSelectedArea + " - " + mSelectedJobName
                showFragment(transaction, mMainJobFragment)

            }

            if (id == R.id.navigation_analyse) {
                tv_title.text = "工作分析"
                iv_search.visibility = View.GONE
                if (mJobAnalyseFragment == null) {
                    mJobAnalyseFragment = JobAnalyseFragment()
                    transaction.add(R.id.fragment_container, mJobAnalyseFragment).commit()
                } else
                    showFragment(transaction, mJobAnalyseFragment)
            }


            if (id == R.id.navigation_setting) {
                tv_title.text = "工作设置"
                iv_search.visibility = View.GONE
                if (mJobSettingFragment == null) {
                    mJobSettingFragment = JobSetFragment()
                    transaction.add(R.id.fragment_container, mJobSettingFragment).commit()
                } else
                    showFragment(transaction, mJobSettingFragment)
            }


            true
        })


        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (!Hawk.contains(SELECTED_JOB)) {
                    selectJob()
                }

            }
        }

        mViewCustomChoiceJobDialog = LayoutInflater.from(this).inflate(R.layout.view_custom_choice_job_dialog, null)
        mSpinnerCategoryChoice = mViewCustomChoiceJobDialog!!.findViewById(R.id.spinner_category_choice)
        mSpinnerJobChoice = mViewCustomChoiceJobDialog!!.findViewById(R.id.spinner_job_choice)
        mSpinnerCategoryChoice!!.prompt = "选择分类"
        mSpinnerJobChoice!!.prompt = "选择职位"


        if (!Hawk.contains(JOB_CATEGORY)) {
            getAllJobFactory()
        } else {
            mJobCategoryList = Hawk.get<List<JobCategory>>(JOB_CATEGORY)
            insertCategorySpinnerData()
        }


        if (!Hawk.contains(SELECTED_AREA))
            selectArea() // 选择地区
        else {
            mSelectedArea = Hawk.get<String>(SELECTED_AREA)
            tv_title.text = mSelectedArea

            tv_title.setOnClickListener({
                if (System.currentTimeMillis() - mTitleClickTime < 2000) {
                    EventBus.getDefault().post("123", "back_top")
                } else {
                    mTitleClickTime = System.currentTimeMillis()
                }
            })

            if (!Hawk.contains(SELECTED_JOB)) {
                selectJob()
            } else {
                mSelectedJobId = Hawk.get<String>(SELECTED_JOB)
                mSelectedJobName = Hawk.get<String>(SELECTED_JOB_NAME)
                if (mMainJobFragment == null) {
                    mMainJobFragment = MainJobFragment()
                    val b = Bundle()
                    b.putString("city", mSelectedArea)
                    b.putString("job_id", mSelectedJobId)
                    tv_title.text = mSelectedArea + " - " + mSelectedJobName
                    val transaction = supportFragmentManager.beginTransaction()
                    mMainJobFragment!!.arguments = b
                    transaction.add(R.id.fragment_container, mMainJobFragment).commit()
                }
            }
        }


        iv_search.setOnClickListener({
            val toSearchIntent = Intent(this, SearchJobActivity::class.java)
            toSearchIntent.putExtra("selected_city", mSelectedArea)
            toSearchIntent.putExtra("selected_job", mSelectedJobId)
            startActivity(toSearchIntent)
        })

    }

    private fun selectArea() {
        val areaList = resources.getStringArray(R.array.area)
        MaterialDialog.Builder(this)
                .title("请选择目标城市")
                .items(*areaList)
                .itemsCallbackSingleChoice(0) { _, _, which, _ ->
                    mSelectedArea = areaList[which]
                    if (!TextUtils.isEmpty(mSelectedJobName))
                        tv_title.text = mSelectedArea + " - " + mSelectedJobName
                    else
                        tv_title.text = mSelectedArea
                    Hawk.put<String>(SELECTED_AREA, mSelectedArea)
                    mHandler!!.sendEmptyMessage(1)
                    EventBus.getDefault().post(mSelectedArea, "selected_city")
                    false
                }
                .cancelable(Hawk.contains(SELECTED_AREA))
                .positiveText("确定")
                .show()

    }

    @SuppressLint("SetTextI18n")
    private fun selectJob() {

        MaterialDialog.Builder(this)
                .title("请选择目标工作")
                .customView(mViewCustomChoiceJobDialog!!, false)
                .positiveText("确定")
                .onPositive { _, _ ->

                    if (mMainJobFragment == null) {
                        mMainJobFragment = MainJobFragment()
                        val b = Bundle()
                        b.putString("city", mSelectedArea)
                        b.putString("job_id", mSelectedJobId)
                        tv_title.text = mSelectedArea + " - " + mSelectedJobName
                        val transaction = supportFragmentManager.beginTransaction()
                        mMainJobFragment!!.arguments = b
                        transaction.add(R.id.fragment_container, mMainJobFragment).commit()
                    } else {
                        EventBus.getDefault().post(mSelectedJobId, "selected_job")
                    }
                }
                .cancelable(Hawk.contains(SELECTED_JOB))
                .show()

    }


    /**
     * 获取所有的工作分类
     */
    private fun getAllJobFactory() {
        RetrofitFactory
                .requestApi
                .allJobCategory
                .enqueue(object : CustomRequestCallback<List<JobCategory>> {
                    override fun onParse(data: List<JobCategory>) {
                        Hawk.put(JOB_CATEGORY, data)
                        mJobCategoryList = data
                        insertCategorySpinnerData()
                    }
                })

    }

    private fun insertCategorySpinnerData() {
        val data = mJobCategoryList!!.stream().map { it.categoryName }.collect(Collectors.toList())
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpinnerCategoryChoice!!.adapter = adapter

        mSpinnerCategoryChoice!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                Log.d("TAG", mJobCategoryList!![i].categoryName)
                mJobList = mJobCategoryList!![i].job!!
                insertJobSpinnerData()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }


    }

    private fun insertJobSpinnerData() {
        val data = mJobList!!.stream().map { it.jobName }.collect(Collectors.toList())
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        mSpinnerJobChoice!!.adapter = adapter
        mSpinnerJobChoice!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                mSelectedJobName = mJobList!![i].jobName!!
                mSelectedJobId = mJobList!![i].jobId!!
                Hawk.put<String>(SELECTED_JOB, mSelectedJobId)
                Hawk.put<String>(SELECTED_JOB_NAME, mSelectedJobName)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
    }


    private fun hideFragment(transaction: FragmentTransaction) {
        transaction.hide(mMainJobFragment)
        if (mJobAnalyseFragment != null)
            transaction.hide(mJobAnalyseFragment)

        if (mJobSettingFragment != null)
            transaction.hide(mJobSettingFragment)


    }

    private fun showFragment(transaction: FragmentTransaction, fragment: Fragment?) {
        transaction.show(fragment)
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {

        val SELECTED_AREA = "selected_area"
        val JOB_CATEGORY = "job_category"
        val SELECTED_JOB = "selected_job"
        val SELECTED_JOB_NAME = "selected_job_name"
        val SELECTED_SALARY = "selected_salary"
    }

    @Subscriber(tag = "selected_city")
    fun onEventSelectedCity(city: String) {
        mSelectedArea = city
    }


    @Subscriber(tag = "selected_job")
    fun onEventSelectedJob(job: String) {
        mSelectedJobName = job
    }

}
