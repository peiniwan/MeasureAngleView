package com.ly.measureangleview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.interfaces.datasets.IDataSet

class LineChartMarkerClick : LineChart {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var handled = true
        // if there is no marker view or drawing marker is disabled
        if (isShowingMarker && this.marker is FollowupMarkerView) {
            val markerView = this.marker as FollowupMarkerView
            val rect = Rect(
                markerView.drawingPosX.toInt(),
                markerView.drawingPosY.toInt(),
                markerView.drawingPosX.toInt() + markerView.width,
                markerView.drawingPosY.toInt() + markerView.height
            )
            if (rect.contains(event.x.toInt(), event.y.toInt())) {
                // touch on marker -> dispatch touch event in to marker
                markerView.dispatchTouchEvent(event)
            } else {
                handled = super.onTouchEvent(event)
            }
        } else {
            handled = super.onTouchEvent(event)
        }
        return handled
    }

    private val isShowingMarker: Boolean
        private get() = mMarker != null && isDrawMarkersEnabled && valuesToHighlight()


//    override fun drawMarkers(canvas: Canvas?) {
//
//        // if there is no marker view or drawing marker is disabled
//        if (mMarker == null || !isDrawMarkersEnabled || !valuesToHighlight()) return
//        for (i in mIndicesToHighlight.indices) {
//            val highlight = mIndicesToHighlight[i]
//            val set: IDataSet<*> = mData.getDataSetByIndex(highlight.dataSetIndex)
//            val e = mData.getEntryForHighlight(mIndicesToHighlight[i])
//            val entryIndex = set.getEntryIndex(e)
//
//            // make sure entry not null
////            if (e == null || entryIndex > set.entryCount * mAnimator.phaseX) continue
//            val pos = getMarkerPosition(highlight)
//
//            // check bounds
//            if (!mViewPortHandler.isInBounds(pos[0], pos[1])) continue
//
//            // callbacks to update the content
//            mMarker.refreshContent(e, highlight)
//
//            // draw the marker
//            mMarker.draw(canvas, pos[0], pos[1])
//        }
//    }

}