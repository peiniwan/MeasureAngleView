package com.ly.measureangleview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView


class MeasuerAngleView2 : AppCompatImageView {
    private var mContext: Context
    private lateinit var mLinePaint: Paint
    private lateinit var mExtendPaint: Paint

    private var downY: Float = 0f
    private var downX: Float = 0f
    var lastX = 0f
    var lastY = 0f
    var list: ArrayList<FloatArray> = ArrayList()
    lateinit var mHandles: Array<Point?>
    var extendPoint: Point? = null
    var middlePoint: Point? = null
    var intersect = true
    var isExtend = false
    var slope2 = 0.0
    var connectionRule = 0
    var k1 = Float.MAX_VALUE
    var k2 = Float.MAX_VALUE
    var xuPoint1: Point? = null
    var xuPoint2: Point? = null
    private lateinit var mAcrPaint: Paint
    var sweepAngle = 0


    constructor(context: Context) : super(context) {
        mContext = context
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        initialize()
        mHandles = arrayOfNulls(4)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        mContext = context
        initialize()
    }

    private fun initialize() {
        mLinePaint = Paint()
        mLinePaint.isAntiAlias = true
        mLinePaint.color = Color.parseColor("#146BFF")
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeWidth = 10f
        mLinePaint.setStrokeCap(Paint.Cap.ROUND)
        mLinePaint.setStrokeJoin(Paint.Join.BEVEL)

        mExtendPaint = Paint()
        mExtendPaint.isAntiAlias = true
        mExtendPaint.color = Color.WHITE
        mExtendPaint.style = Paint.Style.STROKE
        mExtendPaint.strokeWidth = 10f
        mExtendPaint.setStrokeCap(Paint.Cap.ROUND)
        mExtendPaint.pathEffect = DashPathEffect(floatArrayOf(14f, 14f), 1f)

        mAcrPaint = Paint()
        mAcrPaint.style = Paint.Style.STROKE
        mAcrPaint.isAntiAlias = true
        mAcrPaint.color = Color.parseColor("#146BFF")
        mAcrPaint.strokeWidth = 10f
        mAcrPaint.pathEffect = DashPathEffect(floatArrayOf(14f, 14f), 1f)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (list.size > 1) {
            connectionRule = 1
            when (connectionRule) {
                0 -> {
                    drawExtendLines(canvas)
                }
                1 -> {
                    drawTranslationLine(canvas)
                }
            }
        }
        if (list.size <= 1) {
            canvas?.drawLine(downX, downY, lastX, lastY, mLinePaint)
        }
        for (i in list.indices) {
            val data = list[i]
            canvas?.drawLine(data[0], data[1], data[2], data[3], mLinePaint)
        }
    }

    private fun drawTranslationLine(canvas: Canvas?) {
        if (middlePoint != null && xuPoint1 != null && xuPoint2 != null) {
            canvas?.drawLine(
                xuPoint1!!.x.toFloat(),
                xuPoint1!!.y.toFloat(),
                middlePoint!!.x.toFloat(),
                middlePoint!!.y.toFloat(),
                mExtendPaint
            )
            canvas?.drawLine(
                xuPoint2!!.x.toFloat(),
                xuPoint2!!.y.toFloat(),
                middlePoint!!.x.toFloat(),
                middlePoint!!.y.toFloat(),
                mExtendPaint
            )
            var startAngle = -getCurrentAngle1()
            var distanceMiddle = 50f  //弧半径
            var rectF = RectF(
                middlePoint?.x!! - distanceMiddle,
                middlePoint?.y!! - distanceMiddle,
                middlePoint?.x!! + distanceMiddle,
                middlePoint?.y!! + distanceMiddle
            )
            canvas?.drawArc(
                rectF,
                startAngle.toFloat(),
                sweepAngle.toFloat(),
                false,
                mAcrPaint!!
            ) // 绘制扇形
        }
    }

    fun getCurrentAngle1(): Int {
        var shuiping = Point(mHandles[1]!!.x!! + 100, mHandles[1]!!.y!!)
        val AB = Point(
            mHandles[1]!!.x - mHandles[0]!!.x,
            mHandles[1]!!.y - mHandles[0]!!.y
        )
        val CB = Point(
            mHandles[1]!!.x - shuiping.x,
            mHandles[1]!!.y - shuiping.y
        )
        val dot = (AB.x * CB.x + AB.y * CB.y).toDouble()
        val cross = (AB.x * CB.y - AB.y * CB.x).toDouble()
        val alpha = Math.atan2(cross, dot)
        var xxx = Math.floor(alpha * 180.0 / Math.PI + 0.5).toInt()
        return Math.abs(xxx)
    }

