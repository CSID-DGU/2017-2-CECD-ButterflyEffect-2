package csid.butterflyeffect.util;

import android.graphics.Color;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import csid.butterflyeffect.game.Point2D;

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

    /*public static double getDegree(Point2D centerPoint, Point2D targetPoint) {
        double degree = Math.toDegrees(Math.atan2(targetPoint.y - centerPoint.y, targetPoint.x - centerPoint.x));
        if (degree < 0) {
            degree += 360;
        }
        return degree;
    }*/



    //main method
    //transfer server string to "2 45.5 130.2"
    public static String stringToDegree(String str) {
        ArrayList<Point2D[]> userKeypoints = stringToKeyPoints(str);
        double degrees[] = new double[userKeypoints.size()];
        for(int i=0;i<userKeypoints.size();i++){
            degrees[i] = getDegree(userKeypoints.get(i));
        }
        return degreesToStr(degrees);
    }

    //sub method
    //transfer serverString to Point2D[] arraylists
    public static ArrayList<Point2D[]> stringToKeyPoints(String serverStr){
        Log.d("STRING", serverStr);
        ArrayList<Point2D[]> rtnArray = new ArrayList<>();
        int len = serverStr.length();
        String strNumOfDetectedPeople = serverStr.substring(0, 1);
        int numOfDetectedPeople = Integer.parseInt(strNumOfDetectedPeople);
        serverStr = serverStr.substring(3, len);

        String[] detected = serverStr.split("; ");
        for(int i=0;i<numOfDetectedPeople;i++){
            String[] tokens = detected[i].split(",");
            Point2D[] points = new Point2D[tokens.length/2];
            for(int j=0;j<18;j++){
                points[j] = new Point2D(Double.parseDouble(tokens[j*2]),Double.parseDouble(tokens[j*2+1]));
            }
            rtnArray.add(points);
        }

        return rtnArray;
    }

    //sub method
    //transfer Point2D[] to degree
    //TODO keyPoints 를 받아서 필요한 것만 써서 각을 구해야 함. 왼쪽-오른어깨의 중점과, 코를 이은 직선 // 왼오른 어깨의 직선 사이의 각 구하기
    public static double getDegree(Point2D[] keyPoints){
        double degree = 0;
        Point2D nose = keyPoints[Constants.NOSE];
        Point2D leftShoulder = keyPoints[Constants.L_SHOULDER];
        Point2D rightShoulder = keyPoints[Constants.R_SHOULDER];
        Point2D mid = new Point2D(Math.abs((rightShoulder.x + leftShoulder.x)/2), Math.abs((rightShoulder.y + leftShoulder.y)/2));
        double angle1 = Math.atan2(rightShoulder.y - rightShoulder.y, rightShoulder.x - leftShoulder.x); //프레임과 동일한 수직선
        double angle2 = Math.atan2(nose.y - mid.y, nose.x - mid.x);
        degree = (angle2 - angle1) * 180 / Math.PI;
        if(degree < 0){
            degree += 360;
        }
        //각도 계산
        return degree;
    }
    public static int getColor(int index){
        switch (index){
            case 0:
                return Color.GREEN;

            case 1:
                return Color.RED;
            case 2:
                return Color.BLUE;
            default:
                return Color.MAGENTA;
        }
    }

    //sub method
    //transfer degrees[] to "2 45.3 130.5"
    public static String degreesToStr(double[] degrees){
        String rtnStr = "";
        rtnStr += String.valueOf(degrees.length)+" ";
        for(int i=0;i<degrees.length;i++)
            rtnStr+= String.valueOf(String.format("%.2f",degrees[i])+" ");

        return rtnStr;
    }



    public static double getFristAngle(String str){
        StringTokenizer st = new StringTokenizer(str," " );
        st.nextToken();
        return Double.parseDouble(st.nextToken());

    }


}
