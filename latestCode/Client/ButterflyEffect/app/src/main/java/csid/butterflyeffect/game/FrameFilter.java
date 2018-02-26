package csid.butterflyeffect.game;

import android.util.Log;

import java.util.ArrayList;

import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

public class FrameFilter {
    private ArrayList<UserInfo> userInfos;
    private ArrayList<ArrayList<Point2D[]>> userInfoList;
    public FrameFilter(ArrayList<UserInfo> userInfos){
        this.userInfos = userInfos;
        userInfoList = new ArrayList<>();
    }

    public void update(){
        ArrayList<Point2D[]> recentInfo;
        Point2D[] temp = new Point2D[Constants.KEYPOINT_NUM];
        for(int i = 0; i < Constants.KEYPOINT_NUM; i++){
            temp[i] = new Point2D();
        }
        for(int user = 0; user < userInfos.size(); user++){
            recentInfo = userInfoList.get(userInfoList.size() -1);
            temp = recentInfo.get(user);
            userInfos.get(user).setNeck(temp[1]);
        }
    }

    public Point2D[] getRecentUserInfo(int user){
        ArrayList<Point2D[]> recentInfo = userInfoList.get(userInfoList.size());
        return recentInfo.get(user);
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

    public ArrayList<Point2D[]> filter(ArrayList<Point2D[]> peopleKeyPoints){
        int userSize = userInfos.size();
        ArrayList<Point2D[]> result = new ArrayList<>();

        for(int user = 0; user < userSize; user++){
            Point2D neck = userInfos.get(user).getNeck();
            int candidate = -1;
            int peopleSize = peopleKeyPoints.size();
            double min = 100000000;

            //When the OpenPose didn't detect key points or User died
            if(neck.x == 0 && neck.y==0){
                //If the user had died
                if(!userInfos.get(user).isPlaying()){
                    Point2D[] nominalKeyPoint =  new Point2D[Constants.KEYPOINT_NUM];
                    for(int i = 0; i < Constants.KEYPOINT_NUM; i++){
                        nominalKeyPoint[i] = new Point2D();
                    }
                    result.add(nominalKeyPoint);
                }
                else {
                    result.add(getRecentUserInfo(user));
                }
                continue;
            }

            //Check all key points in frame to detect user
            for(int people = 0; people < peopleSize; people++){
                Point2D[] keyPoints = peopleKeyPoints.get(people);
                //Caculate the distance between user neck and person's neck in frame
                double distance = Utils.getDistance(neck, keyPoints[1]);
                //Select the nearest distance
                if(distance <= Constants.PLAYER_RADIUS && distance < min){
                    min = distance;
                    candidate = people;
                }
            }
            //When the filter didn't find the targeted user.
            if(candidate == -1) {
                result.add(getRecentUserInfo(user));
            }
            //When the filter find the targeted user
            else
                result.add(peopleKeyPoints.get(candidate));
        }
        //Save the best three people in frame
        insert(result);

        //Update the old key points to new key points to draw correct skeletons.
        update();

        //Return the result, result has key points of user1, user2, user3
        return result;
    }
}
