package com.ly.measureangleview

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewTreeObserver
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.*


/**
 * 线形图管理类
 */
class FollowupLineChartManager(private val lineChart: LineChart) {
    val leftAxis: YAxis   //左边Y轴
    val rightAxis: YAxis  //右边Y轴
    val xAxis: XAxis      //X轴
    var xValues = ArrayList<Float>()
    var yValues = ArrayList<Float>()

    /**是否已经设置Y轴自动缩放*/
    var hasSetYScalseAuto = false


    init {
        leftAxis = lineChart.axisLeft
        rightAxis = lineChart.axisRight
        xAxis = lineChart.xAxis
    }


    /**
     * 初始化LineChart
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initLineChart() {
        //XY轴的设置
        //false就不显示X轴了
        xAxis.setDrawAxisLine(false)
        //X轴设置显示位置在底部
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.granularity = 1f
        xAxis.textColor = Color.parseColor("#666666")
        xAxis.textSize = 11f
        //设置和X轴垂直的线是否显示,网格
        xAxis.setDrawGridLines(false)
        //X轴线的颜色
//        xAxis.axisLineColor = Color.parseColor("#D8D8D8")
//        xAxis.axisLineWidth = 1f
//        xAxis.setLabelCount(xValues.size, false)
        xAxis.yOffset = 15f  //距离y轴距离
//        xAxis.xOffset=-20f  //不起作用
        xAxis.spaceMax = 0.2f //解决x最后显示不全问题

        lineChart.setDrawGridBackground(false)
        lineChart.setNoDataText("暂无数据")
        //是否显示整个图标外框框
        lineChart.setDrawBorders(false)
        //是否可拖拽
        lineChart.isDragEnabled = true
        //是否显示description
        lineChart.description.isEnabled = false
        //图表与legend的距离
//        lineChart.extraTopOffset=30f
        lineChart.extraRightOffset = 10f
        lineChart.extraBottomOffset = 2 * 11f  //x轴字体的2倍
        // 是否可以缩放 x和y轴, 默认是true
        lineChart.setScaleEnabled(false)
//        lineChart.setHighlightPerTapEnabled(false)
//        lineChart.isHighlightPerDragEnabled=false



        //右边Y轴
        rightAxis.isEnabled = false
        //和Y轴垂直网格线颜色
        leftAxis.gridColor = Color.parseColor("#ededed")
        leftAxis.gridLineWidth = 1f
        //Y轴颜色
        leftAxis.axisLineColor = Color.parseColor("#D8D8D8")
        leftAxis.axisLineWidth = 1f
        leftAxis.enableGridDashedLine(10f, 10f, 200f)
        leftAxis.setLabelCount(6, false)//5段
        leftAxis.granularity = 1f
        //保证Y轴从0开始，不然会上移一点
        leftAxis.axisMinimum = -1f //TODO 设置最小值-10,要不然会显示出断开的-数
//        leftAxis.axisMaximum=120f
        leftAxis.setDrawAxisLine(false)

        //图例（哪条线什么颜色） 标签 设置
        val legend = lineChart.legend
        legend.isEnabled = false  //TODO 设置不显示，多条线要显示
        //显示位置
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        //形状
        legend.form = Legend.LegendForm.CIRCLE
        legend.formSize = 6f
        legend.textSize = 12f
        legend.textColor = Color.parseColor("#333333")
        //各图例之间的水平间距，默认6f
        legend.xEntrySpace = 24f

        lineChart.addOnLayoutChangeListener(object : ViewTreeObserver.OnGlobalLayoutListener,
            View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                if (!hasSetYScalseAuto) {
                    lineChart.isAutoScaleMinMaxEnabled = false
                    lineChart.invalidate()
                    hasSetYScalseAuto = true
                }
            }

            override fun onGlobalLayout() {
                if (!hasSetYScalseAuto) {
                    lineChart.isAutoScaleMinMaxEnabled = false
                    lineChart.invalidate()
                    hasSetYScalseAuto = true
                }
            }
        })

    }

    /**
     * 初始化曲线 每一个LineDataSet代表一条线
     *
     * @param lineDataSet
     * @param color
     * @param mode        折线图是否填充
     */
    private fun initLineDataSet(lineDataSet: LineDataSet, color: Int, mode: Boolean) {
        lineDataSet.color = color
        lineDataSet.setCircleColor(color)
        //设置线宽
        lineDataSet.lineWidth = 3f
        //是否绘制曲线上值的点
        lineDataSet.setDrawCircles(true)
        //设置圆心半径
        lineDataSet.circleRadius = 3f
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.valueTextSize = 10f
        //设置折线图填充
        lineDataSet.setDrawFilled(mode)
        lineDataSet.formLineWidth = 1f
//        lineDataSet.formSize = 15f
        //线模式为圆滑曲线（默认折线）
        lineDataSet.mode = LineDataSet.Mode.LINE_DISCONNECT

        lineDataSet.enableDashedHighlightLine(15f, 15f, 0f)//点击后的高亮线的显示样式
        //设置点击交点后显示高亮线宽
        lineDataSet.highlightLineWidth = 0.5f
        //是否禁用点击高亮线
        lineDataSet.isHighlightEnabled = true
        //点击时是否绘制水平提示线
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        //设置点击交点后显示交高亮线的颜色
        lineDataSet.highLightColor = Color.parseColor("#1a71ff")
        //设置显示值的文字大小
        lineDataSet.valueTextSize = 12f
        //设置禁用范围背景填充
        lineDataSet.setDrawFilled(false)
        lineDataSet.setDrawValues(true)
        lineDataSet.valueTypeface = Typeface.DEFAULT_BOLD
//        lineDataSet.setDrawHighlightIndicators(true)
    }

