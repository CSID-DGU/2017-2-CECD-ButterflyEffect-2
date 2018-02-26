package csid.butterflyeffect.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

                Point2D[] userPoints = keyPoints.get(i);

                paint.setStrokeWidth(Constants.LINE_WIDTH);
                paint.setColor(Utils.getColor(i));
                Utils.drawLine(canvas,paint,userPoints[Constants.NOSE],userPoints[Constants.L_EYE]);
                Utils.drawLine(canvas,paint,userPoints[Constants.NOSE],userPoints[Constants.R_EYE]);
                Utils.drawLine(canvas,paint,userPoints[Constants.L_EYE],userPoints[Constants.L_EAR]);
                Utils.drawLine(canvas,paint,userPoints[Constants.R_EYE],userPoints[Constants.R_EAR]);
                Utils.drawLine(canvas,paint,userPoints[Constants.NOSE],userPoints[Constants.NECK]);
                Utils.drawLine(canvas,paint,userPoints[Constants.NECK],userPoints[Constants.L_SHOULDER]);
                Utils.drawLine(canvas,paint,userPoints[Constants.NECK],userPoints[Constants.R_SHOULDER]);
                Utils.drawLine(canvas,paint,userPoints[Constants.NECK],userPoints[Constants.L_SHOULDER]);
                Utils.drawLine(canvas,paint,userPoints[Constants.NECK],userPoints[Constants.R_HIP]);
                Utils.drawLine(canvas,paint,userPoints[Constants.NECK],userPoints[Constants.L_HIP]);
                Utils.drawLine(canvas,paint,userPoints[Constants.R_SHOULDER],userPoints[Constants.R_ELBOW]);
                Utils.drawLine(canvas,paint,userPoints[Constants.L_SHOULDER],userPoints[Constants.L_ELBOW]);
                Utils.drawLine(canvas,paint,userPoints[Constants.R_ELBOW],userPoints[Constants.R_WRIST]);
                Utils.drawLine(canvas,paint,userPoints[Constants.L_ELBOW],userPoints[Constants.L_WRIST]);
                Utils.drawLine(canvas,paint,userPoints[Constants.R_KNEE],userPoints[Constants.R_HIP]);
                Utils.drawLine(canvas,paint,userPoints[Constants.R_KNEE],userPoints[Constants.R_ANKLE]);
                Utils.drawLine(canvas,paint,userPoints[Constants.L_KNEE],userPoints[Constants.L_HIP]);
                Utils.drawLine(canvas,paint,userPoints[Constants.L_KNEE],userPoints[Constants.L_ANKLE]);


                paint.setColor(Color.WHITE);
                for(int j=0;j<userPoints.length;j++){
                    if(!(userPoints[j].x == 0 || userPoints[j].y == 0))
                        canvas.drawCircle((float) userPoints[j].x, (float) userPoints[j].y, Constants.CIRCLE_RADIUS, paint);
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



