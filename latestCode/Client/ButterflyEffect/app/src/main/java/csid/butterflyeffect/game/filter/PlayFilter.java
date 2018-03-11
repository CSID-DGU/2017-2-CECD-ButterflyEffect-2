package csid.butterflyeffect.game.filter;

import android.util.Log;

import java.util.ArrayList;

import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

public class PlayFilter {
    private ArrayList<UserInfo> userInfos;
    private ArrayList<ArrayList<Point2D[]>> userInfoList;
    public PlayFilter(ArrayList<UserInfo> userInfos){
        this.userInfos = userInfos;
        userInfoList = new ArrayList<>();

    }

    //add init value of useres
    public void settingFirstUserInfoToQueue(){
        userInfoList.add(Utils.getPlainKeyPoint(userInfos));
    }

    //
    public void update(){
        ArrayList<Point2D[]> recentInfo;
        Point2D[] temp = new Point2D[Constants.KEYPOINT_NUM];
        for(int i = 0; i < Constants.KEYPOINT_NUM; i++){
            temp[i] = new Point2D();
        }

        for(int user = 0; user < userInfos.size(); user++){
            recentInfo = userInfoList.get(userInfoList.size() -1);
            temp = recentInfo.get(userInfos.get(user).getUserNumber());
            userInfos.get(user).setNeck(temp[Constants.NECK]);
            userInfos.get(user).setNose(temp[Constants.NOSE]);
        }
    }

    public Point2D[] getRecentUserInfo(int userId){
        ArrayList<Point2D[]> recentInfo = userInfoList.get(userInfoList.size()-1);
        return recentInfo.get(userId);
    }

    public void insert(ArrayList<Point2D[]> userInfo){
        //if user list is less than the size
        if(userInfoList.size() < Constants.USER_LIST_SIZE){
            userInfoList.add(userInfo);
        }
        else{
            userInfoList.remove(0);
            userInfoList.add(userInfo);
        }
    }
    public Point2D[] getNominalKeyPoint() {
        Point2D[] nominalKeyPoint =  new Point2D[Constants.KEYPOINT_NUM];
        for(int i = 0; i < Constants.KEYPOINT_NUM; i++){
            if(i == Constants.NECK) {
                //for preventing zero degree
                nominalKeyPoint[i] = new Point2D(0, 1);
            }
            else {
                nominalKeyPoint[i] = new Point2D();
            }
        }
        return nominalKeyPoint;
    }
    public Point2D[] getDiedKeyPoint() {
        Point2D[] diedKeyPoint =  new Point2D[Constants.KEYPOINT_NUM];
        for(int i = 0; i < Constants.KEYPOINT_NUM; i++){
            diedKeyPoint[i] = new Point2D();
        }
        return diedKeyPoint;
    }


    public ArrayList<Point2D[]> filter(ArrayList<Point2D[]> peopleKeyPoints){
        int userSize = userInfos.size();
        Point2D[][] result = new Point2D[userInfos.size()][Constants.KEYPOINT_NUM];

        for(int user = 0; user < userSize; user++){
            Point2D neck = userInfos.get(user).getNeck();
            Point2D nose = userInfos.get(user).getNose();
            int candidate = -1;
            int peopleSize = peopleKeyPoints.size();
            int userNumber = userInfos.get(user).getUserNumber();
            double minNose = Integer.MAX_VALUE;
            double minNeck = Integer.MAX_VALUE;

            //TODO if that player is died player then add diedPoint.
            //If the OpenPose didn't detect correctly key points or User died
            if(neck.x == 0 && neck.y==0){
                //If the user had died
                if(!userInfos.get(user).isPlaying()){
                    result[userNumber]=getNominalKeyPoint();
                }
                else {
                    result[userNumber]=getRecentUserInfo(userNumber);
                }
                continue;
            }
            //If the OpenPose correctly detected the key points
            else{
                //Check all key points in frame to detect user
                for (int people = 0; people < peopleSize; people++) {
                    Point2D[] keyPoints = peopleKeyPoints.get(people);
                    //Calculate the distance between user neck and person's neck in frame
                    double distanceNeck = Utils.getDistance(neck, keyPoints[Constants.NECK]);
                    //double distanceNose = Utils.getDistance(nose, keyPoints[Constants.NOSE]);
                    //Select the nearest distance
                    if (distanceNeck < minNeck) {
                        minNeck = distanceNeck;
                        candidate = people;
                    }
                }
                //If the filter didn't find the targeted user as the user was died or previous distance was too small.
                if(candidate == -1) {
                    //If the user had died
                    if(!userInfos.get(user).isPlaying()){
                        result[userNumber] = getNominalKeyPoint();
                    }
                    else {
                        result[userNumber] = getRecentUserInfo(userNumber);
                    }
                }
                //If the filter find the targeted user
                else {
                    //result.add(peopleKeyPoints.get(candidate));
                    result[userNumber] = peopleKeyPoints.get(candidate);
                }
            }
        }

        //change Point2D[][] to arraylist<Point2D[]>
        ArrayList<Point2D[]> rtnPoints = new ArrayList<>();
        for(int i=0;i<userInfos.size();i++){
            rtnPoints.add(result[i]);
        }

        //Save the best three people in frame
        insert(rtnPoints);

        //Update the old key points to new key points to draw correct skeletons.
        update();

        //Return the result, result has key points of user1, user2, user3
        return rtnPoints;
    }
}
