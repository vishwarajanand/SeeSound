package vishwarajanand.seesound

/**
 * Created by dabba on 17/12/17.
 */
class PointsCircularIndexedArray(
    val numElements: Int,
    val numPointsPerElement: Int,
) {
    private val numValuesPerElement = numPointsPerElement * 2
    val bufferArray: FloatArray
    val bufferSize: Int = numElements * numPointsPerElement
    var currentPos: Int = 0

    init {
        this.bufferArray = FloatArray(this.bufferSize)
    }

    /**
     * Adds Y coordinates for the current element.
     * For 1 point per element (points), expects 1 value.
     * For 2 points per element (lines), expects 2 values.
     */
    fun add(vararg yValues: Float): Boolean {
        if (yValues.size != numPointsPerElement) {
            return false
        }
        
        for (i in yValues.indices) {
            bufferArray[(currentPos + i) % bufferSize] = yValues[i]
        }
        currentPos = (currentPos + numPointsPerElement) % bufferSize
        return true
    }

    /**
     * Returns a FloatArray formatted for Canvas.drawPoints or Canvas.drawLines.
     * [x0, y0, x1, y1, ...]
     * The array is constructed such that the newest element is at far right 
     * (startIndexMargin + numElements) and the oldest is at far left (startIndexMargin).
     */
    fun getIndexedArray(startIndexMargin: Int): FloatArray {
        val outputBuffer = FloatArray(numElements * numValuesPerElement)
        
        for (i in 0 until numElements) {
            // i is the screen offset from left to right (0 to numElements-1)
            // We want to map this to the circular buffer such that the newest data is at the end of the screen range.
            // The newest data in buffer is just before currentPos.
            // Oldest data is at currentPos.
            
            val bufferIdx = (currentPos + (i * numPointsPerElement)) % bufferSize
            val outIdx = i * numValuesPerElement
            val x = (startIndexMargin + i).toFloat()
            
            if (numPointsPerElement == 1) {
                // Points: [x, y]
                outputBuffer[outIdx] = x
                outputBuffer[outIdx + 1] = bufferArray[bufferIdx]
            } else if (numPointsPerElement == 2) {
                // Lines: [x0, y0, x1, y1]
                outputBuffer[outIdx] = x
                outputBuffer[outIdx + 1] = bufferArray[bufferIdx]
                outputBuffer[outIdx + 2] = x
                outputBuffer[outIdx + 3] = bufferArray[(bufferIdx + 1) % bufferSize]
            }
        }
        return outputBuffer
    }
}
