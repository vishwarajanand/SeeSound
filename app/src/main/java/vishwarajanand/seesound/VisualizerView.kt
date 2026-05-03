package vishwarajanand.seesound

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * Created by dabba on 2/12/17.
 */
class VisualizerView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var amplitudes: PointsCircularIndexedArray? = null
    private var vectors: PointsCircularIndexedArray? = null
    private val colors: Colors = Colors()
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.viewWidth = w
        this.viewHeight = h
        
        val halfWidth = viewWidth / 2
        if (halfWidth <= 0) return

        // Left panel (amplitudes): 0 to halfWidth. Solid vertical lines.
        this.amplitudes = PointsCircularIndexedArray(halfWidth, 2) 
        // Right panel (vectors): halfWidth to viewWidth. Dotted points.
        this.vectors = PointsCircularIndexedArray(halfWidth, 1)

        // Initialize buffers so they start at the bottom of the screen
        for (i in 0 until halfWidth) {
            this.amplitudes?.add(viewHeight.toFloat(), viewHeight.toFloat())
            this.vectors?.add(viewHeight.toFloat())
        }
    }

    /**
     * adds amplitude and triggers redraw
     */
    fun addAmplitude(amplitude: Int) {
        if (viewHeight <= 0) return

        // Scale amplitude to screen height (leaving a small margin at top)
        val scaledHeight = (amplitude.toFloat() / MAX_AMPLITUDE) * (viewHeight - 20)
        val yTop = viewHeight - scaledHeight
        val yBottom = viewHeight.toFloat()
        
        // Add to both buffers
        amplitudes?.add(yBottom, yTop)
        vectors?.add(yTop)
        
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (viewWidth <= 0 || viewHeight <= 0) return

        colors.shuffle()
        canvas.drawPaint(colors.canvasPaint)
        
        val halfWidth = viewWidth / 2

        // Draw Left Panel: Solid Lines (0 to halfWidth)
        // Newest data appears at halfWidth and moves towards 0
        amplitudes?.let {
            canvas.drawLines(it.getIndexedArray(0), colors.linePaint)
        }
        
        // Draw Separator
        canvas.drawLine(halfWidth.toFloat(), viewHeight.toFloat(), halfWidth.toFloat(), 0f, colors.linePaint)
        
        // Draw Right Panel: Points (halfWidth to viewWidth)
        // Newest data appears at viewWidth and moves towards halfWidth
        vectors?.let {
            canvas.drawPoints(it.getIndexedArray(halfWidth), colors.circlePaint)
        }
    }

    companion object {
        private const val MAX_AMPLITUDE = 32767
    }
}
