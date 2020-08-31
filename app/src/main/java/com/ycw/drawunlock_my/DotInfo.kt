package com.ycw.drawunlock_my

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

/**
 * @Description
 * @Author 闫彩威
 * @QQ
 */
class DotInfo(var x:Float,var y :Float,var radius: Float,val tag: Int) {
    private val paint = Paint().apply {
        strokeWidth = 5f
        style = Paint.Style.FILL
    }

    val rect = Rect().apply {
        left = (x-radius).toInt()
        top = (y-radius).toInt()
        right = (x+radius).toInt()
        bottom = (y+radius).toInt()
    }

    var isSelected = false

    var innerCircleRadius = radius / 3f

    fun getDotPaint():Paint{
        if (isSelected){
            return paint.apply { color = Color.RED }
        }else{
            return paint.apply { color = Color.BLACK }
        }
    }
}