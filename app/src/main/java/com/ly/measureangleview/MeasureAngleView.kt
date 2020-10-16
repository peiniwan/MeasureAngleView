package com.ly.measureangleview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView


/**
 * 角度测量view
 */
internal class MeasureAngleView : AppCompatImageView {
    private var downY: Float = 0f
    private var downX: Float = 0f
    private var mContext: Context
    lateinit var mHandles: Array<Point?>
    private lateinit var mHandlePaint: Paint
    private lateinit var mTextPaint: Paint
    private lateinit var mAcrPaint: Paint
    private lateinit var mLinePaint: Paint
    private lateinit var mLinePaint2: Paint
    private lateinit var mAcrPaint2: Paint

    private var mCurrentHandle: Point? = null

    private var mHandleSize = 21f
    private var mHandleColor = Color.parseColor("#146BFF")

    private var mListener: OnAngleSelectedListener? = null
    private var angleLessZero = false
    private var isShowLine = false
    private var cacheBitmap: Bitmap? = null
    private var scaleFactor = 1.5f


    constructor(context: Context) : super(context) {
        mContext = context
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        mContext = context
        initialize()
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val d = drawable
//        if (d != null) {
//            // ceil not round - avoid thin vertical gaps along the left/right edges
//            val width = MeasureSpec.getSize(widthMeasureSpec)
//            val height = MeasureSpec.getSize(heightMeasureSpec)00
//            val width1 = Math.ceil(
//                (height.toFloat() /(d.intrinsicHeight.toFloat() / d.intrinsicWidth.toFloat())).toDouble()
//            ).toInt()
//            //高度根据使得图片的宽度充满屏幕计算而得
//            val height1 = Math.ceil(
//                (width.toFloat() * d.intrinsicHeight.toFloat() / d.intrinsicWidth.toFloat()).toDouble()).toInt()
//            if(d.intrinsicWidth>d.intrinsicHeight){
//                setMeasuredDimension(width, height1)
//            }else{
//                setMeasuredDimension(width1, height)
//            }
//        } else {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        }
//    }


    private fun initialize() {
        setDrawingCacheEnabled(true)
        mDensity = resources.displayMetrics.density
        mLinePaint = Paint()
        mLinePaint.isAntiAlias = true
        mLinePaint.color = mHandleColor
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeWidth = mHandleSize / 2
        mLinePaint.setStrokeCap(Paint.Cap.ROUND)
        mLinePaint.setStrokeJoin(Paint.Join.BEVEL)

        mLinePaint2 = Paint()
        mLinePaint2.isAntiAlias = true
        mLinePaint2.color = Color.WHITE
        mLinePaint2.style = Paint.Style.STROKE
        mLinePaint2.strokeWidth = 15f
        mLinePaint2.setStrokeCap(Paint.Cap.ROUND)
        mLinePaint2.setStrokeJoin(Paint.Join.BEVEL)


        mHandlePaint = Paint()
        mHandlePaint.color = 0Xccffffff.toInt()
        mHandlePaint.isAntiAlias = true
        mHandlePaint.style = Paint.Style.FILL_AND_STROKE

        mTextPaint = Paint()
        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.textSize = 56f
        mTextPaint.color = mHandleColor
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD)
        mTextPaint.isAntiAlias = true

        mAcrPaint = Paint()
        mAcrPaint.style = Paint.Style.STROKE
        mAcrPaint.isAntiAlias = true
        mAcrPaint.color = mHandleColor
        mAcrPaint.strokeWidth = 10f
        mAcrPaint.pathEffect = DashPathEffect(floatArrayOf(14f, 14f), 1f)

        mAcrPaint2 = Paint()
        mAcrPaint2.style = Paint.Style.STROKE
        mAcrPaint2.isAntiAlias = true
        mAcrPaint2.color = Color.WHITE
        mAcrPaint2.strokeWidth = 12f
        mAcrPaint2.pathEffect = DashPathEffect(floatArrayOf(14f, 14f), 1f)

        mMagnifierPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMagnifierPaint.color = Color.WHITE
        mMagnifierPaint.style = Paint.Style.FILL

        mMagnifierCrossPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMagnifierCrossPaint.setColor(mHandleColor)
        mMagnifierCrossPaint.setStyle(Paint.Style.FILL)
        mMagnifierCrossPaint.setStrokeWidth(dp2px(MAGNIFIER_CROSS_LINE_WIDTH))

//        mPointPaint = Paint(Paint.ANTI_ALIAS_FLAG)  //四周圆点,蓝
//        mPointPaint.color = 0xFF00FFFF.toInt()
//        mPointPaint.strokeWidth = 1f
//        mPointPaint.style = Paint.Style.STROKE

//        mPointFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)  //四周圆点,白，大
//        mPointFillPaint.color = Color.WHITE
//        mPointFillPaint.style = Paint.Style.FILL
//        mPointFillPaint.alpha = 175

        mCropPoints = getFullImgCropPoints()
        mHandles = arrayOfNulls(3)
    }


