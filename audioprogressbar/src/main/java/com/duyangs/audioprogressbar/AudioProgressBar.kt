package com.duyangs.audioprogressbar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * description: 音频播放进度条
 * author:杜洋
 * date:2020/03/29
 */
class AudioProgressBar : View {

    companion object {
        const val NORMAL = 0
        const val DYNAMIC = 1
    }

    private var primaryColor: Int = Color.BLACK//无信号颜色
    private var progressColor: Int = Color.WHITE//有信号颜色
    private var spacing: Int = 4.toPx() //间隙
    private var unitWidth: Int = 2.toPx() //信号柱宽度
    private var barStyle: Int = NORMAL //风格

    private var mPaint: Paint? = null

    private var mRectHeight: Int = 0
    private var mRectWidth: Int = 0

    private var progress = 0f

    private var itemsHeightList = arrayListOf<Int>()


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        setAttributeSet(attr)
        init()
    }

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ) {
        setAttributeSet(attr)
        init()
    }

    private fun setAttributeSet(attr: AttributeSet) {
        context?.let {
            val typedArray = it.obtainStyledAttributes(attr, R.styleable.AudioProgressBar)
            primaryColor =
                typedArray.getColor(R.styleable.AudioProgressBar_primary_color, primaryColor)
            progressColor =
                typedArray.getColor(R.styleable.AudioProgressBar_progress_color, progressColor)
            spacing =
                typedArray.getDimensionPixelSize(R.styleable.AudioProgressBar_spacing, spacing)
            unitWidth =
                typedArray.getDimensionPixelSize(R.styleable.AudioProgressBar_unit_width, unitWidth)
            barStyle = typedArray.getInt(R.styleable.AudioProgressBar_barStyle, barStyle)
            typedArray.recycle()
        }

    }

    private fun init() {
        mPaint = Paint()
        mPaint?.isAntiAlias
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initSize()
    }

    private fun initSize() {
        mRectHeight = height
        mRectWidth = unitWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))

    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        var height: Int
        // 测量模式，从xml可知
        val specMode = MeasureSpec.getMode(heightMeasureSpec)
        // 测量大小,从xml中获取
        val specSize = MeasureSpec.getSize(heightMeasureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize
        } else {
            height = 50
            // wrap_content模式，选择最小值
            if (specMode == MeasureSpec.AT_MOST) {
                height = height.coerceAtMost(specSize)
            }
        }
        mRectHeight = height
        return height
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        var width: Int
        // 测量模式，从xml可知
        val specMode = MeasureSpec.getMode(widthMeasureSpec)
        // 测量大小,从xml中获取
        val specSize = MeasureSpec.getSize(widthMeasureSpec)

        Log.d("SignalView", "measureWidth#specMode $specMode")
        if (specMode == MeasureSpec.EXACTLY) {
            width = specSize
        } else {
            width = 80
            // wrap_content模式，选择最小值
            if (specMode == MeasureSpec.AT_MOST) {
                width = width.coerceAtMost(specSize)
            }
        }
        return width
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPaint?.apply {
            strokeWidth = unitWidth.toFloat()
            strokeCap = Paint.Cap.SQUARE
            style = Paint.Style.FILL
            canvas(this, canvas)
        }
    }

    /**
     *
     */
    private fun canvas(paint: Paint, canvas: Canvas?) {
        val signalMaximum = width / (unitWidth + spacing)
        val progressNum = (signalMaximum.toFloat() * progress).toInt()
        for (i in 0 until signalMaximum) {
            paint.color = if (i <= progressNum) progressColor else primaryColor
            canvasLine(paint, canvas, i)
        }
    }

    private fun canvasLine(paint: Paint, canvas: Canvas?, index: Int) {
        val x = ((mRectWidth + spacing) * index).toFloat() + spacing
        val paintHeight = when (barStyle) {
            DYNAMIC -> randomHeight()
            else -> historyHeight(index)
        }
        val coefficient = paintHeight.toFloat() / mRectHeight.toFloat() / 2f
        val startY = 0.5f - coefficient
        val stopY = 0.5f + coefficient
        canvas?.drawLine(x, startY * mRectHeight, x, stopY * mRectHeight, paint)
    }

    private fun historyHeight(index: Int): Int {
        return if (itemsHeightList.size > index) {
            itemsHeightList[index]
        } else {
            itemsHeightList.add(randomHeight())
            itemsHeightList[index]
        }
    }

    private fun randomHeight(): Int {
        val randomNum = ((4..9).random().toFloat() / 10f)
        return (height * randomNum).toInt()
    }

    /**
     * 设置当前进度
     * @param progress 当前进度 [Float]
     */
    fun setProgress(progress: Float) {
        if ((progress > 1f) or (progress < 0f)) return
        if (this.progress != progress) {
            this.progress = progress
            this.invalidate()
        }
    }

    /**
     * 设置进度调风格
     * @param style 仅支持 [NORMAL] [DYNAMIC]
     */
    fun setBarStyle(style: Int) {
        this.barStyle = style
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}
