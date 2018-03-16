package csid.butterflyeffect.game.filter;

import android.util.Log;

import java.util.ArrayList;

import csid.butterflyeffect.game.BattleWorms;
import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.UnityConnector;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

/**
 * Created by hanseungbeom on 2018. 2. 23..
 */

public class ReadyFilter {
    private ArrayList<UserInfo> userInfos;
    private ArrayList<ArrayList<Point2D[]>> list;
    private ArrayList<Point2D[]> userKeypoints;
    private BattleWorms battleWorms;

    public ReadyFilter(ArrayList<UserInfo> userInfos,BattleWorms battleWorms){
        this.userInfos = userInfos;
        this.battleWorms = battleWorms;
        list = new ArrayList<>();
        userKeypoints = new ArrayList<>();
        for(int i=0;i<Constants.PLAYER_NUMBER;i++){
            Point2D[] keypoint = new Point2D[Constants.KEYPOINT_NUM];
            for(int j=0;j<Constants.KEYPOINT_NUM;j++){
                keypoint[j] = new Point2D();
            }
            userKeypoints.add(keypoint);
        }
    }

    public ArrayList<Point2D[]> filter(ArrayList<Point2D[]> data){


        if(list.size() == Constants.LIST_SIZE) {
            //remove to keep the queue.size constant
            list.remove(list.get(0));

            //if player exists
            //update userinfos position with the latest points
            if(userInfos.size()!=0){
                //search the closest point for each player and update their points.
                for (int i = 0; i < userInfos.size(); i++) {
                    UserInfo player = userInfos.get(i);
                    Point2D playerBody = player.getKeyPoints()[Constants.NECK];
                    int closestIndex = -1;
                    double closestDistance = Integer.MAX_VALUE;
                    for(int j=0;j<data.size();j++){
                        double d = Utils.getDistance(playerBody,data.get(j)[Constants.NECK]);
                        if(d < closestDistance){
                            closestIndex = j;
                            closestDistance = d;
                        }
                    }

                    //update playerBodyPoints
                    if(closestDistance <= Constants.PLAYER_RADIUS && closestIndex != -1) {
                        player.setKeyPoints(data.get(closestIndex));
                    }
                }


            }

            //pick player from people
            // 1) ignore player in game from data
            // 2) finding the longest raising hand person with least movement using list.
            if(userInfos.size()<3){
                boolean[] ignore = new boolean[data.size()]; // only picked Player will be false.
                int[] handCount = new int[data.size()];


                for(int i=0;i<data.size();i++){

                    // 1) ignore player in game from data
                    Point2D[] target = data.get(i);
                    for(int j=0;j<userInfos.size();j++){
                        UserInfo user = userInfos.get(j);
                        if(Utils.getDistance(target[Constants.NECK],user.getKeyPoints()[Constants.NECK])< Constants.PLAYER_RADIUS) {
                            ignore[i] = true;
                        }
                    }

                    // 2) finding how many times person raise hand using list.
                    for(int j=0;j<list.size();j++){
                        ArrayList<Point2D[]> pickedData = list.get(j);
                        int closestIndex = -1;
                        double closestDistance = Integer.MAX_VALUE;
                        for(int k=0;k<pickedData.size();k++){
                            double d = Utils.getDistance(pickedData.get(k)[Constants.NECK],target[Constants.NECK]);
                            if(d<closestDistance){
                                closestDistance = d;
                                closestIndex = k;
                            }
                        }

                        if(closestDistance <= Constants.PLAYER_RADIUS && closestIndex!=-1){
                            //check if the person is raising hand
                            if(Utils.isRaisingHands(pickedData.get(closestIndex))){
                                handCount[i]++;
                            }
                        }
                    }
                }

                // 3) find best score person

                int bestIndex = -1;
                int score = Integer.MIN_VALUE;
                for(int i=0;i<data.size();i++){
                    if(!ignore[i] && handCount[i]>score && handCount[i] >= 17){
                        score = handCount[i];
                        bestIndex = i;
                    }
                }

                //create userInfo
                if(bestIndex!=-1){
                    //setting user and add to userInfo
                    UserInfo user = new UserInfo(userInfos.size());
                    user.setKeyPoints(data.get(bestIndex));
                    userInfos.add(user);

                    Log.d("#####","new user added!");
                    UnityConnector.createWorms();
                    battleWorms.requestUserUpdate();
                }

            }
        }
        list.add(data);

        //TODO 여기 한번 봐보기
        for(int i=0;i<userInfos.size();i++){
            userKeypoints.set(i,userInfos.get(i).getKeyPoints());
        }
        return userKeypoints;
    }


}