    fun reset() {
        if (lineCount == 0) {
            return
        }
        list.clear()
        lastX = 0f
        isShowLine = false
        lineCount = 0
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mCanvas.drawPaint(paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        mHandles[0] = null
        mHandles[1] = null
        mHandles[2] = null
        invalidate()
    }


    fun setShowLine(x1: Int, y1: Int, x2: Int, y2: Int, x3: Int, y3: Int) {
        isShowLine = true
        lineCount = 2
        mHandles[0] = Point(x1, y1)
        mHandles[1] = Point(x2, y2)
        mHandles[2] = Point(x3, y3)
        invalidate()
    }

    lateinit var mCanvas: Canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCanvas = canvas
        //初始化图片位置信息,需要一直调
        getDrawablePosition()
        //绘制锚点
        onDrawPoints(canvas)
        if (lineCount == 2) {
            if (mHandles.get(0) == null || mHandles.get(1) == null || mHandles.get(2) == null) {
                return
            }
            Log.d("measureView", "onTouchEvent: " + 13)
            drawLines(canvas)
            drawHandles(canvas)
            drawAngle(canvas)
            //绘制放大镜
            onDrawMagnifier(canvas)
        } else {
            Log.d("measureView", "onTouchEvent:30---- " + downX + "---" + lastX)
//            canvas.drawBitmap(bit, 0f, 0f, null)
            if (lastX != 0f && (Math.abs(lastX - downX) > 10 || lineCount == 1)) { // && Math.abs(lastX - downX) > 10
                Log.d("measureView", "onTouchEvent: " + 31)
                canvas.drawLine(downX, downY, lastX, lastY, mLinePaint2)
                canvas.drawLine(downX, downY, lastX, lastY, mLinePaint)
                for (i in list.indices) {
                    val data = list[i]
                    canvas.drawLine(data[0], data[1], data[2], data[3], mLinePaint2)
                    canvas.drawLine(data[0], data[1], data[2], data[3], mLinePaint)
                    drawHandles(canvas)
                    if (mHandles.get(0) != null && mHandles.get(1) != null && mHandles.get(2) != null) {
                        drawAngle(canvas)
                    }

                }

                drawHandles(canvas)
                //绘制放大镜
                onDrawMagnifier(canvas)
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return onDown(event)
            MotionEvent.ACTION_MOVE -> return onMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (lineCount <= 1 && twoLineAngle != 180 && twoLineAngle != 0) {
                    var x = event.x.toInt()
                    var y = event.y.toInt()
                    if (mHandles[1] == null) {
                        if (x > mActLeft + mActWidth - 20) {
                            x = mActLeft + mActWidth - 20
                        }
                        if (x < mActLeft) {
                            x = mActLeft + 10
                        }
                        if (y < mActTop + 10) {
                            y = mActTop + 10
                        }
                        if (y > mActHeight + mActTop - 20) {  //bottom - top
                            y = mActHeight + mActTop - 20
                        }
                        mHandles[1] = Point(x, y)
                    }
                    if (mHandles[2] == null) {
                        //https://stackoverflow.com/questions/12175086/draw-perpendicular-line-to-given-line-on-canvas-android
                        val dx: Int = mHandles[1]!!.x!! - mHandles[0]!!.x!!
                        val dy: Int = mHandles[1]!!.y!! - mHandles[0]!!.y!!

                        x = mHandles[1]!!.x!! - dy
                        y = mHandles[1]!!.y!! + dx
                        if (x > mActLeft + mActWidth - 20) {
                            x = mActLeft + mActWidth - 20
                        }
                        if (x < mActLeft) {
                            x = mActLeft + 10
                        }
                        if (y < mActTop + 10) {
                            y = mActTop + 10
                        }
                        if (y > mActHeight + mActTop - 20) {  //bottom - top
                            y = mActHeight + mActTop - 20
                        }

                        mHandles[2] = Point(x, y)
                        lineCount = 2
                        Log.d(
                            "measureView",
                            "onTouchEvent:12----- " + mHandles[1]!!.x + "," + mHandles[1]!!.y
                        )
                    }
                    lineCount = 2

                }
                mCurrentHandle = null
                mDraggingPoint = null
                invalidate()
                return true
            }
        }
        return false
    }


    var list: ArrayList<FloatArray> = ArrayList()


    private fun drawHandles(canvas: Canvas) {
        mHandles.forEachIndexed { index, point ->
            var handle = mHandles[index]
            handle?.x?.toFloat()?.let {
                canvas.drawCircle(
                    it, handle.y.toFloat(), 20f,
                    mHandlePaint
                )
//                canvas.drawText(index.toString().plus("˚"), it, handle.y.toFloat(), mTextPaint)
            }
        }
    }

    private fun drawLines(canvas: Canvas) {
        val middleHandle = mHandles[1]
        canvas.drawLine(
            mHandles[0]!!.x.toFloat(),
            mHandles[0]!!.y.toFloat(),
            middleHandle!!.x.toFloat(),
            middleHandle.y.toFloat(),
            mLinePaint2
        )
        canvas.drawLine(
            middleHandle.x.toFloat(),
            middleHandle.y.toFloat(),
            mHandles[2]!!.x.toFloat(),
            mHandles[2]!!.y.toFloat(),
            mLinePaint2
        )
        canvas.drawLine(
            mHandles[0]!!.x.toFloat(),
            mHandles[0]!!.y.toFloat(),
            middleHandle!!.x.toFloat(),
            middleHandle.y.toFloat(),
            mLinePaint
        )
        canvas.drawLine(
            middleHandle.x.toFloat(),
            middleHandle.y.toFloat(),
            mHandles[2]!!.x.toFloat(),
            mHandles[2]!!.y.toFloat(),
            mLinePaint
        )


    }


