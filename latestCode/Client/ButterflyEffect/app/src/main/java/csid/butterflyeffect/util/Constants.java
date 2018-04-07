package csid.butterflyeffect.util;

import csid.butterflyeffect.R;

/**
 * Created by hanseungbeom on 2018. 1. 16..
 */

public class Constants {

    //about battleWorms
    public static int PLAYER_NUMBER = 1;
    public static final int STATE_WAIT = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_START = 2;
    public static final int WAITING_TIME = 3;


    //about CONNECTION
    public static int PORT_NUM = 9000;
    public static int TIME_OUT_FOR_TCP_CONNECTION = 3000;//3 sec
    public static String ADDR = "13.125.195.240";

    //about CAMERA
    public static final int FRAME_RATE = 10000; //10000 = 10 fps
    public static final int COMPRESS_QUAILITY = 60; //100 -> same quaility

    //about skeleton
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

    public static final int BIG_CIRCLE_RADIUS = 13;
    public static final int CIRCLE_RADIUS = 10;

    public static final int READY_CIRCLE_RADIUS = 5;
    public static final int PLAYER_TEXT_SIZE = 25;
    public static final float LINE_WIDTH = 5;
    public static final float SPECIAL_LINE_WIDTH = 15;
    public static final int USER_FACE_CROP_DISTANCE = 40;


    //about user device GLOBAL VARIABLE
    public static float CAMERA_WIDTH = -1;
    public static float CAMERA_HEIGHT = -1;
    public static float PREVIEW_WIDTH = -1;
    public static float PREVIEW_HEIGHT = -1;

    //about filter
    public static final int LIST_SIZE = 30;
    public static final int QUEUE_SIZE = 20;
    public static final int USER_LIST_SIZE  = 20;
    public static final int PLAYER_RADIUS = 100;
    public static final int RAISING_HAND_C = 50;
    public static final int KEYPOINT_NUM = 18;
    public static final int OFFSET = 5;
    public static final int[] COLOR_LISTS_NAME={
            Constants.NECK
    };

    //adapter
    public static final int USER_COLOR_LISTS_NUM = 1;
    public static final int[] COLOR_LISTS={
            R.id.view_c1
    };


}
