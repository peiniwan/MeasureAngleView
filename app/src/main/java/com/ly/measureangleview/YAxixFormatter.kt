package com.ly.measureangleview

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.util.ArrayList


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

        return value.toInt().toString() + last
    }

}