    private fun drawAngle(canvas: Canvas) {
        val middleHandle = mHandles[1]
        if (middleHandle == null) {
            return
        }
        var currentAngle = getCurrentAngle()
        if (mListener != null) {
            mListener?.onAngleSelected(currentAngle)
        }
        var isDrawArc = true
        if (Math.abs(middleHandle!!.x - mHandles[0]!!.x) < 30 && Math.abs(middleHandle!!.y - mHandles[0]!!.y) < 30) {
            isDrawArc = false
        }
        if (Math.abs(middleHandle!!.x - mHandles[2]!!.x) < 30 && Math.abs(middleHandle!!.y - mHandles[2]!!.y) < 30) {
            isDrawArc = false
        }

        var distanceMiddle = 140f  //弧半径
        //a到b的距离
        var r1 =
            Math.sqrt(((middleHandle!!.x - mHandles[0]!!.x) * (middleHandle!!.x - mHandles[0]!!.x) + (middleHandle!!.y - mHandles[0]!!.y) * (middleHandle!!.y - mHandles[0]!!.y)).toDouble())
        var r2 =
            Math.sqrt(((middleHandle!!.x - mHandles[2]!!.x) * (middleHandle!!.x - mHandles[2]!!.x) + (middleHandle!!.y - mHandles[2]!!.y) * (middleHandle!!.y - mHandles[2]!!.y)).toDouble())
        if ((r1 < 140 || r2 < 140)) {
            if (r1 < r2) {
                distanceMiddle = r1.toFloat()
            } else {
                distanceMiddle = r2.toFloat()
            }
        }
        var y1 = (distanceMiddle * (mHandles[0]!!.y - middleHandle!!.y)) / r1 + middleHandle!!.y
        var x1 = (distanceMiddle * (mHandles[0]!!.x - middleHandle!!.x)) / r1 + middleHandle!!.x

        var y2 = (distanceMiddle * (mHandles[2]!!.y - middleHandle!!.y)) / r2 + middleHandle!!.y
        var x2 = (distanceMiddle * (mHandles[2]!!.x - middleHandle!!.x)) / r2 + middleHandle!!.x


        //左右俩点
        var p1 = PointF(x1.toFloat(), y1.toFloat())
        var p2 = PointF(x2.toFloat(), y2.toFloat())

        var difValue = 0
        if (angleLessZero) {
            difValue = 90
        } else {
            difValue = -90  //下
        }
        val path = Path()
        val midX: Float = p1.x + (p2.x - p1.x) / 2
        val midY: Float = p1.y + (p2.y - p1.y) / 2
        val xDiff: Float = (midX - p1.x)
        val yDiff: Float = (midY - p1.y)
        //计算两点之间的90度角
        val angle = Math.atan2(
            yDiff.toDouble(),
            xDiff.toDouble()
        ) * (180 / Math.PI) + difValue
        val angleRadians = Math.toRadians(angle)

        var pHeight = currentAngle + 50
        if ((r1 < 140 || r2 < 140)) {
            pHeight -= 80
        }
        val pointX =
            (midX + pHeight * Math.cos(angleRadians)).toFloat()
        val pointY =
            (midY + pHeight * Math.sin(angleRadians)).toFloat()

        var x = pointX - 40
        var y = pointY

        if (x > mActLeft + mActWidth) {
            x = (mActLeft + mActWidth - 100).toFloat()
            isDrawArc = false
        }
        if (x < mActLeft) {
            x = (mActLeft).toFloat()
            isDrawArc = false
        }
        if (y < mActTop) {
            y = (mActTop + 40).toFloat()
            isDrawArc = false
        }
        if (y > mActHeight + mActTop) {
            y = (mActHeight + mActTop).toFloat()
            isDrawArc = false
        }
        canvas.drawText(currentAngle.toString().plus("˚"), x, y, mTextPaint)


        if (isDrawArc) {
//            path.moveTo(p1.x, p1.y)
            //三点画弧
//            path.cubicTo(p1.x, p1.y, pointX, pointY, p2.x, p2.y)
//            canvas.drawPath(path, mAcrPaint2)
//            canvas.drawPath(path, mAcrPaint)

            var startAngle = 0  //开始角度
            var yyy = 0f  //旋转角度，-为逆时针
            if (mCurrentHandle == mHandles[1]) {
                if (mHandles[0]!!.y < mHandles[2]!!.y) {
                    if (mHandles[1]!!.y < mHandles[0]!!.y) {  //v
                        if (angleLessZero) {
                            startAngle = getCurrentAngle1()
                            yyy = -currentAngle.toFloat()
                        } else {
                            if (mHandles[0]!!.x < mHandles[2]!!.x) {
                                startAngle = -getCurrentAngle1()
                                yyy = currentAngle.toFloat()
                            } else {
                                startAngle = getCurrentAngle1()
                                yyy = currentAngle.toFloat()
                            }
                        }
                    } else {
                        if (angleLessZero) {//
                            startAngle = -getCurrentAngle1()
                            yyy = -currentAngle.toFloat()
                        } else {
                            if (mHandles[0]!!.x < mHandles[2]!!.x) {
                                startAngle = -getCurrentAngle1()
                                yyy = currentAngle.toFloat()
                            } else {
                                startAngle = -getCurrentAngle1()
                                yyy = currentAngle.toFloat()
                            }
                        }
                    }
                } else {
                    if (mHandles[1]!!.y < mHandles[2]!!.y) {  //v
                        if (angleLessZero) {
                            startAngle = getCurrentAngle1()
                            yyy = -currentAngle.toFloat()
                        } else {
                            startAngle = getCurrentAngle2() //
                            yyy = -currentAngle.toFloat()
                        }
                    } else {
                        if (mHandles[1]!!.y < mHandles[0]!!.y) {
                            if (angleLessZero) {
                                startAngle = getCurrentAngle1()
                                yyy = -currentAngle.toFloat()
                            } else {
                                startAngle = getCurrentAngle1()
                                yyy = currentAngle.toFloat()
                            }
                        } else {
                            if (mHandles[0]!!.x < mHandles[2]!!.x) {
                                if (mHandles[1]!!.x < mHandles[0]!!.x) {
                                    if (mHandles[1]!!.y < mHandles[0]!!.y) {
                                        startAngle = -getCurrentAngle1()
                                        yyy = currentAngle.toFloat()
                                    } else {
                                        if (angleLessZero) {
                                            startAngle = -getCurrentAngle1()
                                            yyy = -currentAngle.toFloat()
                                        } else {
                                            startAngle = -getCurrentAngle1()
                                            yyy = currentAngle.toFloat()
                                        }
                                    }
                                } else {
                                    startAngle = -getCurrentAngle1()
                                    yyy = currentAngle.toFloat()
                                }

//                                startAngle = -getCurrentAngle1()
//                                yyy = currentAngle.toFloat()
                            } else {
                                if (mHandles[1]!!.x < mHandles[0]!!.x) {
                                    startAngle = -getCurrentAngle1()
                                    yyy = -currentAngle.toFloat()
                                } else {
                                    startAngle = -getCurrentAngle1()
                                    yyy = currentAngle.toFloat()
                                }
                            }
                        }
                    }
                }
            } else if (mCurrentHandle == mHandles[2]) {
                if (angleLessZero) {
                    yyy = -currentAngle.toFloat()
                } else {
                    yyy = currentAngle.toFloat()
                }
                if (mHandles[0]!!.y < mHandles[1]!!.y) {
                    startAngle = -getCurrentAngle1()
                } else {
                    startAngle = getCurrentAngle1()
                }
            } else {
                if (angleLessZero) {
                    yyy = currentAngle.toFloat()
                } else {
                    yyy = -currentAngle.toFloat()
                }
                if (mHandles[2]!!.y < mHandles[1]!!.y) {
                    startAngle = -getCurrentAngle2()
                } else {
                    startAngle = getCurrentAngle2()
                }
            }
            var rectF = RectF(
                middleHandle?.x!! - distanceMiddle,
                middleHandle?.y!! - distanceMiddle,
                middleHandle?.x!! + distanceMiddle,
                middleHandle?.y!! + distanceMiddle
            )
            canvas.drawArc(rectF, startAngle.toFloat(), yyy, false, mAcrPaint2!!) // 绘制扇形
            canvas.drawArc(rectF, startAngle.toFloat(), yyy, false, mAcrPaint!!) // 绘制扇形
//            Log.d(
//                "measureView",
//                "onTouchEvent:----- " + currentAngle + "---==" + startAngle + "-----" + angleLessZero
//            )
        }

    }