    private fun drawExtendLines(canvas: Canvas?) {
        if (extendPoint != null && !isExtend) {
            var y = extendPoint!!.y - 60//交点在线下面+
//            var y = k1 * (x - extendPoint!!.x) + extendPoint!!.y
            var x = (y - extendPoint!!.y) / k1 + extendPoint!!.x
            canvas?.drawLine(
                mHandles[1]!!.x.toFloat(),
                mHandles[1]!!.y.toFloat(),
                x.toFloat(),
                y.toFloat(),
                mExtendPaint
            )
            x = (y - extendPoint!!.y) / k2 + extendPoint!!.x
            canvas?.drawLine(
                mHandles[3]!!.x.toFloat(),
                mHandles[3]!!.y.toFloat(),
                x.toFloat(),
                y.toFloat(),
                mExtendPaint
            )

//            canvas?.drawLine(
//                mHandles[1]!!.x.toFloat(),
//                mHandles[1]!!.y.toFloat(),
//                extendPoint!!.x.toFloat(),
//                extendPoint!!.y.toFloat(),
//                mExtendPaint
//            )
//            canvas?.drawLine(
//                mHandles[3]!!.x.toFloat(),
//                mHandles[3]!!.y.toFloat(),
//                extendPoint!!.x.toFloat(),
//                extendPoint!!.y.toFloat(),
//                mExtendPaint
//            )
            isExtend = true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> return onDown(event)
            MotionEvent.ACTION_MOVE -> return onMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (list.size == 0) {
                    mHandles[0] = Point(downX.toInt(), downY.toInt())
                    mHandles[1] = Point(event?.x.toInt(), event?.y.toInt())
                    val data = floatArrayOf(
                        downX,
                        downY,
                        event?.x.toFloat(),
                        event?.y.toFloat()
                    )
                    list.add(data)
                } else if (list.size == 1) {
                    mHandles[2] = Point(downX.toInt(), downY.toInt())
                    mHandles[3] = Point(event?.x.toInt(), event?.y.toInt())

                    val data = floatArrayOf(
                        downX,
                        downY,
                        event?.x.toFloat(),
                        event?.y.toFloat()
                    )
                    list.add(data)
                }
                if (list.size > 1) {
                    var yyy = mainLogic()
                    if (yyy != null) {
                        if (intersect) {
                            var angle = getCurrentAngle(mHandles[0]!!, yyy!!, mHandles[3]!!)
                            Log.d("getCurrentAngle", angle.toString())
                            var angle2 = getCurrentAngle(mHandles[0]!!, yyy!!, mHandles[2]!!)
                            Log.d("getCurrentAngle", angle2.toString())
                        } else {
                            if (xuPoint2 != null) {
                                sweepAngle = getCurrentAngle(mHandles[0]!!, yyy!!, xuPoint2!!)
                                Log.d("getCurrentAngle", "xuPoint：" + sweepAngle.toString())
                            }
                        }
                        invalidate()
                        return true
                    }
                }
                return true
            }
        }
        return false
    }

    private fun onMove(event: MotionEvent): Boolean {
        if (list.size > 2) {
            return true
        }
        lastX = event.x
        lastY = event.y
        invalidate()
        return true
    }

    private fun onDown(event: MotionEvent): Boolean {
        downX = event.x
        downY = event.y
        return true
    }

    fun mainLogic(): Point? {
        var slope1 =
            (mHandles[1]!!.x - mHandles[0]!!.x).toDouble() / (mHandles[1]!!.y - mHandles[0]!!.y).toDouble()
        slope2 =
            (mHandles[3]!!.x - mHandles[2]!!.x).toDouble() / (mHandles[3]!!.y - mHandles[2]!!.y).toDouble()
        Log.d("getCurrentAngle", "slope:" + slope1 + "----" + slope2)
        if (slope1 == slope2) {   //平行
            intersect = false
        } else {
            intersect = intersection(
                mHandles[0]!!.x.toDouble(),
                mHandles[0]!!.y.toDouble(),
                mHandles[1]!!.x.toDouble(),
                mHandles[1]!!.y.toDouble(),
                mHandles[2]!!.x.toDouble(),
                mHandles[2]!!.y.toDouble(),
                mHandles[3]!!.x.toDouble(),
                mHandles[3]!!.y.toDouble()
            )
            if (intersect) {
                //计算交点，计算角度
                var startPoint = LinePoint(mHandles[0]!!.x.toDouble(), mHandles[0]!!.y.toDouble())
                var endPoint = LinePoint(mHandles[1]!!.x.toDouble(), mHandles[1]!!.y.toDouble())
                var line1 = Line(startPoint, endPoint)
                startPoint = LinePoint(mHandles[2]!!.x.toDouble(), mHandles[2]!!.y.toDouble())
                endPoint = LinePoint(mHandles[3]!!.x.toDouble(), mHandles[3]!!.y.toDouble())
                var line2 = Line(startPoint, endPoint)
                var xx = getInterHPoint(line1, line2)
                Log.d("getCurrentAngle", "getInterHPoint:" + xx!!.x + "--" + xx!!.y)
                return xx
            } else {
                //延长 or 平移相交，计算交点，计算角度
                extendPoint = getCrossPoint()
                //延长
//                if (extendPoint != null) {
//                    return extendPoint
//                }
                //平移、连接
                var middleX = (mHandles[1]!!.x + mHandles[0]!!.x) / 2.toDouble()
                var middleY = (mHandles[1]!!.y + mHandles[0]!!.y) / 2.toDouble()
                middlePoint = Point(middleX.toInt(), middleY.toInt())

                var x = 0f
                var y = 0f
                if (Math.abs(mHandles[2]!!.y - mHandles[3]!!.y) < 50) {
                    x = (middlePoint!!.x - 150).toFloat()
                    y = k2 * (x - middlePoint!!.x) + middlePoint!!.y
                    xuPoint1 = Point(x.toInt(), y.toInt())

                    x = (middlePoint!!.x + 150).toFloat()
                    y = k2 * (x - middlePoint!!.x) + middlePoint!!.y
                    xuPoint2 = Point(x.toInt(), y.toInt())

                } else {
                    y = (middlePoint!!.y - 100).toFloat()
                    x = (y - middlePoint!!.y) / k2 + middlePoint!!.x
                    xuPoint1 = Point(x.toInt(), y.toInt())
                    y = (middlePoint!!.y + 100).toFloat()
                    x = (y - middlePoint!!.y) / k2 + middlePoint!!.x
                    xuPoint2 = Point(x.toInt(), y.toInt())
                }


                return middlePoint
            }
        }
        return null
    }

    /**
     * 两【线段】是否相交
     * @param l1x1 线段1的x1
     * @param l1y1 线段1的y1
     * @param l1x2 线段1的x2
     * @param l1y2 线段1的y2
     * @param l2x1 线段2的x1
     * @param l2y1 线段2的y1
     * @param l2x2 线段2的x2
     * @param l2y2 线段2的y2
     * @return 是否相交
     */
    fun intersection(
        l1x1: Double, l1y1: Double, l1x2: Double, l1y2: Double,
        l2x1: Double, l2y1: Double, l2x2: Double, l2y2: Double
    ): Boolean {
        //https://blog.csdn.net/shy_snow/article/details/84642568
        // 快速排斥实验 首先判断两条线段在 x 以及 y 坐标的投影是否有重合。 有一个为真，则代表两线段必不可交。
        if (Math.max(l1x1, l1x2) < Math.min(l2x1, l2x2) || Math.max(l1y1, l1y2) < Math.min(
                l2y1,
                l2y2
            ) || Math.max(l2x1, l2x2) < Math.min(l1x1, l1x2) || Math.max(
                l2y1,
                l2y2
            ) < Math.min(l1y1, l1y2)
        ) {
            return false
        }
        // 跨立实验  如果相交则矢量叉积异号或为零，大于零则不相交
        return if ((((l1x1 - l2x1) * (l2y2 - l2y1) - (l1y1 - l2y1) * (l2x2 - l2x1))
                    * ((l1x2 - l2x1) * (l2y2 - l2y1) - (l1y2 - l2y1) * (l2x2 - l2x1))) > 0
            || (((l2x1 - l1x1) * (l1y2 - l1y1) - (l2y1 - l1y1) * (l1x2 - l1x1))
                    * ((l2x2 - l1x1) * (l1y2 - l1y1) - (l2y2 - l1y1) * (l1x2 - l1x1))) > 0
        ) {
            false
        } else true
    }


    /**
     * 行列式求两条直线交点
     */
    fun getInterHPoint(a: Line, b: Line): Point? {
        //https://www.zhihu.com/question/38642943?sort=created
        val hp = Point()
        val D: Double = ((a.endPoint.x - a.startPoint.x) * (b.startPoint.y - b.endPoint.y)
                - (b.endPoint.x - b.startPoint.x) * (a.startPoint.y - a.endPoint.y))
        val D1: Double = ((b.startPoint.y * b.endPoint.x - b.startPoint.x * b.endPoint.y)
                * (a.endPoint.x - a.startPoint.x)
                - (a.startPoint.y * a.endPoint.x - a.startPoint.x * a.endPoint.y)
                * (b.endPoint.x - b.startPoint.x))
        val D2: Double = ((a.startPoint.y * a.endPoint.x - a.startPoint.x * a.endPoint.y)
                * (b.startPoint.y - b.endPoint.y)
                - (b.startPoint.y * b.endPoint.x - b.startPoint.x * b.endPoint.y)
                * (a.startPoint.y - a.endPoint.y))
        hp.x = (D1 / D).toInt()
        hp.y = (D2 / D).toInt()
        return hp
    }

    fun getCurrentAngle(a: Point, b: Point, c: Point): Int {
        val AB = Point(
            b.x - a.x,
            b.y - a.y
        )
        val CB = Point(
            b.x - c.x,
            b.y - c.y
        )
        val dot = (AB.x * CB.x + AB.y * CB.y).toDouble()
        val cross = (AB.x * CB.y - AB.y * CB.x).toDouble()
        val alpha = Math.atan2(cross, dot)
        var xxx = Math.floor(alpha * 180.0 / Math.PI + 0.5).toInt()
//        angleLessZero = xxx < 0
        return Math.abs(xxx)
    }


    /**
     * 计算两条线段交点
     */
    //https://blog.csdn.net/zhangxbj/article/details/45558361
    fun getCrossPoint(): Point? {
        val x: Float
        val y: Float
        val x1: Float = mHandles[0]?.x!!.toFloat()
        val y1: Float = mHandles[0]?.y!!.toFloat()
        val x2: Float = mHandles[1]?.x!!.toFloat()
        val y2: Float = mHandles[1]?.y!!.toFloat()
        val x3: Float = mHandles[2]?.x!!.toFloat()
        val y3: Float = mHandles[2]?.y!!.toFloat()
        val x4: Float = mHandles[3]?.x!!.toFloat()
        val y4: Float = mHandles[3]?.y!!.toFloat()

        var flag1 = false
        var flag2 = false
        if (x1 - x2 == 0f) flag1 = true
        if (x3 - x4 == 0f) flag2 = true
        if (!flag1) k1 = (y1 - y2) / (x1 - x2)
        if (!flag2) k2 = (y3 - y4) / (x3 - x4)

        Log.d("getCurrentAngle", "k：" + k1 + "----" + k2)

        if (k1 == k2) return null
        if (flag1) {
            if (flag2) return null
            x = x1
            y = if (k2 == 0f) {
                y3
            } else {
                k2 * (x - x4) + y4
            }
        } else if (flag2) {
            x = x3
            y = if (k1 == 0f) {
                y1
            } else {
                k1 * (x - x2) + y2
            }
        } else {
            if (k1 == 0f) {
                y = y1
                x = (y - y4) / k2 + x4
            } else if (k2 == 0f) {
                y = y3
                x = (y - y2) / k1 + x2
            } else {//kx-y=b
                // https://leetcode-cn.com/problems/intersection-lcci/solution/shi-yong-zhi-xian-xie-jie-shi-fang-cheng-zai-pan-d/
                x = (k1 * x2 - k2 * x4 + y4 - y2) / (k1 - k2)
                y = k1 * (x - x2) + y2
            }
        }
        val point = Point()
        point.x = x.toInt()
        point.y = y.toInt()
        Log.d("getCurrentAngle", "getCrossPoint" + x + "---" + y)
        return point

        //检测交点的横、纵坐标是否在两条线段的横纵坐标范围之内
        return if (between(x1, x2, x) && between(y1, y2, y) && between(x3, x4, x) && between(
                y3,
                y4,
                y
            )
        ) {
            val point = Point()
            point.x = x.toInt()
            point.y = y.toInt()
            Log.d("getCurrentAngle", "getCrossPoint" + x + "---" + y)
            point
//            if (point == lsegA.getA() || point == lsegA.getB()) null else point
        } else {
            null
        }
    }

    fun between(a: Float, b: Float, target: Float): Boolean {
        return if (target >= a - 0.01 && target <= b + 0.01 || target <= a + 0.01 && target >= b - 0.01) true else false
    }


    /**
     *  垂直线
     */
    fun verticlalLine(): Point {
        val dx = mHandles[1]!!.x!! - mHandles[0]!!.x!!
        val dy = mHandles[1]!!.y!! - mHandles[0]!!.y!!

        x = (mHandles[1]!!.x!! - dy).toFloat()
        y = (mHandles[1]!!.y!! + dx).toFloat()

        var vp = Point(x.toInt(), y.toInt())
        return vp
    }

}