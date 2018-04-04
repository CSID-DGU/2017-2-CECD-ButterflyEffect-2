package csid.butterflyeffect.game.filter;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

import csid.butterflyeffect.PreviewSurface;
import csid.butterflyeffect.game.BattleWorms;
import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.model.KeyPoint;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

public class PlayFilter {
    private ArrayList<UserInfo> userInfos;
    private ArrayList<ArrayList<KeyPoint>> userInfoList;
    private BattleWorms battleWorms;

    public PlayFilter(ArrayList<UserInfo> userInfos,BattleWorms battleWorms) {
        this.battleWorms = battleWorms;
        this.userInfos = userInfos;
        userInfoList = new ArrayList<>();

    }

    //add init value of useres
    public void saveFirstUserInfo(){
        userInfoList.add(Utils.getPlainKeyPoint(userInfos));

        ArrayList<KeyPoint> userKeyPointsInfo = new ArrayList<>();
        for(int i=0;i<userInfos.size();i++)
            userKeyPointsInfo.add(userInfos.get(i).getKeyPoint());

        ArrayList<int[]> colors = getRGBFromPixels(userKeyPointsInfo);

        //save user's color information for each users.
        for(int i=0;i<userInfos.size();i++){
            int[] userColors = colors.get(i);
            for(int j=0;j<Constants.USER_COLOR_LISTS_NUM;j++){
                userInfos.get(i).addColor(userColors[j]);
            }
        }

    }

    //
    public void update() {
        ArrayList<KeyPoint> recentInfo;
        Point2D[] temp = new Point2D[Constants.KEYPOINT_NUM];
        for (int i = 0; i < Constants.KEYPOINT_NUM; i++) {
            temp[i] = new Point2D();
        }

        for (int user = 0; user < userInfos.size(); user++) {
            recentInfo = userInfoList.get(userInfoList.size() - 1);
            temp = recentInfo.get(userInfos.get(user).getUserNumber()).getSkeleton();
            userInfos.get(user).getKeyPoint().setSkeleton(temp);
            //userInfos.get(user).setNeck(temp[Constants.NECK]);
            //userInfos.get(user).setNose(temp[Constants.NOSE]);
        }
    }

    public Point2D[] getRecentUserInfo(int userId) {
        ArrayList<KeyPoint> recentInfo = userInfoList.get(userInfoList.size() - 1);
        return recentInfo.get(userId).getSkeleton();
    }

    public void insert(ArrayList<KeyPoint> userInfo) {
        //if user list is less than the size
        if (userInfoList.size() < Constants.USER_LIST_SIZE) {
            userInfoList.add(userInfo);
        } else {
            userInfoList.remove(0);
            userInfoList.add(userInfo);
        }
    }

    public Point2D[] getNominalKeyPoint() {
        Point2D[] nominalKeyPoint = new Point2D[Constants.KEYPOINT_NUM];
        for (int i = 0; i < Constants.KEYPOINT_NUM; i++) {
            if (i == Constants.NECK) {
                //for preventing zero degree
                nominalKeyPoint[i] = new Point2D(0, 1);
            } else {
                nominalKeyPoint[i] = new Point2D();
            }
        }
        return nominalKeyPoint;
    }

    public Point2D[] getDiedKeyPoint() {
        Point2D[] diedKeyPoint = new Point2D[Constants.KEYPOINT_NUM];
        for (int i = 0; i < Constants.KEYPOINT_NUM; i++) {
            diedKeyPoint[i] = new Point2D();
        }
        return diedKeyPoint;
    }


    public ArrayList<KeyPoint> filter(ArrayList<KeyPoint> peopleKeyPoints) {
        int userSize = userInfos.size();
        Point2D[][] result = new Point2D[userInfos.size()][Constants.KEYPOINT_NUM];
        for (int user = 0; user < userSize; user++) {
            int userNumber = userInfos.get(user).getUserNumber();
            if (!userInfos.get(user).isPlaying()) {
                result[userNumber] = getNominalKeyPoint();
            }
            else{

                Point2D neck = userInfos.get(user).getKeyPoint().getSkeleton()[Constants.NECK];
                ArrayList<Point2D[]> candidatesKeyPoints = new ArrayList<>();
                //int candidate = -1;
                int peopleSize = peopleKeyPoints.size();

                double minNose = Integer.MAX_VALUE;
                double minNeck = Integer.MAX_VALUE;

                //TODO if that player is died player then add diedPoint.
                //If the OpenPose didn't detect correctly key points or User died
                if (neck.x == 0 && neck.y == 0) {
                    //If the user had died
                    result[userNumber] = getRecentUserInfo(userNumber);
                }
                //If the OpenPose correctly detected the key points
                else {
                    //Check all key points in frame to detect user
                    for (int people = 0; people < peopleSize; people++) {
                        Point2D[] keyPoints = peopleKeyPoints.get(people).getSkeleton();
                        //Calculate the distance between user neck and person's neck in frame
                        double distanceNeck = Utils.getDistance(neck, keyPoints[Constants.NECK]);
                        //double distanceNose = Utils.getDistance(nose, keyPoints[Constants.NOSE]);
                        //Select the nearest distance
                        //if(distanceNeck<Constants.PLAYER_RADIUS){
                        if (distanceNeck < Constants.PLAYER_RADIUS && distanceNeck < minNeck) {
                            minNeck = distanceNeck;
                            candidatesKeyPoints.add(keyPoints);
                        }
                    }
                    Log.d("#####","candidates:"+candidatesKeyPoints.size());
                    //If the filter didn't find the targeted user as the user was died or previous distance was too small.
                    if (candidatesKeyPoints.size() == 0) {
                        //If the user had died
                        result[userNumber] = getRecentUserInfo(userNumber);
                    }
                    else if(candidatesKeyPoints.size() == 1){
                        //TODO 색 정보 갱신하는 것도 생각해보기
                        //If the filter find the targeted user
                        result[userNumber] = candidatesKeyPoints.get(0);
                        deleteFromKeyPoints(peopleKeyPoints,result[userNumber][Constants.NECK]);

                    }
                    else {//multiple candidate, so color filter applied
                        result[userNumber] = filter(userInfos.get(user).getColors(), candidatesKeyPoints);
                        deleteFromKeyPoints(peopleKeyPoints,result[userNumber][Constants.NECK]);
                    }
                }
            }
        }



        //change Point2D[][] to arraylist<Point2D[]>
        ArrayList<KeyPoint> rtnPoints = new ArrayList<>();
        for (int i = 0; i < userInfos.size(); i++) {
            rtnPoints.add(new KeyPoint(result[i]));
        }

        //Save the best three people in frame
        insert(rtnPoints);

        //Update the old key points to new key points to draw correct skeletons.
        update();

        //Return the result, result has key points of user1, user2, user3
        return rtnPoints;
    }