    private fun onDown(event: MotionEvent): Boolean {
        if (lineCount < 2) {
            downX = event.x
            downY = event.y
            mHandles[0] = Point(downX.toInt(), downY.toInt())
            invalidate()
        }
        mDraggingPoint = getNearbyPoint(event)
        if (mHandles.get(0) == null || mHandles.get(1) == null || mHandles.get(2) == null) {
            return true
        }
        if (checkIfHandleTouch(event.x, event.y) == null) {
            return true
        }
        lineCount = 2
        val handle = checkIfHandleTouch(event.x, event.y)
        mCurrentHandle = handle
        Log.d("measureView", "onTouchEvent: " + 6)
        return true
    }

    var lineCount = 0
    var twoLineAngle = 0

    private fun onMove(event: MotionEvent): Boolean {
        toImagePointSize(mDraggingPoint, event)
        if (mCurrentHandle != null && checkInBounds(event.x, event.y)) {
            invalidate()
            Log.d("measureView", "onTouchEvent: " + 7)
            mCurrentHandle!![event.x.toInt()] = event.y.toInt()
            return true
        } else {
            if (!checkInBounds(
                    event.x,
                    event.y
                ) || lineCount == 2
            ) {
                invalidate()
                return true
            }
            if (Math.abs(lastX - event?.x) > 10f || Math.abs(lastY - event?.y) > 10f) {
                Log.d("measureView", "onTouchEvent " + 35)

                var point0 = PointF(downX, downY)
                var point1 = PointF(lastX, lastY)
                var point2 = PointF(event?.x, event?.y)
                twoLineAngle = 180 - getCurrentAngle3(point0, point1, point2)

                if (twoLineAngle > 15 && twoLineAngle < 170 && lineCount == 0 && (Math.abs(lastX - downX) > 10f || Math.abs(
                        lastY - downY
                    ) > 10f)
                ) {
                    Log.d(
                        "measureView",
                        "onTouchEvent: 20----" + twoLineAngle + "---" + downX + "," + downY + "---" + lastX + "," + lastY + "----" + event.x + "," + event.y
                    )
                    if (lineCount == 0 && checkInBounds(event.x, event.y)) {
                        Log.d("measureView", "onTouchEvent: " + 22)
                        mHandles[1] = Point(event?.x.toInt(), event?.y.toInt())
                        lineCount = 1
                        val data = floatArrayOf(
                            downX,
                            downY,
                            event?.x.toFloat(),
                            event?.y.toFloat()
                        )
                        list.add(data)
                        downX = event?.x
                        downY = event?.y

                        lastX = event.x
                        lastY = event.y
//                        invalidate()
                    }
                } else {
                    Log.d("measureView", "onTouchEvent: " + 23)
                    lastX = event.x
                    lastY = event.y
                    if (list.size > 0) {
                        mHandles[2] = Point(event.x.toInt(), event.y.toInt())
                    }
//                    invalidate()
                }
            }
            invalidate()

            Log.d("measureView", "lineCount:==== " + lineCount)
            return true
        }
        return false
    }


