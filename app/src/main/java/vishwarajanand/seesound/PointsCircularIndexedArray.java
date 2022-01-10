package vishwarajanand.seesound;

/**
 * Created by dabba on 17/12/17.
 */

public class PointsCircularIndexedArray {
    final float[] bufferArray;
    final int bufferSize, numElements, numPointsPerElement, numValuesPerPoint;
    int currentPos;

    PointsCircularIndexedArray(int numElements, int numPointsPerElement) {
        this(numElements, numPointsPerElement, 2);
    }

    PointsCircularIndexedArray(int numElements, int numPointsPerElement, int numValuesPerPoint) {
        this.currentPos = 0;

        this.numElements = numElements;
        this.numPointsPerElement = numPointsPerElement;
        this.numValuesPerPoint = numValuesPerPoint;

        this.bufferSize = numElements * numPointsPerElement * numValuesPerPoint;
        this.bufferArray = new float[this.bufferSize];
    }

    public boolean add(float... args) {
        int numInputValues = args.length;

        if (numInputValues != numPointsPerElement * numValuesPerPoint) {
            // no valid values or too many values  for buffer
            return false;
        }

        for (int i = 0; i < numInputValues; i++) {
            bufferArray[(currentPos + i) % bufferSize] = args[i];
        }
        currentPos = (currentPos + numInputValues) % bufferSize;

        return true;
    }

    public float[] getArray() {
        return this.getIndexedArray(0);
    }

    public float[] getIndexedArray(int startIndexMargin) {
        float[] outputBuffer = new float[this.bufferSize];
        int actualPos = 0;
        for (int i = 0; i < numElements; i++) {
            for (int j = 0; j < numPointsPerElement; j++) {
                actualPos = i * numPointsPerElement * numValuesPerPoint + j * numPointsPerElement;
                outputBuffer[actualPos] = i + startIndexMargin;
                for (int k = 1; k < numValuesPerPoint; k++) {
                    actualPos++;
                    outputBuffer[actualPos] = bufferArray[(currentPos + actualPos) % bufferSize];
                }
            }
        }

        return outputBuffer;
    }
}
