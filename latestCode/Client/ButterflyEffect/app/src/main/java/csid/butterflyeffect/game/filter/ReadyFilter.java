package csid.butterflyeffect.game.filter;

import android.util.Log;

import java.util.ArrayList;

import csid.butterflyeffect.game.BattleWorms;
import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.UnityConnector;
import csid.butterflyeffect.game.model.KeyPoint;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

/**
 * Created by hanseungbeom on 2018. 2. 23..
 */

public class ReadyFilter {
    private ArrayList<UserInfo> userInfos;
    private ArrayList<ArrayList<KeyPoint>> list;
    private ArrayList<KeyPoint> userKeypoints;
    private BattleWorms battleWorms;

    public ReadyFilter(ArrayList<UserInfo> userInfos,BattleWorms battleWorms){
        this.userInfos = userInfos;
        this.battleWorms = battleWorms;
        list = new ArrayList<>();
        userKeypoints = new ArrayList<>();
        for(int i=0;i<Constants.PLAYER_NUMBER;i++){
            KeyPoint keyPoint = new KeyPoint();
            Point2D[] plain = new Point2D[Constants.KEYPOINT_NUM];
            for(int j=0;j<Constants.KEYPOINT_NUM;j++){
                plain[j] = new Point2D();
            }
            keyPoint.setSkeleton(plain);
            userKeypoints.add(keyPoint);
        }
    }

    public ArrayList<KeyPoint> filter(ArrayList<KeyPoint> keyPoints){

        if(list.size() == Constants.LIST_SIZE) {
            //remove to keep the queue.size constant
            list.remove(list.get(0));

            //if player exists
            //update userinfos position with the latest points
            if(userInfos.size()!=0){
                //search the closest point for each player and update their points.
                for (int i = 0; i < userInfos.size(); i++) {
                    UserInfo player = userInfos.get(i);
                    Point2D playerBody = player.getKeyPoint().getSkeleton()[Constants.NECK];
                    int closestIndex = -1;
                    double closestDistance = Integer.MAX_VALUE;
                    for(int j=0;j<keyPoints.size();j++){
                        double d = Utils.getDistance(playerBody,keyPoints.get(j).getSkeleton()[Constants.NECK]);
                        if(d < closestDistance){
                            closestIndex = j;
                            closestDistance = d;
                        }
                    }

                    //update playerBodyPoints
                    if(closestDistance <= Constants.PLAYER_RADIUS && closestIndex != -1) {
                        player.getKeyPoint().setSkeleton(keyPoints.get(closestIndex).getSkeleton());
                    }
                }


            }

            //pick player from people
            // 1) ignore player in game from data
            // 2) finding the longest raising hand person with least movement using list.
            if(userInfos.size()<Constants.PLAYER_NUMBER){
                boolean[] ignore = new boolean[keyPoints.size()]; // only picked Player will be false.
                int[] handCount = new int[keyPoints.size()];


                for(int i=0;i<keyPoints.size();i++){

                    // 1) ignore player in game from data
                    Point2D[] target = keyPoints.get(i).getSkeleton();
                    for(int j=0;j<userInfos.size();j++){
                        UserInfo user = userInfos.get(j);
                        if(Utils.getDistance(target[Constants.NECK],user.getKeyPoint().getSkeleton()[Constants.NECK])< Constants.PLAYER_RADIUS) {
                            ignore[i] = true;
                        }
                    }

                    // 2) finding how many times person raise hand using list.
                    for(int j=0;j<list.size();j++){
                        ArrayList<KeyPoint> pickedData = list.get(j);
                        int closestIndex = -1;
                        double closestDistance = Integer.MAX_VALUE;
                        for(int k=0;k<pickedData.size();k++){
                            double d = Utils.getDistance(pickedData.get(k).getSkeleton()[Constants.NECK],target[Constants.NECK]);
                            if(d<closestDistance){
                                closestDistance = d;
                                closestIndex = k;
                            }
                        }

                        if(closestDistance <= Constants.PLAYER_RADIUS && closestIndex!=-1){
                            //check if the person is raising hand
                            if(Utils.isRaisingHands(pickedData.get(closestIndex).getSkeleton())){
                                handCount[i]++;
                            }
                        }
                    }
                }

                // 3) find best score person

                int bestIndex = -1;
                int score = Integer.MIN_VALUE;
                for(int i=0;i<keyPoints.size();i++){
                    if(!ignore[i] && handCount[i]>score && handCount[i] >= 10){
                        score = handCount[i];
                        bestIndex = i;
                    }
                }

                //create userInfo
                if(bestIndex!=-1){
                    //setting user and add to userInfo
                    UserInfo user = new UserInfo(userInfos.size());
                    user.getKeyPoint().setSkeleton(keyPoints.get(bestIndex).getSkeleton());
                    userInfos.add(user);

                    Log.d("#####","new user added!");
                    UnityConnector.createWorms();
                    battleWorms.requestUserUpdate();
                }

            }
        }
        list.add(keyPoints);

        //TODO 여기 한번 봐보기
        for(int i=0;i<userInfos.size();i++){
            userKeypoints.get(i).setSkeleton(userInfos.get(i).getKeyPoint().getSkeleton());
        }
        return userKeypoints;
    }


}
