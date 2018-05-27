package csid.butterflyeffect.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

import csid.butterflyeffect.R;
import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

/**
 * Created by sy081 on 2018-05-13.
 */

public class WormsView extends View
{

    private boolean isPlaying;
    private boolean isColorSet;
    private ArrayList<UserInfo> userInfos;
    private Bitmap btmBody, newBtmBody;
    private Bitmap btmHead, newBtmHead;
    private ArrayList<ColorFilter> colorFilters;
    private int viewMode; // 0: worms, 1: skeleton

    public void setViewMode(int viewMode){
        this.viewMode = viewMode;
    }

    public int getViewMode(){
        return this.viewMode;
    }

    public void setColorFilters(ArrayList<UserInfo> userInfos){
        for(int user_idx = 0; user_idx < userInfos.size(); user_idx++) {
            int RGB = android.graphics.Color.rgb(userInfos.get(user_idx).getR(), userInfos.get(user_idx).getG(), userInfos.get(user_idx).getB());
            ColorFilter colorFilter = new LightingColorFilter(0x00000000, RGB);
            colorFilters.add(colorFilter);
        }
        isColorSet = true;
    }

    public ColorFilter getColorFilter(int index){
        return colorFilters.get(index);
    }

    public boolean getIsColorSet(){
        return isColorSet;
    }

    public WormsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setWillNotDraw(false);
        isPlaying = false;
        isColorSet = false;
        colorFilters = new ArrayList<>();
        btmBody = BitmapFactory.decodeResource(getResources(), R.drawable.worm_body);
        btmHead = BitmapFactory.decodeResource(getResources(), R.drawable.worm_head);
        viewMode = 0;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(userInfos != null)
        {
            for(int user_idx = 0; user_idx < userInfos.size(); user_idx++)
            {
                Paint paint = new Paint();

                Point2D[] userPoints = userInfos.get(user_idx).getKeyPoint().getSkeleton();

                if(!isPlaying)
                {
                    paint.setColor(Utils.getColor(user_idx));
                    if(! (userPoints[Constants.NECK].x == 0 || userPoints[Constants.NECK].y == 0))
                    {
                        paint.setTextSize(Constants.PLAYER_TEXT_SIZE);
                        paint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText("[ PLAYER "+(user_idx+1)+" ]", (float)userPoints[Constants.NECK].x, (float)userPoints[Constants.NECK].y, paint);

                    }
                }
                // Playing
                else
                {
                    if(this.viewMode == 0)
                    {
                        paint.setColorFilter(getColorFilter(user_idx));
                        // Draw body worms
                        if (isAvailable(userPoints[Constants.R_HIP], userPoints[Constants.L_HIP])) {
                            double rhx = userPoints[Constants.R_HIP].x;
                            double rhy = userPoints[Constants.R_HIP].y;

                            double lhx = userPoints[Constants.L_HIP].x;
                            double lhy = userPoints[Constants.L_HIP].y;

                            double mhx = (lhx + rhx) / 2;
                            double mhy = (lhy + rhy) / 2;

                            Point2D middle_hip = new Point2D(mhx, mhy);

                            if (isAvailable(userPoints[Constants.NECK], middle_hip)) {
                                double distance = Utils.getDistance(userPoints[Constants.NECK], middle_hip);
                                int target_size = (int) (distance / 3);

                                newBtmBody = Bitmap.createScaledBitmap(btmBody, target_size, target_size, false);

                                canvas.drawBitmap(newBtmBody, (int) (userPoints[Constants.NECK].x - (target_size / 2)), (int) (userPoints[Constants.NECK].y - (target_size / 2)), paint);
                                canvas.drawBitmap(newBtmBody, (int) ((mhx + userPoints[Constants.NECK].x) / 2 - (target_size / 2)), (int) ((mhy + userPoints[Constants.NECK].y) / 2 - (target_size / 2)), paint);
                                canvas.drawBitmap(newBtmBody, (int) (mhx - (target_size / 2)), (int) (mhy - (target_size / 2)), paint);
                            }
                        }

                        // Draw face worms
                        if (isAvailable(userPoints[Constants.L_EAR], userPoints[Constants.R_EAR], userPoints[Constants.NOSE])) {
                            double distance = Utils.getDistance(userPoints[Constants.L_EAR], userPoints[Constants.R_EAR]);
                            int target_size = (int) (distance * 2);
                            newBtmHead = Bitmap.createScaledBitmap(btmHead, target_size, target_size, false);
                            canvas.drawBitmap(newBtmHead, (int) (userPoints[Constants.NOSE].x - (target_size / 2)), (int) (userPoints[Constants.NOSE].y - (target_size / 2)), paint);
                        }
                    }
                    else if(this.viewMode == 1)
                    {
                        paint.setStrokeWidth(Constants.LINE_WIDTH);
                        paint.setColor(Utils.getColor(user_idx));
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
            }
        }
    }

    public boolean isAvailable(Point2D ...point){
        for(Point2D pt : point) {
            if(pt.x == 0 && pt.y == 0)
                return false;
        }
        return true;
    }

    public void drawWorms(ArrayList<UserInfo> userInfos){
        this.userInfos = userInfos;
        invalidate();
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}

