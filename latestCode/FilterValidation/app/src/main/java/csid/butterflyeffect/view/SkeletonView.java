package csid.butterflyeffect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import csid.butterflyeffect.game.model.KeyPoint;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Point2D;
import csid.butterflyeffect.util.Utils;

public class SkeletonView extends View {
    private boolean isPlaying;
    public SkeletonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        isPlaying = false;
    }
    ArrayList<KeyPoint> keyPoints;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //투명화
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if(keyPoints!=null) {
            for (int i = 0; i < keyPoints.size(); i++) {
                Paint paint = new Paint();

                Point2D[] userPoints = keyPoints.get(i).getSkeleton();

                if(!isPlaying){//ready state
                    paint.setColor(Utils.getColor(i));
                        if(!(userPoints[Constants.NECK].x == 0 || userPoints[Constants.NECK].y == 0)) {
                            //canvas.drawCircle((float) userPoints[Constants.NECK].x, (float) userPoints[Constants.NECK].y, Constants.READY_CIRCLE_RADIUS, paint);
                            paint.setTextSize(Constants.PLAYER_TEXT_SIZE);
                            paint.setTextAlign(Paint.Align.CENTER);
                            canvas.drawText("[ PLAYER "+(i+1)+" ]", (float)userPoints[Constants.NECK].x, (float)userPoints[Constants.NECK].y, paint);
                        }
                }
                else {
                    paint.setStrokeWidth(Constants.LINE_WIDTH);
                    paint.setColor(Utils.getColor(i));
                    Utils.drawLine(canvas, paint, userPoints[Constants.NOSE], userPoints[Constants.L_EYE]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.NOSE], userPoints[Constants.R_EYE]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.L_EYE], userPoints[Constants.L_EAR]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.R_EYE], userPoints[Constants.R_EAR]);

                    Utils.drawLine(canvas, paint, userPoints[Constants.NECK], userPoints[Constants.L_SHOULDER]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.NECK], userPoints[Constants.R_SHOULDER]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.NECK], userPoints[Constants.L_SHOULDER]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.NECK], userPoints[Constants.R_HIP]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.NECK], userPoints[Constants.L_HIP]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.R_SHOULDER], userPoints[Constants.R_ELBOW]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.L_SHOULDER], userPoints[Constants.L_ELBOW]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.R_ELBOW], userPoints[Constants.R_WRIST]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.L_ELBOW], userPoints[Constants.L_WRIST]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.R_KNEE], userPoints[Constants.R_HIP]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.R_KNEE], userPoints[Constants.R_ANKLE]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.L_KNEE], userPoints[Constants.L_HIP]);
                    Utils.drawLine(canvas, paint, userPoints[Constants.L_KNEE], userPoints[Constants.L_ANKLE]);

                    paint.setStrokeWidth(Constants.SPECIAL_LINE_WIDTH);
                    paint.setColor(Color.WHITE);
                    Utils.drawLine(canvas, paint, userPoints[Constants.NOSE], userPoints[Constants.NECK]);
                    paint.setStrokeWidth(Constants.LINE_WIDTH);

                    for (int j = 0; j < userPoints.length; j++) {
                        if (!(userPoints[j].x == 0 || userPoints[j].y == 0)) {
                            paint.setColor(Color.WHITE);
                            canvas.drawCircle((float) userPoints[j].x, (float) userPoints[j].y, Constants.BIG_CIRCLE_RADIUS, paint);
                            int RGB = android.graphics.Color.rgb(userPoints[j].r, userPoints[j].g, userPoints[j].b);
                            paint.setColor(RGB);
                            canvas.drawCircle((float) userPoints[j].x, (float) userPoints[j].y, Constants.CIRCLE_RADIUS, paint);
                        }
                    }
                }

            }
            keyPoints = null;
        }
    }
    public void drawSkeletons(ArrayList<KeyPoint> keyPoints){
        this.keyPoints = keyPoints;
        invalidate();
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}



