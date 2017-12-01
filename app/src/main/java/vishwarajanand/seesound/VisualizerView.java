package vishwarajanand.seesound;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dabba on 2/12/17.
 */

public class VisualizerView extends View {
    private static final int MAX_AMPLITUDE = 32767;

    private float[] amplitudes;
    private float[] vectors;
    private int insertIdx = 0;
    private Colors colors;
    private int width;
    private int height;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        colors = new Colors();
    }

    @Override
    protected void onSizeChanged(int width, int h, int oldw, int oldh) {
        this.width = width;
        this.height = h;
        this.amplitudes = new float[this.width * 2]; // xy for each point across the width
        this.vectors = new float[this.width * 4]; // xxyy for each line across the width
    }

    /**
     * modifies draw arrays. cycles back to zero when amplitude samples reach max screen size
     */
    public void addAmplitude(int amplitude) {
        invalidate();
        float scaledHeight = ((float) amplitude / MAX_AMPLITUDE) * (height - 1);
        int ampIdx = insertIdx * 2;
        amplitudes[ampIdx++] = insertIdx;   // x
        amplitudes[ampIdx] = scaledHeight;  // y
        int vectorIdx = insertIdx * 4;
        vectors[vectorIdx++] = insertIdx;   // x0
        vectors[vectorIdx++] = 0;           // y0
        vectors[vectorIdx++] = insertIdx;   // x1
        vectors[vectorIdx] = scaledHeight;  // y1
        // insert index must be shorter than screen width
        insertIdx = ++insertIdx >= width ? 0 : insertIdx;
    }

    @Override
    public void onDraw(Canvas canvas) {
        colors.shuffle();
        canvas.drawPaint(colors.CanvasPaint);
        canvas.drawLines(vectors, colors.LinePaint);
        canvas.drawPoints(amplitudes, colors.PointPaint);
    }
}