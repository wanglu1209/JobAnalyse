package com.wanglu.jobanalyse.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.afollestad.materialdialogs.MaterialDialog
import com.orhanobut.hawk.Hawk
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.fragment.DistrictAnalyseFragment
import com.wanglu.jobanalyse.fragment.SalaryAnalyseFragment
import com.wanglu.jobanalyse.model.Job
import com.wanglu.jobanalyse.model.JobCategory
import kotlinx.android.synthetic.main.view_toolbar.*
import org.simple.eventbus.EventBus
import java.util.stream.Collectors

/**
 * Created by WangLu on 2018/4/26.
 */

class AnalyseActivity : BaseActivity() {


    private var mSelectedCity: String? = null
    private var mSelectedJob: String? = null
    private var mSelectedJobName: String? = null
    private var mViewCustomChoiceJobDialog: View? = null
    private var mSpinnerCategoryChoice: Spinner? = null
    private var mSpinnerJobChoice: Spinner? = null

    private var mJobCategoryList: List<JobCategory>? = null


    private var mJobList: List<Job>? = null // 工作list
    private var mChoiceJob: String? = null
    private var mChoiceJobName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyse)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        mSelectedCity = Hawk.get<String>(MainActivity.SELECTED_AREA)
        mSelectedJob = Hawk.get<String>(MainActivity.SELECTED_JOB)
        mSelectedJobName = Hawk.get<String>(MainActivity.SELECTED_JOB_NAME)

        val title = intent.getStringExtra("title")

        toolbar.post {
            val transaction = supportFragmentManager.beginTransaction()
            if (title == "薪资分析") {
                toolbar.title = "薪资分析"
                transaction.add(R.id.layout_fragment_container, SalaryAnalyseFragment())
            }
            if (title == "区域分析"){
                toolbar.title = "区域分析"
                transaction.add(R.id.layout_fragment_container, DistrictAnalyseFragment())
            }
            transaction.commit()
            toolbar.subtitle = mSelectedCity + " - " + mSelectedJobName
        }

        mViewCustomChoiceJobDialog = LayoutInflater.from(this).inflate(R.layout.view_custom_choice_job_dialog, null)
        mSpinnerCategoryChoice = mViewCustomChoiceJobDialog!!.findViewById(R.id.spinner_category_choice)
        mSpinnerJobChoice = mViewCustomChoiceJobDialog!!.findViewById(R.id.spinner_job_choice)

        mJobCategoryList = Hawk.get<List<JobCategory>>(MainActivity.JOB_CATEGORY)
        insertCategorySpinnerData()

    }


    private fun selectArea() {
        val areaList = resources.getStringArray(R.array.area)
        MaterialDialog.Builder(this)
                .title("请选择目标城市")
                .items(*areaList)
                .itemsCallbackSingleChoice(0) { _, _, which, _ ->
                    mSelectedCity = areaList[which]
                    toolbar.subtitle = mSelectedCity + " - " + mSelectedJobName
                    EventBus.getDefault().post(mSelectedCity!!, "change_city")
                    false
                }
                .positiveText("确定")
                .show()

    }

    private fun selectJob() {

        MaterialDialog.Builder(this)
                .title("请选择目标工作")
                .customView(mViewCustomChoiceJobDialog!!, false)
                .positiveText("确定")
                .onPositive { _, _ ->
                    mSelectedJobName = mChoiceJobName
                    mSelectedJob = mChoiceJob
                    toolbar.subtitle = mSelectedCity + " - " + mSelectedJobName
                    EventBus.getDefault().post(mSelectedJob!!, "change_job")
                }
                .show()

    }

    private fun insertCategorySpinnerData() {
        val data = mJobCategoryList!!.stream().map { it.categoryName }.collect(Collectors.toList())
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpinnerCategoryChoice!!.adapter = adapter

        mSpinnerCategoryChoice!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                mJobList = mJobCategoryList!![i].job
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
                mChoiceJobName = mJobList!![i].jobName
                mChoiceJob = mJobList!![i].jobId
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.job_switch, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_switch_city) {
            selectArea()
            return true
        }

        if (id == R.id.action_switch_job) {
            selectJob()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
