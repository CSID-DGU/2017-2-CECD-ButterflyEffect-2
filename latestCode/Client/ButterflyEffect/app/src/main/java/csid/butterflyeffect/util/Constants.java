package csid.butterflyeffect.util;

/**
 * Created by hanseungbeom on 2018. 1. 16..
 */

public class Constants {
     //about CONNECTION
    public static int PORT_NUM = 9000;
    public static int TIME_OUT_FOR_TCP_CONNECTION = 3000;//3 sec
    public static String ADDR = "52.79.126.145";

    //about CAMERA
    public static final int FRAME_RATE = 10000; //10000 = 10 fps

    //about skeleteon
    public static final int NOSE = 0;
    public static final int NECK = 1;
    public static final int R_SHOULDER = 2;
    public static final int R_ELBOW = 3;
    public static final int R_WRIST = 4;
    public static final int L_SHOULDER = 5;
    public static final int L_ELBOW = 6;
    public static final int L_WRIST = 7;
    public static final int R_HIP = 8;
    public static final int R_KNEE = 9;
    public static final int R_ANKLE = 10;
    public static final int L_HIP = 11;
    public static final int L_KNEE = 12;
    public static final int L_ANKLE= 13;
    public static final int R_EYE = 14;
    public static final int L_EYE = 15;
    public static final int R_EAR = 16;
    public static final int L_EAR = 17;
    public static final int BACKGROUND = 18;

    public static final int CIRCLE_RADIUS = 10;


    public static float CAMERA_WIDTH = -1;
    public static float CAMERA_HEIGHT = -1;
    public static float PREVIEW_WIDTH = -1;
    public static float PREVIEW_HEIGHT = -1;
}
