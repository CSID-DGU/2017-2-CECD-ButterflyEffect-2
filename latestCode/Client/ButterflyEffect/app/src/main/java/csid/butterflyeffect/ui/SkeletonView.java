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

/**
 * Created by hanseungbeom on 2018. 2. 19..
 */

public class SkeletonView extends View {

        private Canvas mCanvas;

        public SkeletonView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //투명화
            //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvas = canvas;
            Log.d("#####","2");

        }

        public void drawSkeletons(ArrayList<Point2D[]> keyPoints){
           // mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            Log.d("#####","3");

            for(int i=0;i<keyPoints.size();i++){
                Paint paint = new Paint();
                paint.setColor(Utils.getColor(i));
                Point2D[] userPoints = keyPoints.get(i);
                for(int j=0;j<userPoints.length;j++){
                    mCanvas.drawCircle((float)userPoints[j].x,(float)userPoints[j].y, Constants.CIRCLE_RADIUS,paint);
                }
            }
        }

    }




