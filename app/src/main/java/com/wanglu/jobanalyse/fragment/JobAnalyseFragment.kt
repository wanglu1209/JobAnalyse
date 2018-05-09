package com.wanglu.jobanalyse.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.activity.AnalyseActivity
import kotlinx.android.synthetic.main.fragment_job_analyse.*

/**
 * Created by WangLu on 2018/4/24.
 */

class JobAnalyseFragment : BaseFragment(), View.OnClickListener{


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_analyse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layout_salary.setOnClickListener(this)
        layout_district.setOnClickListener(this)
        layout_create_time.setOnClickListener(this)
        layout_work_year.setOnClickListener(this)
        layout_company_info.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val i = Intent()
        var title = ""
        when (view.id) {
            R.id.layout_salary -> title = "薪资分析"
            R.id.layout_district -> title = "区域分析"
            R.id.layout_create_time -> {
            }
            R.id.layout_work_year -> {
            }
            R.id.layout_company_info -> {
            }
        }
        i.setClass(context!!, AnalyseActivity::class.java)
        i.putExtra("title", title)
        startActivity(i)
    }


}
