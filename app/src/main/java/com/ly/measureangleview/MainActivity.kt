package com.ly.measureangleview

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var llRoot = findViewById<LinearLayout>(R.id.llRoot)
        var textView = findViewById<TextView>(R.id.textView)

        var measureView = findViewById<MeasureAngleView>(R.id.measureView)
        var measureView2 = findViewById<MeasuerAngleView2>(R.id.measureView2)
        measureView2.setImageResource(R.drawable.abc)

        measureView.setOnAngleSelectedListener(object : MeasureAngleView.OnAngleSelectedListener {
            override fun onAngleSelected(angle: Int) {
                Log.d("measureView", "Angle selected: " + angle)

            }
        })
        measureView.setImageResource(R.drawable.abc)
        measureView.post {
            var d = resources.getDrawable(R.drawable.abc)
            var height =
                d.intrinsicHeight.toFloat() / d.intrinsicWidth.toFloat() * measureView.measuredWidth
            var cha = (measureView.measuredHeight - height) / 2
            val points = arrayOfNulls<Point>(4)
            points[0] = Point(0, cha.toInt())
            points[1] = Point(measureView.measuredWidth, cha.toInt())
            points[2] = Point(measureView.measuredWidth, (cha + height).toInt())
            points[3] = Point(0, (cha + height).toInt())

            points[2] = Point(measureView.measuredWidth, (measureView.measuredHeight).toInt())
            points[3] = Point(0, (measureView.measuredHeight).toInt())
            measureView.mCropPoints = points
        }

        textView.setOnClickListener {
            measureView.reset()
//            measureView.setShowLine(700,250,450,250,500,475)
            measureView.setShowLine(1332, 2916, 1476, 1728, 3276, 1116)
            measureView.setShowLine(0, 0, 200, 0, 200, 400)
        }



    }
}