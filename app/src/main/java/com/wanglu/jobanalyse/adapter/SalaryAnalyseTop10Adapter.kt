package com.wanglu.jobanalyse.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.model.MainJob

/**
 * Created by WangLu on 2018/5/1.
 */

class SalaryAnalyseTop10Adapter : BaseQuickAdapter<MainJob, BaseViewHolder>(R.layout.item_salary_top) {

    override fun convert(helper: BaseViewHolder, item: MainJob) {
        helper.setText(R.id.tv_salary, item.salary)
                .setText(R.id.tv_company_name, item.companyName)
    }
}
