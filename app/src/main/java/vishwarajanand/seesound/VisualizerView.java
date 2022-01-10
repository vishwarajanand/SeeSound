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

    private PointsCircularIndexedArray amplitudes;
    private PointsCircularIndexedArray vectors;
    private Colors colors;
    private int width;
    private int height;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        colors = new Colors();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        this.width = width;
        this.height = height;
        //show wave(amplitudes) and bar(vectors) graph in split width windows, effective width = width/2
        this.amplitudes = new PointsCircularIndexedArray(this.width / 2, 1); // xy for each point across the half-screen width
        this.vectors = new PointsCircularIndexedArray(this.width / 2, 2); // x0 y0 x1 y1 for each line across the half-screen width
    }

    /**
     * modifies draw arrays. cycles back to zero when amplitude samples reach max screen size
     */
    public void addAmplitude(int amplitude) {
        invalidate();
        float scaledHeight = ((float) amplitude / MAX_AMPLITUDE) * (height - 1);
        amplitudes.add(0, height - scaledHeight);  // xy
        vectors.add(0, height, 0, height - scaledHeight); // x0 y0 x1 y1
    }

    @Override
    public void onDraw(Canvas canvas) {
        colors.shuffle();
        canvas.drawPaint(colors.CanvasPaint);
        canvas.drawLines(vectors.getIndexedArray(0), colors.LinePaint);
        canvas.drawLine(width / 2, height, width / 2, 0, colors.LinePaint);
        canvas.drawPoints(amplitudes.getIndexedArray(this.width / 2), colors.CirclePaint);
    }
}