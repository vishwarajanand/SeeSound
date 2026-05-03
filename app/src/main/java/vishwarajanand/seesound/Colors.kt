package vishwarajanand.seesound

import android.graphics.Color
import android.graphics.Paint
import java.util.Random

class Colors {
    var linePaint: Paint = Paint().apply {
        strokeWidth = 3f
        color = Color.GREEN
        isAntiAlias = true
    }
    var circlePaint: Paint = Paint().apply {
        strokeWidth = 10f
        color = Color.RED
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }
    var canvasPaint: Paint = Paint().apply {
        color = Color.WHITE
    }

    private var colorChangeFrameSeq = 0
    private val rand = Random()

    fun shuffle() {
        if (colorChangeFrameSeq-- > 0) {
            return
        }
        colorChangeFrameSeq = COLOR_CHANGE_FRAME_RATE
        // Ensure colors are not too light (to contrast with white background)
        linePaint.color = randomDarkColor()
        circlePaint.color = randomDarkColor()
    }

    private fun randomDarkColor(): Int {
        val r = rand.nextInt(200)
        val g = rand.nextInt(200)
        val b = rand.nextInt(200)
        return Color.rgb(r, g, b)
    }

    companion object {
        private const val COLOR_CHANGE_FRAME_RATE = 20
    }
}