    fun getCurrentAngle3(p0: PointF, p1: PointF, p2: PointF): Int {
        val AB = Point(
            (p1.x - p0.x).toInt(),
            (p1.y - p0.y).toInt()
        )
        val CB = Point(
            (p1.x - p2.x).toInt(),
            (p1.y - p2.y).toInt()
        )
        val dot = (AB.x * CB.x + AB.y * CB.y).toDouble()
        val cross = (AB.x * CB.y - AB.y * CB.x).toDouble()
        val alpha = Math.atan2(cross, dot)
        var xxx = Math.floor(alpha * 180.0 / Math.PI + 0.5).toInt()
        return Math.abs(xxx)
    }

    var lastX = 0f
    var lastY = 0f

    private fun checkIfHandleTouch(x: Float, y: Float): Point? {
        for (handle in mHandles) {
            var p = calcViewScreenLocation(handle)
            var b = p?.contains(x, y)
            if (b == true) {
                return handle
            }
        }
        return null
    }

    fun calcViewScreenLocation(p: Point?): RectF? {
        return p?.run {
            RectF(
                p.x - mHandleSize - 50, //
                p.y - mHandleSize - 50,
                p.x + mHandleSize + 50,
                p.y + mHandleSize + 50
            )
        }
    }

    private fun checkInBounds(x: Float, y: Float): Boolean {
        return mActLeft + 10 < x && x < mActLeft + mActWidth - 20 &&
                mActTop + 10 < y && y < mActHeight + mActTop - 20
    }

