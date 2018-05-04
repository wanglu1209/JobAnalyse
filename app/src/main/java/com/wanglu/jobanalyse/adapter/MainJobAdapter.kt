package com.wanglu.jobanalyse.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.Utils.Utils
import com.wanglu.jobanalyse.model.MainJob

/**
 * Created by WangLu on 2018/4/18.
 */

class MainJobAdapter : BaseQuickAdapter<MainJob, BaseViewHolder>(R.layout.item_main_job) {

    override fun convert(helper: BaseViewHolder, item: MainJob) {
        helper.setText(R.id.tv_company_name, item.companyName)
                .setText(R.id.tv_salary, item.salary)
                .setText(R.id.tv_position_info, item.positionName)
                .setText(R.id.tv_work_info, item.workInfo)
                .setText(R.id.tv_company_info, item.companyInfo)
                .setText(R.id.tv_position_create_time, item.createTime)
        Utils.displayCircleAngleImg("http://" + item.companyLogoUrl!!, helper.getView<ImageView>(R.id.iv_company_logo))
    }
}
