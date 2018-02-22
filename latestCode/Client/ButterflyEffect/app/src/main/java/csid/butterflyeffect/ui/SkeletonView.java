package csid.butterflyeffect.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.game.Point2D;
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
                float ratio_X = Constants.PREVIEW_WIDTH / Constants.CAMERA_WIDTH;
                float ratio_Y = Constants.PREVIEW_HEIGHT / Constants.CAMERA_HEIGHT;
                for(int j=0;j<userPoints.length;j++){
                    if(!(userPoints[j].x == 0 || userPoints[j].y == 0))
                        canvas.drawCircle(ratio_X * (float) userPoints[j].x, ratio_Y * (float) userPoints[j].y, Constants.CIRCLE_RADIUS, paint);
                }

            }
            keyPoints = null;
        }
    }


    public void drawSkeletons(ArrayList<Point2D[]> keyPoints){
        this.keyPoints = keyPoints;
        invalidate();
    }

}



