package com.ly.measureangleview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView


class MeasuerAngleView3 : AppCompatImageView {
    private var mContext: Context
    private lateinit var mLinePaint: Paint

    private var downY: Float = 0f
    private var downX: Float = 0f
    var lastX = 0f
    var lastY = 0f
    var list: ArrayList<FloatArray> = ArrayList()
    lateinit var mHandles: Array<Point?>


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

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (list.size > 1) {
        }
        if (list.size <= 1) {
            canvas?.drawLine(downX, downY, lastX, lastY, mLinePaint)
        }
        for (i in list.indices) {
            val data = list[i]
            canvas?.drawLine(data[0], data[1], data[2], data[3], mLinePaint)
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
                }
                return true
            }
        }
        return false
    }

    private fun mainLogic(): Point? {
        return null
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


}