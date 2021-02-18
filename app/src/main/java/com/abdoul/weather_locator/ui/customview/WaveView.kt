package com.abdoul.weather_locator.ui.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.abdoul.weather_locator.R
import kotlin.math.sin

class WaveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var amplitude = 20f.toDp() // scale
    private var speed = 0f
    private val path = Path()
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createPath()
    }

    override fun onDraw(c: Canvas) = c.drawPath(path, paint)

    private fun createPath() {
        path.reset()
        paint.color = ContextCompat.getColor(context, R.color.background) //Color.parseColor("#1da6f9")
        path.moveTo(0f, height.toFloat())
        path.lineTo(0f, amplitude)
        var i = 0
        while (i < width + 10) {
            val wx = i.toFloat()
            val wy =
                amplitude * 2 + amplitude * sin((i + 10) * Math.PI / WAVE_AMOUNT_ON_SCREEN + speed).toFloat()
            path.lineTo(wx, wy)
            i += 10
        }
        path.lineTo(width.toFloat(), height.toFloat())
        path.close()
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    companion object {
        const val WAVE_AMOUNT_ON_SCREEN = 500
    }

    private fun Float.toDp() = this * context.resources.displayMetrics.density
}