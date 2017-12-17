package vishwarajanand.seesound;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by dabba on 2/12/17.
 */

public class Colors {
    private static final int colorChangeFrameRate = 20;
    private static int colorChangeFrameSeq = 0;
    public Paint LinePaint;
    public Paint PointPaint;
    public Paint CanvasPaint;

    public Colors(){
        LinePaint = new Paint();
        LinePaint.setStrokeWidth(1);
        LinePaint.setColor(Color.GREEN);
        PointPaint = new Paint();
        PointPaint.setStrokeWidth(2);
        PointPaint.setColor(Color.GREEN);
        CanvasPaint = new Paint();
        CanvasPaint.setColor(Color.WHITE);
    }

    public void shuffle(){
        if(colorChangeFrameSeq-- >= 0){
            return;
        }
        colorChangeFrameSeq = colorChangeFrameRate;
        LinePaint.setColor(randomColor());
        PointPaint.setColor(randomColor());
        //CanvasPaint.setColor(randomColor());
    }

    private int randomColor(){
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r,g,b);
    }
}
