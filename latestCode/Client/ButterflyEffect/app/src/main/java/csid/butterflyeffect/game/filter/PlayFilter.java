package csid.butterflyeffect.game.filter;

import android.graphics.Color;
import android.util.Log;

import java.security.Key;
import java.util.ArrayList;

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

    public PlayFilter(ArrayList<UserInfo> userInfos, BattleWorms battleWorms) {
        this.battleWorms = battleWorms;
        this.userInfos = userInfos;
        userInfoList = new ArrayList<>();
    }

    //add init value of useres
    public void saveFirstUserInfo() {
        userInfoList.add(Utils.getPlainKeyPoint(userInfos));
        ArrayList<KeyPoint> userKeyPointsInfo = new ArrayList<>();
        for (int i = 0; i < userInfos.size(); i++)
            userKeyPointsInfo.add(userInfos.get(i).getKeyPoint());
        ArrayList<int[]> colors = getRGBfromPlayerData(userKeyPointsInfo);
        //save user's color information for each users.
        for (int i = 0; i < userInfos.size(); i++) {
            int[] userColors = colors.get(i);
            for (int j = 0; j < Constants.USER_COLOR_LISTS_NUM; j++) {
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

            Point2D neck = userInfos.get(user).getKeyPoint().getSkeleton()[Constants.NECK];
            //ArrayList<Point2D[]> candidatesKeyPoints = new ArrayList<>();
            ArrayList<KeyPoint> candidatesKeyPoints = new ArrayList<>();
            //int candidate = -1;
            int peopleSize = peopleKeyPoints.size();

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
                    //Select the nearest distance
                    //if(distanceNeck<Constants.PLAYER_RADIUS){
                    if (distanceNeck < Constants.PLAYER_RADIUS) {
                        candidatesKeyPoints.add(new KeyPoint(keyPoints));
                    }
                }
                Log.d("#####", "candidates:" + candidatesKeyPoints.size());
                //If the filter didn't find the targeted user as the user was died or previous distance was too small.
                if (candidatesKeyPoints.size() == 0) {
                    //If the user had died
                    result[userNumber] = getRecentUserInfo(userNumber);
                } else if (candidatesKeyPoints.size() == 1) {
                    //TODO 색 정보 갱신하는 것도 생각해보기
                    //If the filter find the targeted user
                    result[userNumber] = candidatesKeyPoints.get(0).getSkeleton();
                    deleteFromKeyPoints(peopleKeyPoints, result[userNumber][Constants.NECK]);
                } else {//multiple candidate, so color filter applied
                    Log.d("DEBUG", "Color filter");
                    result[userNumber] = colorFilter(userInfos.get(user).getColors(), candidatesKeyPoints);
                    deleteFromKeyPoints(peopleKeyPoints, result[userNumber][Constants.NECK]);
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

    public void deleteFromKeyPoints(ArrayList<KeyPoint> peopleKeyPoints, Point2D targetUserNeck) {
        int index = -1;
        for (int i = 0; i < peopleKeyPoints.size(); i++) {
            Point2D neck = peopleKeyPoints.get(i).getSkeleton()[Constants.NECK];
            if (neck.x == targetUserNeck.x && neck.y == targetUserNeck.y) {
                index = i;
                break;
            }
        }
        peopleKeyPoints.remove(index);
    }

    //filter Overloading
    public Point2D[] colorFilter(ArrayList<Integer> userColors, ArrayList<KeyPoint> candidatesKeyPoints) {
        int userColorsSize = userColors.size();
        int result = -1;
        double[] colorDistance = new double[candidatesKeyPoints.size()];
        int red, green, blue;
        //Calculate color difference
        ArrayList<int[]> colors = getRGBfromPlayerData(candidatesKeyPoints);

        for (int user = 0; user < candidatesKeyPoints.size(); user++) {
            int[] color = colors.get(user);
            red = 0;
            green = 0;
            blue = 0;
            for (int bodyPart = 0; bodyPart < Constants.COLOR_LISTS_NAME.length; bodyPart++) {
                red += Math.abs(Color.red(color[bodyPart]) - Color.red(userColors.get(bodyPart)));
                green += Math.abs(Color.green(color[bodyPart]) - Color.green(userColors.get(bodyPart)));
                blue += Math.abs(Color.blue(color[bodyPart]) - Color.blue(userColors.get(bodyPart)));
            }
            colorDistance[user] = red + green + blue;
        }

        //Pick minimum diff
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < candidatesKeyPoints.size(); i++) {
            if (colorDistance[i] < minDistance) {
                minDistance = colorDistance[i];
                result = i;
            }
        }
        Log.d("DEBUG", "" + minDistance);
        return candidatesKeyPoints.get(result).getSkeleton();
    }

    public ArrayList<int[]> getRGBfromPlayerData(ArrayList<KeyPoint> userKeyPointsInfo) {
        ArrayList<int[]> colors = new ArrayList<>();

        for (int i = 0; i < userKeyPointsInfo.size(); i++) {
            Point2D[] targets = userKeyPointsInfo.get(i).getSkeleton();
            //it is because we will get pixel from raw frame of camera.
            int[] userColors = new int[Constants.COLOR_LISTS_NAME.length];
            for (int j = 0; j < Constants.COLOR_LISTS_NAME.length; j++) {
                Point2D targetArea = targets[Constants.COLOR_LISTS_NAME[j]];
                userColors[j] = Utils.getIntFromColor(targetArea.r, targetArea.g, targetArea.b);
            }
            colors.add(userColors);
        }
        return colors;
    }
}