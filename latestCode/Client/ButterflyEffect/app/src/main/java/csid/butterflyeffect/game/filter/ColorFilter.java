package csid.butterflyeffect.game.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import csid.butterflyeffect.PreviewSurface;

/**
 * Created by sy081 on 2018-03-11.
 */

public class ColorFilter {
    public int[] getRGBFromPixel(double[] x, double[] y) {
        Bitmap bitmap = PreviewSurface.curFrameImage();
        int length = x.length;
        int[] color = new int[length];
        for(int i = 0; i < length; i++) {
            color[i] = bitmap.getPixel((int)x[i], (int)y[i]);
        }
        return color;
    }

    public float compare(int RGB1, int RGB2) {
        float prob = 0f;
        //pixel1 = Color
        return prob;
    }
}
