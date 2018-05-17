package com.wanglu.jobanalyse.fragment

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.orhanobut.hawk.Hawk
import com.wanglu.jobanalyse.CustomView.DoublySeekBar
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_job_setting.*
import org.simple.eventbus.EventBus


/**
 * Created by WangLu on 2018/5/15.
 */
class JobSetFragment : BaseFragment(), View.OnClickListener {

    private val salaryRange = mutableListOf<String>()

    private var selectSalaryView: View? = null
    private var seekBar: DoublySeekBar? = null
    private var selectSalaryText: TextView? = null
    private var selectBtn: Button? = null
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var selectAreaDialog: MaterialDialog? = null
    private var selectedArea: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        selectSalaryView = inflater.inflate(R.layout.view_select_salary, null)

        return layoutInflater.inflate(R.layout.fragment_job_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBottomSheetDialog = BottomSheetDialog(context!!)
        mBottomSheetDialog!!.setContentView(selectSalaryView)
        mBottomSheetBehavior = BottomSheetBehavior.from(selectSalaryView!!.parent as View)
        currentCity.text = "当前城市: " + Hawk.get<String>(MainActivity.SELECTED_AREA)
        val selectedSalary = Hawk.get<String>(MainActivity.SELECTED_SALARY)
        currentSalary.text = "当前薪资: " + (selectedSalary ?: "不限")

        val areaList = resources.getStringArray(R.array.area)
        selectAreaDialog = MaterialDialog.Builder(context!!)
                .title("请选择目标城市")
                .items(*areaList)
                .itemsCallbackSingleChoice(0) { _, _, which, _ ->
                    selectedArea = areaList[which]
                    Hawk.put<String>(MainActivity.SELECTED_AREA, selectedArea)
                    EventBus.getDefault().post(selectedArea, "selected_city")
                    currentCity.text = "当前城市: " + selectedArea
                    false
                }
                .positiveText("确定")
                .build()

        seekBar = selectSalaryView!!.findViewById(R.id.selectSalarySeekBar)
        selectSalaryText = selectSalaryView!!.findViewById(R.id.selectSalaryText)
        selectBtn = selectSalaryView!!.findViewById(R.id.selectSalaryBtn)

        salaryRange.add("不限")
        (1..30 step 2).mapTo(salaryRange) { it.toString() + "K" }
        (40..100 step 10).mapTo(salaryRange) { it.toString() + "K" }
        salaryRange.add("不限")

        seekBar!!.setText(salaryRange)

        seekBar!!.setOnTextChangedListener(object : DoublySeekBar.OnTextChangedListener {
            override fun onTextChange(value: DoublySeekBar.Value) {
                selectSalaryText!!.text = value.left + " - " + value.right
            }
        })

        selectBtn!!.setOnClickListener {
            var text = selectSalaryText!!.text
            if (text == "不限 - 不限") {
                text = "不限"
            }
            currentSalary.text = "当前薪资: " + text
            mBottomSheetDialog!!.dismiss()

            // 存储薪资范围
            Hawk.put(MainActivity.SELECTED_SALARY, text)
        }

        select_city_layout.setOnClickListener(this)
        select_job_layout.setOnClickListener(this)
        select_salary_layout.setOnClickListener(this)
    }

    private fun selectArea() {
        selectAreaDialog!!.show()
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.select_city_layout -> {
                selectArea()
            }
            R.id.select_job_layout -> {
            }
            R.id.select_salary_layout -> {
                mBottomSheetDialog!!.show()
            }
        }
    }


}