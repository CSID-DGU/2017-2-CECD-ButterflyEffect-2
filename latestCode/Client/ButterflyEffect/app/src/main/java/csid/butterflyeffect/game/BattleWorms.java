package csid.butterflyeffect.game;

import android.graphics.Point;

import java.lang.reflect.Array;
import java.util.ArrayList;

import csid.butterflyeffect.game.filter.PlayFilter;
import csid.butterflyeffect.game.filter.ReadyFilter;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.network.HandleReceiveData;
import csid.butterflyeffect.ui.MainActivity;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

public class BattleWorms implements HandleReceiveData {
    private MainActivity activity;
    private ArrayList<UserInfo> userInfos;
    private PlayFilter playFilter;
    private ReadyFilter readyFilter;
    private boolean isPlaying;
    public BattleWorms(MainActivity activity){
        this.activity = activity;
        userInfos = new ArrayList<>();
        isPlaying = false;

        readyFilter = new ReadyFilter(userInfos,this);
        playFilter = new PlayFilter(userInfos,this);
    }

    public void requestUserUpdate(int position){
        activity.updateUser(position);
    }

    public void requestUserUpdate(){
        activity.updateUser();
    }

    public ArrayList<UserInfo> getUserInfos() {
        return userInfos;
    }

    @Override
    public void handleReceiveData(String data) {
        //Log.d("#####","receive:"+data);

        //modify activity
        //activity.showData(data);

        if(userInfos.size()< Constants.PLAYER_NUMBER) {
            //game ready logic
            ArrayList<Point2D[]> filteredData = readyFilter.filter(Utils.stringToKeyPoints(data));
            activity.drawSkeleton(filteredData);

            //it will be called before state change to play
            if(userInfos.size()==Constants.PLAYER_NUMBER){
                isPlaying = true;
                playFilter.saveFirstUserInfo();
                UnityConnector.startGame();
            }
        }
        else {
            //game start..(at this moment, It is decided how many people will play)
            //filteredKeyPoints guarantees the order of user.
            ArrayList<Point2D[]> filteredKeyPoints = playFilter.filter(Utils.stringToKeyPoints(data));
            int people = filteredKeyPoints.size();
            double[] userAngle = new double[people];
            boolean[] userBoost = new boolean[people];
            for(int person = 0; person < people; person++) {
                userAngle[person] = Utils.getDegree(filteredKeyPoints.get(person));
                userBoost[person] = Utils.isRaisingHands(filteredKeyPoints.get(person));
            }
            //format : "Usercount angle1 angle2 angle3  ... "
            UnityConnector.updateUserAngle(Utils.degreesToStr(userAngle));
            UnityConnector.updateUserBoost(Utils.boostToStr(userBoost));

            //update boost view
            for(int i=0;i<Constants.PLAYER_NUMBER;i++){
                for(int j=0;j<userBoost.length;j++){
                    if(userInfos.get(i).getUserNumber()==j) {
                        userInfos.get(i).setBoost(userBoost[j]);
                        j=userBoost.length;
                    }
                }
            }
            activity.updateUser();

            //draw skeleton
            activity.drawSkeleton(filteredKeyPoints);
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
