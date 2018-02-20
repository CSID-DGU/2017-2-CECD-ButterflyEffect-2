package csid.butterflyeffect.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Point2D;
import csid.butterflyeffect.util.Utils;

public class SkeletonView extends View {


    public SkeletonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }
    ArrayList<Point2D[]> keyPoints;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //투명화
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if(keyPoints!=null) {
            for (int i = 0; i < keyPoints.size(); i++) {
                Paint paint = new Paint();
                paint.setColor(Utils.getColor(i));
                Point2D[] userPoints = keyPoints.get(i);
                float ratio_X = 950 / 1280f;
                float ratio_Y = 800 / 960f;
                canvas.drawCircle(ratio_X * (float) userPoints[0].x, ratio_Y * (float) userPoints[0].y, Constants.CIRCLE_RADIUS, paint);
                canvas.drawCircle(ratio_X * (float) userPoints[2].x, ratio_Y * (float) userPoints[2].y, Constants.CIRCLE_RADIUS, paint);
                canvas.drawCircle(ratio_X * (float) userPoints[5].x, ratio_Y * (float) userPoints[5].y, Constants.CIRCLE_RADIUS, paint);
            }
            keyPoints = null;
        }
    }


    public void drawSkeletons(ArrayList<Point2D[]> keyPoints){
        this.keyPoints = keyPoints;
        invalidate();
    }

}



