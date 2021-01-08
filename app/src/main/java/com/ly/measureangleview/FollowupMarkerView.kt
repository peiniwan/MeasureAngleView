package com.ly.measureangleview

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.utils.MPPointF
import java.util.*

class FollowupMarkerView : MarkerView {

    lateinit var tvContent1: TextView
    lateinit var tvContent2: TextView
    lateinit var tvContent3: TextView
    private val mOffset2 = MPPointF()

    public var drawingPosX = 0f
    public var drawingPosY = 0f
    private val MAX_CLICK_DURATION = 500
    private var startClickTime: Long = 0


    constructor(context: Context?, layoutResource: Int) : super(context, layoutResource) {
        tvContent1 = findViewById<TextView>(R.id.tvContent1)
        tvContent2 = findViewById<TextView>(R.id.tvContent2)
        tvContent3 = findViewById<TextView>(R.id.tvContent3)
        tvContent3.isClickable = true
        tvContent3.setOnClickListener {
            Log.d("FollowupMarkerView", "tvContent3")
        }
    }

    //https://github.com/PhilJay/MPAndroidChart/issues/144
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                startClickTime = Calendar.getInstance().getTimeInMillis()
            }
            MotionEvent.ACTION_UP -> {
                val clickDuration: Long = Calendar.getInstance().getTimeInMillis() - startClickTime
                if (clickDuration < MAX_CLICK_DURATION) {
                    tvContent3.performClick()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun draw(canvas: Canvas?, posX: Float, posY: Float) {
        super.draw(canvas, posX, posY)
        val offset = getOffsetForDrawingAtPoint(posX, posY)
        drawingPosX = posX + offset.x
        drawingPosY = posY + offset.y
    }



    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {


//        val offset = offset
//        mOffset2.x = offset.x
//        mOffset2.y = offset.y
//
//        val chart = chartView
//
//        val width = width.toFloat()
//        val height = height.toFloat()
//
//        if (posX + mOffset2.x < 0) {
//            mOffset2.x = -posX
//        } else if (chart != null && posX + width + mOffset2.x > chart.width) {
//            mOffset2.x = chart.width - posX - width
//        }
//
//        if (posY + mOffset2.y < 0) {
//            mOffset2.y = -posY
//        } else if (chart != null && posY + height + mOffset2.y > chart.height) {
//            mOffset2.y = chart.height - posY - height
//            if(chart.height - posY - height<-300){
//                mOffset2.y=-650f
//            }
////            mOffset2.y = -3f
//        }
//
//        return mOffset2


        val offset = offset
        val chart = chartView
        val width = width.toFloat()
        val height = height.toFloat()
        // posY \posX 指的是markerView左上角点在图表上面的位置
        //处理Y方向
        if (posY <= height) {// 如果点y坐标小于markerView的高度，如果不处理会超出上边界，处理了之后这时候箭头是向上的，我们需要把图标下移一个箭头的大小
            offset.y = 0f
        } else {//否则属于正常情况，因为我们默认是箭头朝下，然后正常偏移就是，需要向上偏移markerView高度和arrow size，再加一个stroke的宽度，因为你需要看到对话框的上面的边框
            offset.y = -height  // 40 arrow height   5 stroke width
        }
        if (chart.height - posY - height < -300) {
            offset.y = -700f
        }
        //处理X方向，分为3种情况，1、在图表左边 2、在图表中间 3、在图表右边
        //
        if (posX > chart.width - width) {//如果超过右边界，则向左偏移markerView的宽度
            offset.x = -width - 15
        } else {//默认情况，不偏移（因为是点是在左上角）
            offset.x = 15f
            if (posX > width) {//如果大于markerView
                offset.x = -(width) - 15
            }
        }
        return offset
    }


}