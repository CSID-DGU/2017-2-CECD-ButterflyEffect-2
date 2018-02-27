package csid.butterflyeffect.util;

import android.graphics.Bitmap;

/**
 * Created by sy081 on 2018-02-27.
 */

public class Comparator {
    public static double compareTwoBitmaps(Bitmap img1, Bitmap img2){
        int width1 = img1.getWidth();
        int height1 = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();

        int[][] buffer1 = new int[width1][height1];
        int[][] buffer2 = new int[width2][height2];

        int minWidth = 0, minHeight = 0;
        int equality = 0;
        if(width1 > width2){
            minWidth = width2;
        }
        else{
            minWidth = width1;
        }

        if(height1 > height2){
            minHeight = height2;
        }else{
            minHeight = height1;
        }
        for(int x = 0; x < minWidth; x++){
            for(int y = 0; y < minHeight; y++){
                buffer1[x][y] = img1.getPixel(x, y);
                buffer2[x][y] = img2.getPixel(x, y);
                if(buffer1[x][y] == buffer2[x][y])
                    equality = equality + 1;
            }
        }
        return (100 * equality) / (minWidth * minHeight);
    }
}
