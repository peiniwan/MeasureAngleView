package com.ly.measureangleview

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.util.*


class YAxixFormatter(var YType: Int) : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        var last = when (YType) {
            1 -> {
                "分  "
            }
            2 -> {
                "%  "
            }
            else -> "分  "
        }

        return Formatter().format("%.2f", value).toString() + last
    }

}