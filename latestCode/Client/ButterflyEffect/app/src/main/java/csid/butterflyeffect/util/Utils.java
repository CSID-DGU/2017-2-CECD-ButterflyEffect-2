package csid.butterflyeffect.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.StringTokenizer;

import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.model.KeyPoint;
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
        for (int i = 0; i < userKeypoints.size(); i++) {
            degrees[i] = getDegree(userKeypoints.get(i));
        }
        return degreesToStr(degrees);
    }

    //sub method
    //transfer serverString to Point2D[] arraylists
    public static ArrayList<Point2D[]> stringToKeyPoints(String serverStr) {
        //Log.d("STRING", serverStr);

        //TODO we have to sure that the data decided before we use it.
        float ratio_X = Constants.PREVIEW_WIDTH / Constants.CAMERA_WIDTH;
        float ratio_Y = Constants.PREVIEW_HEIGHT / Constants.CAMERA_HEIGHT;


        //TODO 이부분 수정하기

        ArrayList<Point2D[]> rtnArray = new ArrayList<>();
        int len = serverStr.length();
        String strNumOfDetectedPeople = serverStr.substring(0, 1);
        int numOfDetectedPeople = Integer.parseInt(strNumOfDetectedPeople);
        serverStr = serverStr.substring(3, len);

        String[] detected = serverStr.split("; ");
        for (int i = 0; i < numOfDetectedPeople; i++) {
            String[] tokens = detected[i].split(",");
            Point2D[] points = new Point2D[tokens.length / 2];
            for (int j = 0; j < Constants.KEYPOINT_NUM; j++) {
                points[j] = new Point2D(Double.parseDouble(tokens[j * 2]) * ratio_X, Double.parseDouble(tokens[j * 2 + 1]) * ratio_Y);
            }
            rtnArray.add(points);
        }

        return rtnArray;
    }

    //sub method
    //transfer Point2D[] to degree
    public static double getDegree(Point2D[] keyPoints) {
        double degree = 0;
        Point2D nose = keyPoints[Constants.NOSE];
        Point2D leftShoulder = keyPoints[Constants.L_SHOULDER];
        Point2D rightShoulder = keyPoints[Constants.R_SHOULDER];
        Point2D mid = new Point2D(Math.abs((rightShoulder.x + leftShoulder.x) / 2), Math.abs((rightShoulder.y + leftShoulder.y) / 2));
        double angle1 = Math.atan2(leftShoulder.y - leftShoulder.y, leftShoulder.x - rightShoulder.x); //프레임과 동일한 수직선
        double angle2 = Math.atan2(mid.y - nose.y, mid.x - nose.x);
        degree = (angle2 - angle1) * 180 / Math.PI;
        if (degree < 0) {
            degree += 360;
        }
        //각도 계산
        return degree;
    }

    //if two elbows points are above your body.

    public static boolean isRaisingHands(Point2D[] keyPoints) {
        double a, b;
        boolean boost;

        if (keyPoints[Constants.L_ELBOW].x * keyPoints[Constants.L_ELBOW].y == 0 ||
                keyPoints[Constants.R_ELBOW].x * keyPoints[Constants.R_ELBOW].y == 0)
            return false;

        if (keyPoints[Constants.L_ELBOW].x == keyPoints[Constants.R_ELBOW].x) {
            return true;
        } else {
            a = (keyPoints[Constants.L_ELBOW].y - keyPoints[Constants.R_ELBOW].y) / (keyPoints[Constants.L_ELBOW].x - keyPoints[Constants.R_ELBOW].x);
            b = keyPoints[Constants.L_ELBOW].y - a * keyPoints[Constants.L_ELBOW].x;
            if (keyPoints[Constants.NECK].y > a * keyPoints[Constants.NECK].x + b)
                return true;
            else
                return false;

        }
    }


    public static int getColor(int index) {
        switch (index) {
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
    public static String degreesToStr(double[] degrees) {
        String rtnStr = "";
        rtnStr += String.valueOf(degrees.length) + " ";
        for (int i = 0; i < degrees.length; i++)
            rtnStr += String.valueOf(String.format("%.2f", degrees[i]) + " ");

        return rtnStr;
    }

    //transfer boost[] to "2 1 0"
    public static String boostToStr(boolean[] boosts) {
        String rtnStr = "";
        rtnStr += String.valueOf(boosts.length) + " ";
        for (int i = 0; i < boosts.length; i++)
            rtnStr += (boosts[i]) ? "true " : "false ";

        return rtnStr;
    }

    public static double getFristAngle(String str) {
        StringTokenizer st = new StringTokenizer(str, " ");
        st.nextToken();
        return Double.parseDouble(st.nextToken());

    }

    public static void drawLine(Canvas c, Paint paint, Point2D p1, Point2D p2) {
        if (!(p1.x == 0 || p1.y == 0 || p2.x == 0 || p2.y == 0)) {
            c.drawLine((float) p1.x, (float) p1.y,
                    (float) p2.x, (float) p2.y, paint);
        }
    }

    public static double getDistance(Point2D p1, Point2D p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    public static ArrayList<KeyPoint> getPlainKeyPoint(ArrayList<UserInfo> users) {
        ArrayList<KeyPoint> userKeypoints = new ArrayList<>();

        for (int user = 0; user < users.size(); user++) {
            KeyPoint keyPoint = new KeyPoint();
            Point2D[] temp = new Point2D[Constants.KEYPOINT_NUM];
            for (int i = 0; i < Constants.KEYPOINT_NUM; i++) {
                if (i == Constants.NECK)
                    temp[i] = users.get(user).getKeyPoint().getSkeleton()[Constants.NECK];
                else
                    temp[i] = new Point2D();
            }
            keyPoint.setSkeleton(temp);

            userKeypoints.add(keyPoint);
        }

        return userKeypoints;
    }

    public static Bitmap getUserFace(Bitmap wholePicture, Point2D userNose) {
        //TODO fix
        Point2D pureXY = getPureCoordinates(wholePicture, userNose);
        Bitmap userFace = Bitmap.createBitmap(wholePicture,
                (int) pureXY.x - Constants.USER_FACE_CROP_DISTANCE,
                (int) pureXY.y - Constants.USER_FACE_CROP_DISTANCE,
                2 * Constants.USER_FACE_CROP_DISTANCE,
                2 * Constants.USER_FACE_CROP_DISTANCE);

        return userFace;
    }

    public static Bitmap getUserRectangle(Bitmap wholePicture, Point2D[] skeleton) {
        if (skeleton == null) return null;

        Point2D nose = getPureCoordinates(wholePicture, skeleton[Constants.NOSE]);
        Point2D neck = getPureCoordinates(wholePicture, skeleton[Constants.NECK]);
        Point2D leftShoulder = getPureCoordinates(wholePicture, skeleton[Constants.L_SHOULDER]);
        Point2D rightShoulder = getPureCoordinates(wholePicture, skeleton[Constants.R_SHOULDER]);

        int width = (int) (rightShoulder.x - leftShoulder.x) + Constants.USER_CROP_DISTANCE;
        int height = (int) (neck.y - nose.y) + Constants.USER_CROP_DISTANCE;
        Bitmap userRectangle = Bitmap.createBitmap(wholePicture,
                (int) leftShoulder.x - Constants.USER_CROP_DISTANCE,
                (int) nose.y - Constants.USER_FACE_CROP_DISTANCE,
                width,
                height);

        return userRectangle;
    }

    public static Point2D getPureCoordinates(Bitmap bitmap, Point2D ratioXY) {
        //the reason why x is minus is frame that is received from camera is reverse shot.
        double x = (Constants.PREVIEW_WIDTH - ratioXY.x) * bitmap.getWidth() / Constants.PREVIEW_WIDTH;

        //because x must be <bitmap.width()
        if (x == bitmap.getWidth())
            x -= 1;

        double y = ratioXY.y * bitmap.getHeight() / Constants.PREVIEW_HEIGHT;
        return new Point2D(x, y);
    }

    public static ArrayList<KeyPoint> getKeyPointsFromJsonData(String jsonData) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<KeyPoint>>() {
        }.getType();
        ArrayList<KeyPoint> keyPoints = gson.fromJson(jsonData, listType);
        for (int i = 0; i < keyPoints.size(); i++) {
            keyPoints.get(i).applyRatio();
        }
        return keyPoints;
    }

    public static int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public static String getEcryptedNumber(String phoneNumber) {
        if (phoneNumber.length() < 11)
            return phoneNumber;

        String encryptedNum = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
        return encryptedNum;

    }

    public static String getTime(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat minutes = new SimpleDateFormat("mm");
        int hourInt = Integer.parseInt(hour.format(date));
        int minuteInt = Integer.parseInt(minutes.format(date));

        boolean isPm = false;

        if (hourInt >= 12) isPm = true;
        if (hourInt > 12) hourInt -= 12;

        return String.format((isPm ? "오후 " : "오전 ") + "%02d:%02d", hourInt, minuteInt);
    }

    public static int calcPlayerRadius(ArrayList<UserInfo> userinfos)
    {
        double min_x, max_x, max_y;
        ArrayList<Double> xs = new ArrayList<>();
        for(UserInfo info : userinfos){
            Point2D[] userPoints = info.getKeyPoint().getSkeleton();
            Point2D neck = userPoints[Constants.NECK];
            xs.add(neck.x);
        }

        // Sort for figuring out maximum and minimum value
        Collections.sort(xs);

        min_x = xs.get(0);
        max_x = xs.get(xs.size()-1);
        int radius = (int)((max_x - min_x)/10);
        return radius;
    }
}