    public void deleteFromKeyPoints(ArrayList<KeyPoint> peopleKeyPoints,Point2D targetUserNeck){
        int index = -1;
        for(int i=0;i<peopleKeyPoints.size();i++){
            Point2D neck = peopleKeyPoints.get(i).getSkeleton()[Constants.NECK];
            if(neck.x ==targetUserNeck.x && neck.y == targetUserNeck.y){
                index = i;
                break;
            }
        }
        peopleKeyPoints.remove(index);
    }

    //filter Overloading
    public Point2D[] filter(ArrayList<Integer> userColors, ArrayList<Point2D[]> candidatesKeyPoints) {
        int userColorsSize = userColors.size();
        int result = -1;
        int[] colorDiff = new int[candidatesKeyPoints.size()];
        //Calculate color difference
         for(int i = 0; i < candidatesKeyPoints.size(); i++) {
            colorDiff[i] = 0;
            for(int j = 0; j < 1; j++) {
                int candidateColor = getRGBFromPixel(candidatesKeyPoints.get(i)[Constants.COLOR_LISTS_NAME[j]]);
                Log.d("#####","user "+(i+1)+"("+j+") color: "+String.valueOf(candidateColor));
                colorDiff[i] += Math.abs(candidateColor - userColors.get(j));
            }
        }

        //Pick minimum diff
        int minDiff = Integer.MAX_VALUE;
        for(int i = 0; i < candidatesKeyPoints.size(); i++){
            if(colorDiff[i] < minDiff){
                minDiff = colorDiff[i];
                result = i;
            }
        }
        return candidatesKeyPoints.get(result);
    }


    public boolean isInWidth(int x, int width){
        if(0 <= x && x < width)
            return true;
        else
            return false;
    }
    public boolean isInHeight(int y, int height){
        if(0 <= y && y < height)
            return true;
        else
            return false;
    }

    public int getRGBFromPixel(Point2D target){
        Bitmap bitmap;
        int colorsSum = 0;
        int cnt = 0;
        do {
            bitmap = PreviewSurface.curFrameImage();
        }while(bitmap==null);
        Point2D pureXY = Utils.getPureCoordinates(bitmap,target);
        int startX = Integer.MAX_VALUE, endX = -1, startY = Integer.MAX_VALUE, endY = -1;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        boolean stop = false;
        for(int i = Constants.OFFSET; i > 0; i--) {
            stop = true;
            if(startX == Integer.MAX_VALUE && isInWidth((int)(pureXY.x-i), bitmapWidth)) {
                startX = ((int)pureXY.x - i);
                stop = false;
            }
            if(endX == -1 && isInWidth(((int)pureXY.x+i-1), bitmapWidth)){
                endX = ((int)pureXY.x + i);
                stop = false;
            }
            if(startY == Integer.MAX_VALUE && isInHeight(((int)pureXY.y-i), bitmapHeight)) {
                startY = ((int)pureXY.y - i);
                stop = false;
            }
            if(endY == -1 && isInHeight(((int)pureXY.y+i-1), bitmapHeight)){
                endY = ((int)pureXY.y + i);
                stop = false;
            }
            if(stop == true)
                break;
        }
        for(int i = startX; i < endX; i++){
            for(int j = startY; j < endY; j++){
                colorsSum += bitmap.getPixel(i, j);
                cnt++;
            }
        }
        //it is because we will get pixel from raw frame of camera.
        if(cnt != 0){
            return colorsSum/cnt;
        }
        else {
            return 0;
        }
    }

    public ArrayList<int[]> getRGBFromPixels(ArrayList<KeyPoint> userKeyPointsInfo) {
        ArrayList<int[]> colors = new ArrayList<>();

       // int[] colors = new int[positions.size()];
        Bitmap bitmap = PreviewSurface.curFrameImage();

        for(int i=0;i<userKeyPointsInfo.size();i++){
            Point2D[] targets = new Point2D[Constants.USER_COLOR_LISTS_NUM];
            for(int j=0;j<Constants.USER_COLOR_LISTS_NUM;j++)
                targets[j] = Utils.getPureCoordinates(bitmap,userKeyPointsInfo.get(i).getSkeleton()[Constants.COLOR_LISTS_NAME[j]]);

            //it is because we will get pixel from raw frame of camera.
            int[] userColors = new int[Constants.USER_COLOR_LISTS_NUM];
            for(int j=0;j<Constants.USER_COLOR_LISTS_NUM;j++){
                //TODO 이곳에서 x 가 음수면 프로그램이 죽음!
                userColors[j] = bitmap.getPixel((int)targets[j].x, (int)targets[j].y);
            }
            colors.add(userColors);
        }

        return colors;
    }
}
