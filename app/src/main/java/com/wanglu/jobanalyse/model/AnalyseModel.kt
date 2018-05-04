package com.wanglu.jobanalyse.model

/**
 * Created by WangLu on 2018/5/1.
 */

class AnalyseModel {
    var text: List<String>? = null
    var value: MutableList<Int>? = null
    var avgValue: List<Int>? = null
    var minValue: List<Int>? = null
    var maxValue: List<Int>? = null
    var multiLineValue: List<LineChartModel>? = null
}
