package com.ycw.drawunlock_my

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.StringBuilder


/**
 * @Description
 * @Author 闫彩威
 * @QQ
 */
class MoveDrawUnlock : View{

    //圆的半径
    private var radius = 0f
    //间距
    private var padding = 0f
    //记录九个点的信息
    private val dotInfos = mutableListOf<DotInfo>()
    //保存点亮的点
    private val selectedDots = mutableListOf<DotInfo>()
    //点与点之间的线
    private val linePath = Path()
    //线条的画笔
    private val linePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    //圆点内部白色填充圆画笔
    private val innerCirclePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL_AND_STROKE
    }
    private var lastSelectedDot: DotInfo? = null
    //触摸点坐标
    private var touchPoint = Point()

    //记录密码
    private val password = StringBuilder()

    //构造方法
    constructor(context: Context,attributeSet: AttributeSet?,style:Int):super(context,attributeSet,style)
    constructor(context: Context,attributeSet: AttributeSet?):super(context,attributeSet,0)
    constructor(context: Context):super(context,null,0)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        //画线
        canvas?.drawPath(linePath,linePaint)
        //画点与触摸点的线
        if (!touchPoint.equals(0,0)){
            canvas?.drawLine(lastSelectedDot!!.x,lastSelectedDot!!.y,
                touchPoint.x.toFloat(),touchPoint.y.toFloat(),linePaint)
        }
        //绘制九个点
        drawNineDot(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                //点亮点
                containPoint(event.x.toInt(),event.y.toInt()).also {
                    if (it != null){
                        highlightDot(it)
                        //将线的起点确定
                        linePath.moveTo(it.x,it.y)
                        //绑定上一个点
                        lastSelectedDot = it
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                //点亮点
                containPoint(event.x.toInt(),event.y.toInt()).also {
                    if (it != null){
                        if (!it.isSelected){
                            if (lastSelectedDot != null){
                                //连线
                                linePath.lineTo(it.x,it.y)
                            }else{
                                linePath.moveTo(it.x,it.y)
                            }
                            highlightDot(it)
                            //绑定上一个点
                            lastSelectedDot = it
                            //重设触摸点
                            touchPoint.set(0,0)
                        }
                    }else{
                        //防止触摸点还没碰到圆点时也记录触摸点坐标
                        if (lastSelectedDot != null){
                            touchPoint.set(event.x.toInt(),event.y.toInt())
                            invalidate()
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.v("test",password.toString())
                reset()
            }
        }
        return true
    }

    //初始化
    private fun init(){
        //记录中心点坐标
        var cx = 0f
        var cy = 0f

        //计算出半径和圆之间的间距
        if (measuredHeight >= measuredWidth){
            radius = measuredWidth/5/2f
            padding = (measuredWidth - radius*2*3) / 4
            cx = padding + radius
            cy = (measuredHeight - measuredWidth)/2 + padding + radius
        }else{
            radius = measuredHeight/5/2f
            padding = (measuredHeight - radius*2*3) / 4
            cx = (measuredWidth - measuredHeight)/2 + padding + radius
            cy = padding + radius
        }

        //添加dot信息
        for (row in 0..2){
            for (column in 0..2){
                dotInfos.add(DotInfo(
                    cx+column*(2*radius+padding),
                    cy+row*(2*radius+padding),
                    radius,
                    row*3+column+1))
            }
        }

    }

    //绘制九个点
    private fun drawNineDot(canvas: Canvas?){
        dotInfos.forEach {
            canvas?.drawCircle(it.x,it.y,it.radius,it.getDotPaint())
            canvas?.drawCircle(it.x,it.y,it.radius-4,innerCirclePaint)

            if (it.isSelected){
                canvas?.drawCircle(it.x,it.y,it.innerCircleRadius,it.getDotPaint())
            }
        }
    }

    //判断点是否在圆内
    private fun containPoint(tx: Int,ty: Int): DotInfo? {
        dotInfos.forEach {
            if (it.rect.contains(tx,ty)){
                return it
            }
        }
        return null
    }

    //点亮点
    private fun highlightDot(item: DotInfo){
        item.isSelected = true
        //添加密码值
        password.append(item.tag)
        invalidate()

        //添加点亮点
        selectedDots.add(item)
    }

    //重载
    private fun reset(){
        selectedDots.clear()
        dotInfos.forEach {
            it.isSelected = false
        }
        touchPoint.set(0,0)
        linePath.reset()
        lastSelectedDot = null
        password.clear()
        //刷新
        invalidate()

    }

}