package csid.butterflyeffect.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.model.UserInfo;

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
        //Log.d("STRING", serverStr);

        //TODO we have to sure that the data decided before we use it.
        float ratio_X = Constants.PREVIEW_WIDTH / Constants.CAMERA_WIDTH;
        float ratio_Y = Constants.PREVIEW_HEIGHT / Constants.CAMERA_HEIGHT;


        ArrayList<Point2D[]> rtnArray = new ArrayList<>();
        int len = serverStr.length();
        String strNumOfDetectedPeople = serverStr.substring(0, 1);
        int numOfDetectedPeople = Integer.parseInt(strNumOfDetectedPeople);
        serverStr = serverStr.substring(3, len);

        String[] detected = serverStr.split("; ");
        for(int i=0;i<numOfDetectedPeople;i++){
            String[] tokens = detected[i].split(",");
            Point2D[] points = new Point2D[tokens.length/2];
            for(int j=0;j<Constants.KEYPOINT_NUM;j++){
                points[j] = new Point2D(Double.parseDouble(tokens[j*2])*ratio_X,Double.parseDouble(tokens[j*2+1])*ratio_Y);
            }
            rtnArray.add(points);
        }

        return rtnArray;
    }

    //sub method
    //transfer Point2D[] to degree
    public static double getDegree(Point2D[] keyPoints){
        double degree = 0;
        Point2D nose = keyPoints[Constants.NOSE];
        Point2D leftShoulder = keyPoints[Constants.L_SHOULDER];
        Point2D rightShoulder = keyPoints[Constants.R_SHOULDER];
        Point2D mid = new Point2D(Math.abs((rightShoulder.x + leftShoulder.x)/2), Math.abs((rightShoulder.y + leftShoulder.y)/2));
        double angle1 = Math.atan2(leftShoulder.y - leftShoulder.y, leftShoulder.x - rightShoulder.x); //프레임과 동일한 수직선
        double angle2 = Math.atan2(mid.y-nose.y , mid.x - nose.x);
        degree = (angle2 - angle1) * 180 / Math.PI;
        if(degree < 0){
            degree += 360;
        }
        //각도 계산
        return degree;
    }

    //if two elbows points are above your body.
    public static boolean getBoost(Point2D[] keyPoints){
        double a,b;
        boolean boost;
        if(keyPoints[Constants.L_ELBOW].x==keyPoints[Constants.R_ELBOW].x){
             return true;
        }
        else{
            a = (keyPoints[Constants.L_ELBOW].y-keyPoints[Constants.R_ELBOW].y)/(keyPoints[Constants.L_ELBOW].x-keyPoints[Constants.R_ELBOW].x);
            b = keyPoints[Constants.L_ELBOW].y-a*keyPoints[Constants.L_ELBOW].x;
            if( keyPoints[Constants.NECK].y >a*keyPoints[Constants.NECK].x + b)
               return true;
            else
               return false;

        }
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

    //transfer boost[] to "2 1 0"
    public static String boostToStr(boolean[] degrees){
        String rtnStr = "";
        rtnStr += String.valueOf(degrees.length)+" ";
        for(int i=0;i<degrees.length;i++)
            rtnStr+= (degrees[i])?"1 ":"0 ";

        return rtnStr;
    }

    public static double getFristAngle(String str){
        StringTokenizer st = new StringTokenizer(str," " );
        st.nextToken();
        return Double.parseDouble(st.nextToken());

    }
    public static void drawLine(Canvas c, Paint paint, Point2D p1, Point2D p2){
        if(!(p1.x ==0 || p1.y ==0 || p2.x==0 || p2.y==0 ) ) {
            c.drawLine((float) p1.x, (float) p1.y,
                    (float) p2.x, (float) p2.y, paint);
        }
    }

    public static double getDistance(Point2D p1, Point2D p2){
        return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+ (p1.y-p2.y)*(p1.y-p2.y));
    }

    public static ArrayList<Point2D[]> getPlainKeyPoint(ArrayList<UserInfo> users)
    {
        ArrayList<Point2D[]> userKeypoints = new ArrayList<>();
        
         for(int user=0;user<users.size();user++){
                Point2D[] keypoint = new Point2D[Constants.KEYPOINT_NUM];
                for(int i=0;i<Constants.KEYPOINT_NUM;i++){
                    if(i==Constants.NECK)
                        keypoint[i] = users.get(user).getNeck();
                    else
                        keypoint[i] = new Point2D();
                }
                userKeypoints.add(keypoint);
        }

        return userKeypoints;
    }
}
