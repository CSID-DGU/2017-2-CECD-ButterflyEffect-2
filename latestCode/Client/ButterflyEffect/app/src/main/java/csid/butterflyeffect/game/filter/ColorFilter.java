package csid.butterflyeffect.game.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    public ArrayList<Float> compare(ArrayList<Integer> target, ArrayList<Integer> comp) {
        int compSize = comp.size();
        int targetSize = target.size();
        ArrayList<Float> prob = new ArrayList<>();
        for(int i = 0; i < compSize; i++) {
            int p = 0;
            for(int j = 0; j < targetSize; j++) {
                if(comp.get(i) == target.get(j)) {
                    p++;
                }
            }
            prob.add((float)((p*100)/targetSize));
        }
        return prob;
    }
}
