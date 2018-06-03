package csid.butterflyeffect.game.model;

import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.util.Constants;

/**
 * Created by hanseungbeom on 2018. 4. 3..
 */

public class KeyPoint {
    private Point2D[] skeleton;
    private int rgbRed;
    private int rgbGreen;
    private int rgbBlue;
    public KeyPoint(){
    }
    public KeyPoint(Point2D[] skeleton){
        this.skeleton = skeleton;
    }
    public void setSkeleton(KeyPoint keyPoint){
        this.skeleton = keyPoint.skeleton;
        this.rgbRed = keyPoint.rgbRed;
        this.rgbGreen = keyPoint.rgbGreen;
        this.rgbBlue = keyPoint.rgbBlue;
    }
    public void setSkeleton(Point2D[] skeleton){
        this.skeleton = skeleton;
    }
    public Point2D[] getSkeleton(){
        return skeleton;
    }
}
