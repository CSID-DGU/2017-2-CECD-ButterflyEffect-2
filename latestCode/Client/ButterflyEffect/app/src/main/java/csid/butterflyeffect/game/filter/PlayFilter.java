package csid.butterflyeffect.game.filter;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

import csid.butterflyeffect.PreviewSurface;
import csid.butterflyeffect.game.BattleWorms;
import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

public class PlayFilter {
    private ArrayList<UserInfo> userInfos;
    private ArrayList<ArrayList<Point2D[]>> userInfoList;
    private BattleWorms battleWorms;

    public PlayFilter(ArrayList<UserInfo> userInfos,BattleWorms battleWorms) {
        this.battleWorms = battleWorms;
        this.userInfos = userInfos;
        userInfoList = new ArrayList<>();

    }

    //add init value of useres
    public void saveFirstUserInfo(){
        userInfoList.add(Utils.getPlainKeyPoint(userInfos));

        ArrayList<Point2D> userNeckInfo = new ArrayList<>();
        for(int i=0;i<userInfos.size();i++)
            userNeckInfo.add(userInfos.get(i).getNeck());

        ArrayList<int[]> colors = getRGBFromPixels(userNeckInfo);

        //repeat to save user's neck's color 5 times for each users.
        for(int i=0;i<userInfos.size();i++){
            int[] userColors = colors.get(i);
            for(int j=0;j<Constants.USER_COLOR_LISTS_NUM;j++){
                userInfos.get(i).addColor(userColors[j]);
            }
        }

    }

    //
    public void update() {
        ArrayList<Point2D[]> recentInfo;
        Point2D[] temp = new Point2D[Constants.KEYPOINT_NUM];
        for (int i = 0; i < Constants.KEYPOINT_NUM; i++) {
            temp[i] = new Point2D();
        }

        for (int user = 0; user < userInfos.size(); user++) {
            recentInfo = userInfoList.get(userInfoList.size() - 1);
            temp = recentInfo.get(userInfos.get(user).getUserNumber());
            userInfos.get(user).setNeck(temp[Constants.NECK]);
            userInfos.get(user).setNose(temp[Constants.NOSE]);
        }
    }

    public Point2D[] getRecentUserInfo(int userId) {
        ArrayList<Point2D[]> recentInfo = userInfoList.get(userInfoList.size() - 1);
        return recentInfo.get(userId);
    }

    public void insert(ArrayList<Point2D[]> userInfo) {
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


    public ArrayList<Point2D[]> filter(ArrayList<Point2D[]> peopleKeyPoints) {
        int userSize = userInfos.size();
        Point2D[][] result = new Point2D[userInfos.size()][Constants.KEYPOINT_NUM];
        for (int user = 0; user < userSize; user++) {
            int userNumber = userInfos.get(user).getUserNumber();
            if (!userInfos.get(user).isPlaying()) {
                result[userNumber] = getNominalKeyPoint();
            }
            else{
                Point2D neck = userInfos.get(user).getNeck();
                Point2D nose = userInfos.get(user).getNose();
                ArrayList<Integer> candidates = new ArrayList<>();
                ArrayList<Point2D> neckPositions = new ArrayList<>();
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
                        Point2D[] keyPoints = peopleKeyPoints.get(people);
                        //Calculate the distance between user neck and person's neck in frame
                        double distanceNeck = Utils.getDistance(neck, keyPoints[Constants.NECK]);
                        //double distanceNose = Utils.getDistance(nose, keyPoints[Constants.NOSE]);
                        //Select the nearest distance
                        if (distanceNeck < Constants.PLAYER_RADIUS && distanceNeck < minNeck) {
                            minNeck = distanceNeck;
                            candidates.add(people);
                            neckPositions.add(keyPoints[Constants.NECK]);
                        }
                    }
                    //If the filter didn't find the targeted user as the user was died or previous distance was too small.
                    if (candidates.size() == 0) {
                        //If the user had died
                        result[userNumber] = getRecentUserInfo(userNumber);
                    }
                    else if(candidates.size() == 1){
                        //TODO keyPoints 에서 해당 유저 지우는거 생각해보기
                        //TODO 색 정보 갱신하는 것도 생각해보기
                        //If the filter find the targeted user
                        result[userNumber] = peopleKeyPoints.get(candidates.get(0));
                    }
                    else {//multiple candidate, so color filter applied
                        result[userNumber] = peopleKeyPoints.get(filter(userInfos.get(user).getColors(), candidates, neckPositions));
                    }
                }
            }
        }

        //change Point2D[][] to arraylist<Point2D[]>
        ArrayList<Point2D[]> rtnPoints = new ArrayList<>();
        for (int i = 0; i < userInfos.size(); i++) {
            rtnPoints.add(result[i]);
        }

        //Save the best three people in frame
        insert(rtnPoints);

        //Update the old key points to new key points to draw correct skeletons.
        update();

        //Return the result, result has key points of user1, user2, user3
        return rtnPoints;
    }


    //filter Overloading
    public int filter(ArrayList<Integer> userColors, ArrayList<Integer> candidates, ArrayList<Point2D> neckPositions) {
        int candidatesSize = candidates.size();
        int userColorsSize = userColors.size();
        int result = -1;
        int[] colorDiff = new int[candidatesSize];
        //Calculate color difference
        for(int i = 0; i < candidatesSize; i++) {
            int candidateColor = getRGBFromPixel(neckPositions.get(i));
            colorDiff[i] = 0;
            for(int j = 0; j < userColorsSize; j++) {
                colorDiff[i] += Math.abs(candidateColor - userColors.get(j));
            }
        }

        //Pick minimum diff
        int minDiff = Integer.MIN_VALUE;
        for(int i = 0; i < candidatesSize; i++){
            if(colorDiff[i] < minDiff){
                minDiff = colorDiff[i];
                result = i;
            }
        }
        return result;
    }

    public int getRGBFromPixel(Point2D target){
        Bitmap bitmap = PreviewSurface.curFrameImage();
        Point2D pureXY = Utils.getPureCoordinates(bitmap,target);
        //it is because we will get pixel from raw frame of camera.

        return bitmap.getPixel((int)pureXY.x, (int)pureXY.y);
    }

    public ArrayList<int[]> getRGBFromPixels(ArrayList<Point2D> positions) {
        ArrayList<int[]> colors = new ArrayList<>();

       // int[] colors = new int[positions.size()];
        Bitmap bitmap = PreviewSurface.curFrameImage();

        for(int i=0;i<positions.size();i++){
            Point2D pureXY = Utils.getPureCoordinates(bitmap,positions.get(i));
            //it is because we will get pixel from raw frame of camera.
            int[] userColors = new int[Constants.USER_COLOR_LISTS_NUM];
            for(int j=0;j<Constants.USER_COLOR_LISTS_NUM;j++){
                userColors[j] = bitmap.getPixel((int)pureXY.x, (int)pureXY.y);
            }
            colors.add(userColors);
        }

        return colors;
    }
}
