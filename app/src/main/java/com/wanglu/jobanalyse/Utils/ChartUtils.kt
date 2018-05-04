package com.wanglu.jobanalyse.Utils

import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler
import com.wanglu.jobanalyse.model.AnalyseModel
import java.text.DecimalFormat


/**
 * Created by WangLu on 2018/5/2.
 */
object ChartUtils {
    // 设置PieChart属性和值
    fun setPieDataAndStyle(pieChart: PieChart, data: AnalyseModel, centerText: String) {

        pieChart.setExtraOffsets(0f, 10f, 0f, 0f)   // 设置偏移
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)  // 设置只显示百分比
        pieChart.isRotationEnabled = false  // 不允许拖动旋转
        pieChart.centerText = centerText
        pieChart.setCenterTextSize(14f)
        val l = pieChart.legend
        l.textSize = 14f
        l.xEntrySpace = 10f
        l.yOffset = 10f
        l.form = Legend.LegendForm.LINE
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM // 设置图例位置
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER // 设置图例位置
        l.orientation = Legend.LegendOrientation.HORIZONTAL // 设置图例显示方向

        val pieXData = data.text
        val entries = (0 until data.value!!.size).map { PieEntry(data.value!![it].toFloat(), pieXData!![it]) }
        val dataSet = PieDataSet(entries, "")

        dataSet.sliceSpace = 2f
        val colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        colors.addAll(ColorTemplate.PASTEL_COLORS.toList())

        dataSet.colors = colors

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(11f)
        pieData.setValueTextColor(Color.WHITE)
        pieChart.data = pieData
        pieChart.setDrawEntryLabels(false)
        pieChart.highlightValues(null)
        pieChart.invalidate()

    }

    /**
     * 设置柱状图数据和样式
     */
    fun setBarChartDataAndStyle(barChart: BarChart, data: AnalyseModel){
        barChart.description.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setDrawValueAboveBar(true)
        barChart.setTouchEnabled(false)
        val xAxis = barChart.xAxis
        xAxis.textSize = 12f
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.granularity = 1f // only intervals of 1 day
        xAxis.valueFormatter = IndexAxisValueFormatter(data.text)
        xAxis.setDrawGridLines(false)
        barChart.axisLeft.textSize = 12f

        val rightAxis = barChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false

        val l = barChart.legend
        l.textSize = 14f
        l.xEntrySpace = 10f
        l.yOffset = 10f
        l.form = Legend.LegendForm.LINE
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM // 设置图例位置
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER // 设置图例位置
        l.orientation = Legend.LegendOrientation.HORIZONTAL // 设置图例显示方向

        val entries = (0 until data.value!!.size).map { BarEntry(it.toFloat(), data.value!![it].toFloat()) }
        val dataSet = BarDataSet(entries, "")
        val colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        colors.addAll(ColorTemplate.PASTEL_COLORS.toList())
        dataSet.colors = colors
        val barData = BarData(dataSet)

        barData.setValueTextSize(11f)
        barData.setValueTextColor(Color.BLACK)
        barChart.data = barData
        barChart.highlightValues(null)
        barChart.invalidate()
    }


    fun setMultiLineChartData(lineChart: LineChart, data: AnalyseModel) {

        lineChart.axisRight.isEnabled = false  //隐藏右Y轴
        lineChart.setExtraOffsets(5f, 5f, 5f, 5f)
        lineChart.setTouchEnabled(false)
        lineChart.axisLeft.setValueFormatter { value, _ -> DecimalFormat("#").format(value.toDouble()) + "   " }
        lineChart.axisLeft.textSize = 12f
        lineChart.xAxis.textSize = 12f
        lineChart.description.isEnabled = false


        val l = lineChart.legend
        l.textSize = 14f
        l.xEntrySpace = 10f
        l.yOffset = 10f
        l.form = Legend.LegendForm.LINE
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM // 设置图例位置

        val dataSets = ArrayList<ILineDataSet>()


        val colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        colors.addAll(ColorTemplate.PASTEL_COLORS.toList())

        for((i, d) in data.multiLineValue!!.withIndex()){
            val values = ArrayList<Entry>()
            d.value!!.indices.mapTo(values) { Entry(it.toFloat(), d.value!![it].toFloat()) }
            val dataSet = LineDataSet(values, d.text)
            dataSet.color = colors[i]
            dataSet.valueTextSize = 12f
            dataSet.valueFormatter = MyValueFormatter()
            dataSet.setCircleColor(colors[i])
            dataSets.add(dataSet)
        }

        val lineData = LineData(dataSets)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(data.text)

        lineChart.data = lineData
        lineChart.invalidate()
    }

    class MyValueFormatter : IValueFormatter {

        private val mFormat: DecimalFormat = DecimalFormat("#") // 不保留小数

        override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
            return mFormat.format(value.toDouble())
        }
    }



}