    /**
     * 展示折线图(一条)
     *
     * @param xAxisValues
     * @param yAxisValues
     * @param color
     */
    fun showSingleLineChart(
        xMonths: ArrayList<String>,
        xAxisValues: ArrayList<Float>,
        yAxisValues: ArrayList<Float>,
        color: Int,
        YType: Int
    ) {
        xValues = xAxisValues
        yValues = yAxisValues
        initLineChart()
        var selectCount = 5f
        //todo 最后一个有值的
        notifyData(selectCount.toInt())
        xAxis.valueFormatter = XAxixYearMonthFormatter(xMonths)
        leftAxis.valueFormatter = YAxixFormatter(YType)
        val entries = ArrayList<Entry>()
        for (i in xAxisValues.indices) {
            entries.add(Entry(xAxisValues[i], yAxisValues[i]))
        }
        // 每一个LineDataSet代表一条线
        val lineDataSet = LineDataSet(entries, "")
        initLineDataSet(lineDataSet, color, true)


        //是否绘制线上的数值
        lineDataSet.setDrawValues(false)
        //todo 多点展示？？
        //只有一条数据时，只显示一个点
//        if (entries.size == 1) {
//            lineDataSet.setDrawCircles(true)
//            //是否有洞 true-有 false-实心圆
//            lineDataSet.setDrawCircleHole(false)
//        } else {
//            lineDataSet.setDrawCircles(true)
//        }
        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet)
        val data = LineData(dataSets)
        //是否绘制线上的数值
        lineDataSet.setDrawValues(true)
        lineChart.data = data
        lineChart.highlightValue(selectCount, 0, false)//默认选中哪个，从0开始

        lineChart.setVisibleXRange(0f, 5.4f)
        lineChart.invalidate()


    }


    /**
     * 展示线性图(多条)
     *
     * @param xAxisValues
     * @param yAxisValues 多条曲线Y轴数据集合的集合
     * @param labels
     * @param colours
     */
    fun showLineChart(
        xMonths: ArrayList<String>,
        xAxisValues: List<Float>,
        yAxisValues: List<List<Float>>,
        labels: List<String>,
        colours: List<Int>
    ) {
        initLineChart()
        xAxis.valueFormatter = XAxixYearMonthFormatter(xMonths)
        leftAxis.valueFormatter = YAxixFormatter(-1)
        val dataSets = ArrayList<ILineDataSet>()
        for (i in yAxisValues.indices) {
            val entries = ArrayList<Entry>()
            var j = 0
            while (j < yAxisValues[i].size) {
                if (j >= xAxisValues.size) {
                    j = xAxisValues.size - 1
                }
                entries.add(Entry(xAxisValues[j], yAxisValues[i][j]))
                j++
            }
            val lineDataSet = LineDataSet(entries, labels[i])
            //是否绘制线上的数值
            lineDataSet.setDrawValues(false)
            initLineDataSet(lineDataSet, colours[i], false)
            dataSets.add(lineDataSet)
        }
        val lineData = LineData(dataSets)

        //Y轴是否自动缩放
        lineChart.isAutoScaleMinMaxEnabled = false
        lineChart.data = lineData
        lineChart.setVisibleXRange(0f, 5.4f)
        lineChart.moveViewToX(xAxisValues.size - 6f)
        lineChart.invalidate()
    }

    fun notifyData(selectX: Int) {
        var colors = mutableListOf<Int>()
        for (index in xValues) {
            colors.add(Color.parseColor("#666666"))
        }
        colors[selectX] = Color.parseColor("#146AFF")
        lineChart.setXAxisRenderer(
            ColoredLabelXAxisRenderer(
                lineChart.getViewPortHandler(),
                lineChart.getXAxis(),
                lineChart.getTransformer(YAxis.AxisDependency.LEFT),
                colors
            )
        )
//        lineChart.rendererLeftYAxis
        lineChart.notifyDataSetChanged()
    }


}
