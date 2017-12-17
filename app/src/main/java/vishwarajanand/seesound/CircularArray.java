package vishwarajanand.seesound;

import java.util.Arrays;

/**
 * Created by dabba on 17/12/17.
 */

public class CircularArray {
    final float[] array;
    final int size;
    final int buffer;
    int currentlyAdded;

    public CircularArray(int givenSize) {
        this(givenSize, 100);
    }

    private CircularArray(int givenSize, int bufferSize) {
        this.size = givenSize;
        this.buffer = bufferSize;
        this.currentlyAdded = 0;
        this.array = new float[size + buffer];
    }

    public boolean add(float... args) {
        int numValues = args.length;

        if (numValues <= 0 || currentlyAdded + numValues >= size + buffer || numValues >= buffer) {
            return false;
        }

        for (int i = 0; i < numValues; i++) {
            array[currentlyAdded + i] = args[i];
        }

        currentlyAdded += numValues;

        if (currentlyAdded >= size) {
            //shift to zero index in array
            // System.arraycopy(array, numValues, array, 0, currentlyAdded);
            for (int i = 0; i <= currentlyAdded; i += 2) {
                array[i + 1] = array[i + 1 + numValues];
            }
            currentlyAdded -= numValues;
        }

        return true;
    }

    public float[] getArray() {
        return Arrays.copyOfRange(array, 0, size);
    }
}
