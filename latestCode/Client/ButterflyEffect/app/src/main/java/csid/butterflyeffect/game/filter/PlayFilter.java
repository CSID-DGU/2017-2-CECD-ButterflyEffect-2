package csid.butterflyeffect.game.filter;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

import csid.butterflyeffect.PreviewSurface;
import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

public class PlayFilter {
    private ArrayList<UserInfo> userInfos;
    private ArrayList<ArrayList<Point2D[]>> userInfoList;

    public PlayFilter(){

    }
    public PlayFilter(ArrayList<UserInfo> userInfos) {
        this.userInfos = userInfos;
        userInfoList = new ArrayList<>();
    }

    //add init value of useres
    public void saveFirstUserInfo(ArrayList<Point2D[]> filteredData) {
        userInfoList.add(Utils.getPlainKeyPoint(userInfos));
        int filteredDataSize = filteredData.size();
        for(int i = 0; i < filteredDataSize; i++){
            ArrayList<Point2D> userNeckInfo = new ArrayList<>();
            Point2D[] keyPoints = filteredData.get(i);
            userNeckInfo.add(keyPoints[Constants.NECK]);
            //save 5 color information
            for(int j = 0; j < 5; j++) {
                //Variable colors  have user N color information
                int[] colors = getRGBFromPixels(userNeckInfo);
                for(int k = 0; k < colors.length; k++) {
                    userInfos.get(k).addColor(colors[k]);
                }
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
                    else if(candidates.size() == 1){//If the filter find the targeted user
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
            int candidateColor = getRGBFromPixel(neckPositions.get(i).x,  neckPositions.get(i).y);
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

    public int getRGBFromPixel(double x, double y){
        Bitmap bitmap = PreviewSurface.curFrameImage();
        return bitmap.getPixel((int)x, (int)y);
    }

    public int[] getRGBFromPixels(ArrayList<Point2D> positions) {
        Bitmap bitmap = PreviewSurface.curFrameImage();

        int length = positions.size();
        int[] colors = new int[length];
        for(int i = 0; i < length; i++) {
            colors[i] = bitmap.getPixel((int)positions.get(i).x, (int)positions.get(i).y);
        }

        return colors;
    }
}
