package com.ly.measureangleview

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var lineChart = findViewById<LineChart>(R.id.lineChart)
        initChart(lineChart)


//        var llRoot = findViewById<LinearLayout>(R.id.llRoot)
//        var textView = findViewById<TextView>(R.id.textView)
//
//        var measureView = findViewById<MeasureAngleView>(R.id.measureView)
////        var measureView2 = findViewById<MeasuerAngleView3>(R.id.measureView2)
////        measureView2.setImageResource(R.drawable.abc)
//
//        measureView.setOnAngleSelectedListener(object : MeasureAngleView.OnAngleSelectedListener {
//            override fun onAngleSelected(angle: Int) {
//                Log.d("measureView", "Angle selected: " + angle)
//
//            }
//        })
//        measureView.setImageResource(R.drawable.abc)
//        measureView.post {
//            var d = resources.getDrawable(R.drawable.abc)
//            var height =
//                d.intrinsicHeight.toFloat() / d.intrinsicWidth.toFloat() * measureView.measuredWidth
//            var cha = (measureView.measuredHeight - height) / 2
//            val points = arrayOfNulls<Point>(4)
//            points[0] = Point(0, cha.toInt())
//            points[1] = Point(measureView.measuredWidth, cha.toInt())
//            points[2] = Point(measureView.measuredWidth, (cha + height).toInt())
//            points[3] = Point(0, (cha + height).toInt())
//
//            points[2] = Point(measureView.measuredWidth, (measureView.measuredHeight).toInt())
//            points[3] = Point(0, (measureView.measuredHeight).toInt())
//            measureView.mCropPoints = points
//        }
//
//        textView.setOnClickListener {
//            measureView.reset()
////            measureView.setShowLine(700,250,450,250,500,475)
//            measureView.setShowLine(1332, 2916, 1476, 1728, 3276, 1116)
//            measureView.setShowLine(0, 0, 200, 0, 200, 400)
//        }
    }

    val xValues = ArrayList<Float>()
    var isSingleLine = true

    private fun initChart(lineChart: LineChart) {
        lineChart.isLogEnabled = true

        val lineMarkerView: MarkerView?
        lineMarkerView = FollowupMarkerView(this, R.layout.medrecord_line_markerview_followup)
        lineChart.marker = lineMarkerView
        lineMarkerView.chartView = lineChart
//        val tvContent1 = lineMarkerView.findViewById<TextView>(R.id.tvContent1)
//        val tvContent2 = lineMarkerView.findViewById<TextView>(R.id.tvContent2)
//        val tvContent3 = lineMarkerView.findViewById<TextView>(R.id.tvContent3)
//        tvContent3.setOnClickListener {
//            Toast.makeText(this@MainActivity,"dddd",Toast.LENGTH_SHORT).show()
//        }

        val lineChartManager = FollowupLineChartManager(this,lineChart)
        //设置x轴的数据
        val xValuesMonth = ArrayList<String>()
        //设置y轴的数据()
        val yValues = ArrayList<Float>()

        for (index in 0..1) {
            xValues.add(index.toFloat())
        }
        xValuesMonth.add("2010.01.23\n"+"节点名字"+1)
        xValuesMonth.add("2010.01.23\n"+"节点名字"+2)
//        xValuesMonth.add("2010.01.23\n"+"节点名字"+3)
//        xValuesMonth.add("2010.01.23\n"+"节点名字"+4)
//        xValuesMonth.add("2010.01.23\n"+"节点名字"+5)
//        xValuesMonth.add("2010.01.23\n"+"节点名字"+6)

        yValues.add(0.8f)
        yValues.add(0.12f)
//        yValues.add(-127f)
//        yValues.add(55f)
//        yValues.add(16f)
//        yValues.add(67f)

//        for (index in 0..2) {
////            xValuesMonth.add("2010.01.23\n"+"术后" + index.toFloat().toInt().toString() + "月吧")
//            xValuesMonth.add("2010.01.23\n"+"节点名字"+index)
//            if (index == 30 || index == 31 || index == 32  || index == 37) {
//                yValues.add(-127f)
//            } else {
//                val xxx = (Math.random() * 100).toInt().toFloat()
//                yValues.add(xxx)
//            }
//        }
        var YType=0  ///总分、平均分、百分制
        lineChartManager.showSingleLineChart(
            xValuesMonth, xValues, yValues, Color.parseColor("#146aff"),YType
        )

        //多条线
//        val yValues2 = ArrayList<Float>()
//        val yValuess = ArrayList<List<Float>>()
//        yValuess.add(yValues)
//        for (index in 20..40) {
//            val xxx = (Math.random() * 100).toInt().toFloat()
//            yValues2.add(xxx)
//        }
//        yValuess.add(yValues2)
//        isSingleLine=false
//        val colours = ArrayList<Int>()
//        colours.add(Color.parseColor("#146aff"))
//        colours.add(Color.parseColor("#FF91F3D1"))
//        val names = ArrayList<String>()
//        names.add("平均住院日")
//        names.add("术前平均住院日")
//        lineChartManager.showLineChart(
//            xValuesMonth, xValues, yValuess,
//            names, colours
//        )


        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                var selectX = e?.x?.toInt() ?: 0
                if (isSingleLine) {
//                    tvContent1.text="总分："+yValues[selectX].toInt().toString().plus("分")
                    lineChartManager.notifyData(selectX)
                }
            }
        })

    }

}