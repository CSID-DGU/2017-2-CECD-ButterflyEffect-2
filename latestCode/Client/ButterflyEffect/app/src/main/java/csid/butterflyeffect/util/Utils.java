package csid.butterflyeffect.util;

import android.graphics.Point;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by hanseungbeom on 2018. 1. 28..
 */

public class Utils {

    public static byte[] intTobyte(int integer, ByteOrder order) {

        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
        buff.order(order);

        // 인수로 넘어온 integer을 putInt로설정
        buff.putInt(integer);

        return buff.array();
    }

    public static double getDegree(Point2D centerPoint, Point2D targetPoint) {
        double degree = Math.toDegrees(Math.atan2(targetPoint.y - centerPoint.y, targetPoint.x - centerPoint.x));
        if (degree < 0) {
            degree += 360;
        }
        return degree;
    }

    public static String stringToDegree(String str) {
        int len = str.length();
        String strNumOfDetectedPeople = str.substring(0, 1);
        int numOfDetectedPeople = Integer.parseInt(strNumOfDetectedPeople);
        double[] degree = new double[numOfDetectedPeople];
        str = str.substring(2, len);
        String[] detected = str.split("; ");
        for (int i = 0; i < numOfDetectedPeople; i++) {
            String[] tokens = detected[i].split(", ");
            Point2D centerPoint = new Point2D(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
            Point2D targetPoint = new Point2D(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
            degree[i] = getDegree(centerPoint, targetPoint);
        }

        String rtnStr = "";
        rtnStr += strNumOfDetectedPeople+" ";
        for(int i=0;i<numOfDetectedPeople;i++)
            rtnStr+= String.valueOf(String.format("%.2f",degree[i])+" ");

        return rtnStr;
    }
}