    fun getCurrentAngle(): Int {
        if (mHandles.get(0) == null || mHandles.get(1) == null || mHandles.get(2) == null) {
            return 0
        }
        val AB = Point(
            mHandles[1]!!.x - mHandles[0]!!.x,
            mHandles[1]!!.y - mHandles[0]!!.y
        )
        val CB = Point(
            mHandles[1]!!.x - mHandles[2]!!.x,
            mHandles[1]!!.y - mHandles[2]!!.y
        )
        val dot = (AB.x * CB.x + AB.y * CB.y).toDouble()
        val cross = (AB.x * CB.y - AB.y * CB.x).toDouble()
        val alpha = Math.atan2(cross, dot)
        var xxx = Math.floor(alpha * 180.0 / Math.PI + 0.5).toInt()
        angleLessZero = xxx < 0
        return Math.abs(xxx)
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

    fun getCurrentAngle2(): Int {
        var shuiping = Point(mHandles[1]!!.x!! + 100, mHandles[1]!!.y!!)
        val AB = Point(
            mHandles[1]!!.x - mHandles[2]!!.x,
            mHandles[1]!!.y - mHandles[2]!!.y
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


    fun setOnAngleSelectedListener(listener: OnAngleSelectedListener?) {
        mListener = listener
    }

//    fun setColor(color: Int) {
//        mHandleColor = color
//        mHandlePaint.color = color
//        invalidate()
//    }
//
//    fun setHandleSize(handleSize: Int) {
//        mHandleSize = handleSize.toFloat()
//        mHandlePaint.strokeWidth = mHandleSize / 4
//    }

    interface OnAngleSelectedListener {
        fun onAngleSelected(angle: Int)
    }


    //============================ 放大镜 =============================
    private var mActWidth: Int = 0
    private var mActHeight: Int = 0
    private var mActLeft: Int = 0
    private var mActTop: Int = 0 //实际显示图片的位置
    private var mMagnifierDrawable: ShapeDrawable? = null
    private var mDraggingPoint: Point? = null
    private var mScaleX = 1.5f
    private var mScaleY = 1.5f// 显示的图片与实际图片缩放比
    private var mDensity = 0f
    private val mMatrixValue = FloatArray(9)

    private val TOUCH_POINT_CATCH_DISTANCE = 15f //dp，触摸点捕捉到锚点的最小距离
    private val POINT_RADIUS = 10f // dp，锚点绘制半价
    private val MAGNIFIER_CROSS_LINE_WIDTH = 0.8f //dp，放大镜十字宽度
    private val MAGNIFIER_CROSS_LINE_LENGTH = 3f //dp， 放大镜十字长度
    private val MAGNIFIER_BORDER_WIDTH = 1f //dp，放大镜边框宽度
    private lateinit var mMagnifierPaint: Paint
    private val mMagnifierMatrix = Matrix()
    private lateinit var mMagnifierCrossPaint: Paint
    var mMagnifierCrossColor: Long = 0xFFFF4081 // 放大镜十字颜色
    var mCropPoints: Array<Point?>? =
        null  // 裁剪区域, 0->LeftTop, 1->RightTop， 2->RightBottom, 3->LeftBottom
    var mEdgeMidPoints: Array<Point?>? = null //边中点
    private lateinit var mPointFillPaint: Paint
    private lateinit var mPointPaint: Paint
    var mShowEdgeMidPoint = true //是否显示边中点
    var mDragLimit = true // 是否限制锚点拖动范围为凸四边形
    private val P_LT = 0
    private val P_RT = 1
    private val P_RB = 2
    private val P_LB = 3


    internal enum class DragPointType {
        LEFT_TOP, RIGHT_TOP, RIGHT_BOTTOM, LEFT_BOTTOM, TOP, RIGHT, BOTTOM, LEFT;

        companion object {
            fun isEdgePoint(type: DragPointType): Boolean {
                return type == TOP || type == RIGHT || type == BOTTOM || type == LEFT
            }
        }
    }

    protected fun onDrawPoints(canvas: Canvas) {
        if (!checkPoints(mCropPoints)) {
            return
        }
        for (point in mCropPoints!!) {
//            getViewPointX(point)?.let {
//                point?.let { it1 -> getViewPointY(it1) }?.let { it2 ->
//                    canvas.drawCircle(
//                        it,
//                        it2,
//                        dp2px(POINT_RADIUS),
//                        mPointFillPaint
//                    )
//                }
//            }
//            getViewPointX(point)?.let {
//                point?.let { it1 -> getViewPointY(it1) }?.let { it2 ->
//                    canvas.drawCircle(
//                        it,
//                        it2,
//                        dp2px(POINT_RADIUS),
//                        mPointPaint
//                    )
//                }
//            }
        }
        if (mShowEdgeMidPoint) {
            setEdgeMidPoints()
            //中间锚点
//            for (point in mEdgeMidPoints!!) {
//                getViewPointX(point!!)?.let {
//                    getViewPointY(point)?.let { it1 ->
//                        canvas.drawCircle(
//                            it,
//                            it1,
//                            dp2px(POINT_RADIUS),
//                            mPointFillPaint
//                        )
//                    }
//                }
////                getViewPointX(point)?.let {
////                    getViewPointY(point)?.let { it1 ->
////                        canvas.drawCircle(
////                            it,
////                            it1,
////                            dp2px(POINT_RADIUS),
////                            mPointPaint
////                        )
////                    }
////                }
//            }
        }
    }


    private fun onDrawMagnifier(canvas: Canvas) {
        if (mDraggingPoint != null) {
//            if (mMagnifierDrawable == null) {
            initMagnifier()

            if (mMagnifierDrawable == null) {
                return
            }
            var draggingX: Float? = getViewPointX(mDraggingPoint)
            var draggingY: Float? = getViewPointY(mDraggingPoint)
            if (draggingX == null || draggingY == null) {
                return
            }
            if (mActTop == 0) {
                draggingX += mActLeft
                draggingY += 20f
            }
            val radius = width / 8.toFloat()
            var cx = radius
//            val lineOffset =
//                dp2px(MAGNIFIER_BORDER_WIDTH).toString().toInt()
            val lineOffset = 2
            mMagnifierDrawable?.setBounds(
                lineOffset,
                lineOffset,
                radius.toInt() * 2 - lineOffset,
                radius.toInt() * 2 - lineOffset
            )
            val pointsDistance: Double? =
                draggingX?.let { draggingY?.let { it1 -> getPointsDistance(it, it1, 0f, 0f) }!! }
            if (pointsDistance != null) {
                if (pointsDistance < radius * 2.5) {
                    mMagnifierDrawable?.setBounds(
                        width - radius.toInt() * 2 + lineOffset,
                        lineOffset,
                        width - lineOffset,
                        radius.toInt() * 2 - lineOffset
                    )
                    cx = width - radius
                }
            }
            canvas.drawCircle(cx, radius, radius, mMagnifierPaint)
            mMagnifierMatrix.setTranslate(
                radius - draggingX * scaleFactor,
                radius - draggingY * scaleFactor
            )
            mMagnifierDrawable?.paint?.shader?.setLocalMatrix(mMagnifierMatrix)
            mMagnifierDrawable?.draw(canvas)

            //十字架
//            val crossLength: Float =
//                dp2px(MAGNIFIER_CROSS_LINE_LENGTH)
//            canvas.drawLine(
//                cx,
//                radius - crossLength,
//                cx,
//                radius + crossLength,
//                mMagnifierCrossPaint
//            )
//            canvas.drawLine(
//                cx - crossLength,
//                radius,
//                cx + crossLength,
//                radius,
//                mMagnifierCrossPaint
//            )
        }
    }


    fun saveSignature(): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(this.width, this.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        if (this != null) {
            draw(canvas)
        }
        return bitmap
    }


    var b: Bitmap? = null

//    var getBitmapListener:GetBitmapListener?=null

    interface GetBitmapListener {
        fun setBitmapListener(b: Bitmap?)
    }


    private fun initMagnifier() {  //显示一俩个。不会一直显示
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)
//        if(getBitmap()==null){
//            return
//        }
//        val service = Executors.newCachedThreadPool()
//        service.submit {
//               b=getBitmapFromView(this)  //不行
//                b = saveSignature()  //太卡
//            b = cacheBitmap  //没效果
//       var  bb = getBitmap()

        if (getDrawingCache() == null) {
            return
        }
        setDrawingCacheEnabled(true)
        buildDrawingCache()
        b = Bitmap.createBitmap(getDrawingCache())
        destroyDrawingCache()
        b?.let {
            canvas.drawBitmap(
                it,
                null,
                Rect(mActLeft, 0, b?.width!! + mActLeft, b?.height!!),
                null
            )
        }
        canvas.save()
        val magnifierShader = BitmapShader(
//            bitmap,
            Bitmap.createScaledBitmap(
                bitmap, (bitmap.width * scaleFactor).toInt(),
                (bitmap.height * scaleFactor).toInt(), true
            ),
            Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        mMagnifierDrawable = ShapeDrawable(OvalShape())
        mMagnifierDrawable?.getPaint()?.setShader(magnifierShader)
//                getBitmapListener.setBitmapListener(b)

    }

    fun getBitmap(): Bitmap? {
        var bmp: Bitmap? = null
        val drawable: Drawable = getDrawable()
        if (drawable is BitmapDrawable) {
            bmp = drawable.bitmap
        }
        return bmp
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        view.isDrawingCacheEnabled = true
        //图片的宽度为屏幕宽度，高度为wrap_content
        view.measure(
            MeasureSpec.makeMeasureSpec(
                getResources().getDisplayMetrics().widthPixels,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(
                0,
                MeasureSpec.UNSPECIFIED
            )
        )
        //放置mView
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        return view.drawingCache
    }

    private fun getDrawablePosition() {
        val drawable = drawable
        if (drawable != null) {
            imageMatrix.getValues(mMatrixValue)
            mScaleX = mMatrixValue.get(Matrix.MSCALE_X)
            mScaleY = mMatrixValue.get(Matrix.MSCALE_Y)
            val origW = drawable.intrinsicWidth
            val origH = drawable.intrinsicHeight
            mActWidth = Math.round(origW * mScaleX)
            mActHeight = Math.round(origH * mScaleY)
            mActLeft = (width - mActWidth) / 2
            mActTop = (height - mActHeight) / 2
        }
    }


    private fun getViewPointX(point: Point?): Float? {
        return point?.x?.toFloat()?.let { getViewPointX(it) }
    }

    private fun getViewPointX(x: Float): Float {
        return x * mScaleX + mActLeft
    }

    private fun getViewPointY(point: Point?): Float? {
        return point?.y?.toFloat()?.let { getViewPointY(it) }
    }

    private fun getViewPointY(y: Float): Float {
        return y * mScaleY + mActTop
    }

    private fun dp2px(dp: Float): Float {
        return dp * mDensity
    }

    fun getPointsDistance(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Double {
        return Math.sqrt(
            Math.pow(x1 - x2.toDouble(), 2.0) + Math.pow(
                y1 - y2.toDouble(),
                2.0
            )
        )
    }

    private fun getNearbyPoint(event: MotionEvent): Point? {
        return Point(event.x.toInt(), event.y.toInt())
        //以前的
//        if (checkPoints(mCropPoints)) {
//            for (p in this!!.mCropPoints!!) {
//                if (p?.let { isTouchPoint(it, event) }!!) return p
//            }
//        }
//        if (checkPoints(mEdgeMidPoints)) {
//            for (p in this!!.mEdgeMidPoints!!) {
//                if (p?.let { isTouchPoint(it, event) }!!) return p
//            }
//        }
//        return null
    }

    fun checkPoints(points: Array<Point?>?): Boolean {
        return points != null && points.size == 4 && points[0] != null && points[1] != null && points[2] != null && points[3] != null
    }

    private fun getFullImgCropPoints(): Array<Point?> {
        val points = arrayOfNulls<Point>(4)
        val drawable = drawable
        if (drawable != null) {
            val width: Int
            val height: Int
//            if (drawable is BitmapDrawable) {
//                var bmp = (drawable as BitmapDrawable).getBitmap();
//                width = bmp.getWidth();
//                height = bmp.getHeight();
//            } else {
            width = drawable.intrinsicWidth
            height = drawable.intrinsicHeight
//            }
            points[0] = Point(0, 0)
            points[1] = Point(width, 0)
            points[2] = Point(width, height)
            points[3] = Point(0, height)
        }
        return points
    }

    private fun isTouchPoint(p: Point, event: MotionEvent): Boolean {
        return true
        //以前
//        val x = event.x
//        val y = event.y
//        val px = getViewPointX(p)
//        val py = getViewPointY(p)
//        val distance = Math.sqrt(
//            Math.pow(
//                x - (px?.toDouble() ?: 0.toDouble()),
//                2.0
//            ) + Math.pow(y - (py?.toDouble() ?: 0.toDouble()), 2.0)
//        )
//        return if (distance < dp2px(TOUCH_POINT_CATCH_DISTANCE)) {
//            true
//        } else false
    }


    fun setEdgeMidPoints() {
        if (mEdgeMidPoints == null) {
//            mEdgeMidPoints=mCropPoints
            mEdgeMidPoints = arrayOfNulls(4)
            for (i in mEdgeMidPoints!!.indices) {
                mEdgeMidPoints!![i] = Point()
            }
        }
        val len = mCropPoints!!.size
        for (i in 0 until len) {
            mEdgeMidPoints!![i]!![mCropPoints!![i]!!.x + (mCropPoints!![(i + 1) % len]!!.x - mCropPoints!![i]!!.x) / 2] =
                mCropPoints!![i]!!.y + (mCropPoints!![(i + 1) % len]!!.y - mCropPoints!![i]!!.y) / 2
        }
    }

    private fun toImagePointSize(
        dragPoint: Point?,
        event: MotionEvent
    ) {
        if (dragPoint == null) {
            return
        }

        val pointType: DragPointType? =
            getPointType(dragPoint)
        val x = ((Math.min(
            Math.max(event.x, mActLeft.toFloat()),
            mActLeft + mActWidth.toFloat()
        ) - mActLeft) / mScaleX).toInt()
        val y = ((Math.min(
            Math.max(event.y, mActTop.toFloat()),
            mActTop + mActHeight.toFloat()
        ) - mActTop) / mScaleY).toInt()
        //以前的
//        if (mDragLimit && pointType != null) {
//            when (pointType) {
//                DragPointType.LEFT_TOP -> if (!canMoveLeftTop(
//                        x,
//                        y
//                    )
//                ) return
//                DragPointType.RIGHT_TOP -> if (!canMoveRightTop(
//                        x,
//                        y
//                    )
//                ) return
//                DragPointType.RIGHT_BOTTOM -> if (!canMoveRightBottom(
//                        x,
//                        y
//                    )
//                ) return
//                DragPointType.LEFT_BOTTOM -> if (!canMoveLeftBottom(
//                        x,
//                        y
//                    )
//                ) return
//                DragPointType.TOP -> if (!canMoveLeftTop(
//                        x,
//                        y
//                    ) || !canMoveRightTop(x, y)
//                ) return
//                DragPointType.RIGHT -> if (!canMoveRightTop(
//                        x,
//                        y
//                    ) || !canMoveRightBottom(x, y)
//                ) return
//                DragPointType.BOTTOM -> if (!canMoveLeftBottom(
//                        x,
//                        y
//                    ) || !canMoveRightBottom(x, y)
//                ) return
//                DragPointType.LEFT -> if (!canMoveLeftBottom(
//                        x,
//                        y
//                    ) || !canMoveLeftTop(x, y)
//                ) return
//                else -> {
//                }
//            }
//        }
        if (DragPointType.isEdgePoint(pointType!!)) {
            val xoff = x - dragPoint.x
            val yoff = y - dragPoint.y
            moveEdge(pointType, xoff, yoff)
        } else {
            dragPoint.y = y
            dragPoint.x = x
        }

    }

    private fun moveEdge(type: DragPointType, xoff: Int, yoff: Int) {
        when (type) {
            DragPointType.TOP -> {
                movePoint(mCropPoints!![P_LT], 0, yoff)
                movePoint(mCropPoints!![P_RT], 0, yoff)
            }
            DragPointType.RIGHT -> {
                movePoint(mCropPoints!![P_RT], xoff, 0)
                movePoint(mCropPoints!![P_RB], xoff, 0)
            }
            DragPointType.BOTTOM -> {
                movePoint(mCropPoints!![P_LB], 0, yoff)
                movePoint(mCropPoints!![P_RB], 0, yoff)
            }
            DragPointType.LEFT -> {
                movePoint(mCropPoints!![P_LT], xoff, 0)
                movePoint(mCropPoints!![P_LB], xoff, 0)
            }
            else -> {
            }
        }
    }

    private fun movePoint(point: Point?, xoff: Int, yoff: Int) {
        if (point == null) return
        val x = point.x + xoff
        val y = point.y + yoff
        if (x < 0 || x > drawable.intrinsicWidth) return
        if (y < 0 || y > drawable.intrinsicHeight) return
        point.x = x
        point.y = y
    }

    private fun getPointType(dragPoint: Point?): DragPointType? {
        if (dragPoint == null) return null
        val type: DragPointType

        if (checkPoints(mCropPoints)) {
            for (i in mCropPoints!!.indices) {
                if (dragPoint === mCropPoints!![i]) {
                    type = DragPointType.values()[i]
                    return type
                }
            }
        }
        if (checkPoints(mEdgeMidPoints)) {
            for (i in mEdgeMidPoints!!.indices) {
                if (dragPoint === mEdgeMidPoints!![i]) {
                    type = DragPointType.values()[4 + i]
                    return type
                }
            }
        }
        type = DragPointType.values()[0]
        return type
    }

    private fun pointSideLine(
        lineP1: Point,
        lineP2: Point,
        point: Point
    ): Long {
        return pointSideLine(lineP1, lineP2, point.x, point.y)
    }

    private fun pointSideLine(
        lineP1: Point,
        lineP2: Point,
        x: Int,
        y: Int
    ): Long {
        val x1 = lineP1.x.toLong()
        val y1 = lineP1.y.toLong()
        val x2 = lineP2.x.toLong()
        val y2 = lineP2.y.toLong()
        return (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1)
    }

    private fun canMoveLeftTop(x: Int, y: Int): Boolean {
        if (pointSideLine(
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_LB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_LB]!!,
                mCropPoints!![P_RB]!!
            ) > 0
        ) {
            return false
        }
        if (pointSideLine(
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_RB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_RB]!!,
                mCropPoints!![P_LB]!!
            ) < 0
        ) {
            return false
        }
        return if (pointSideLine(
                mCropPoints!![P_LB]!!,
                mCropPoints!![P_RB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_LB]!!,
                mCropPoints!![P_RB]!!,
                mCropPoints!![P_RT]!!
            ) < 0
        ) {
            false
        } else true
    }

    private fun canMoveRightTop(x: Int, y: Int): Boolean {
        if (pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_RB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_RB]!!,
                mCropPoints!![P_LB]!!
            ) > 0
        ) {
            return false
        }
        if (pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_LB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_LB]!!,
                mCropPoints!![P_RB]!!
            ) < 0
        ) {
            return false
        }
        return if (pointSideLine(
                mCropPoints!![P_LB]!!,
                mCropPoints!![P_RB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_LB]!!,
                mCropPoints!![P_RB]!!,
                mCropPoints!![P_LT]!!
            ) < 0
        ) {
            false
        } else true
    }

    private fun canMoveRightBottom(x: Int, y: Int): Boolean {
        if (pointSideLine(
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_LB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_LB]!!,
                mCropPoints!![P_LT]!!
            ) > 0
        ) {
            return false
        }
        if (pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_RT]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_LB]!!
            ) < 0
        ) {
            return false
        }
        return if (pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_LB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_LB]!!,
                mCropPoints!![P_RT]!!
            ) < 0
        ) {
            false
        } else true
    }

    private fun canMoveLeftBottom(x: Int, y: Int): Boolean {
        if (pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_RB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_RB]!!,
                mCropPoints!![P_RT]!!
            ) > 0
        ) {
            return false
        }
        if (pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_RT]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_LT]!!,
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_RB]!!
            ) < 0
        ) {
            return false
        }
        return if (pointSideLine(
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_RB]!!,
                x,
                y
            )
            * pointSideLine(
                mCropPoints!![P_RT]!!,
                mCropPoints!![P_RB]!!,
                mCropPoints!![P_LT]!!
            ) < 0
        ) {
            false
        } else true
    }

    /**
     * 设置选区为包裹全图
     */
    fun setFullImgCrop() {
        if (drawable == null) {
            return
        }
        getDrawablePosition()
        mCropPoints = getFullImgCropPoints()
        invalidate()
    }


}