package csid.butterflyeffect.util;

import android.util.Log;

/**
 * Created by sy0814k on 18. 2. 1.
 */

public class Point2D {
    public double x;
    public double y;
    public int r,g,b;

    public Point2D(){
        x = 0;
        y = 0;
        r = -1;
        g = -1;
        b = -1;
    }
    public Point2D(double x, double y){
        this.x = x;
        this.y = y;
    }
    public void setX(double x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }

}
