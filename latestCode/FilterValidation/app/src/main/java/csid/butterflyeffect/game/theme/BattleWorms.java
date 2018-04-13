package csid.butterflyeffect.game.theme;

import android.util.Log;

import java.util.ArrayList;

import csid.butterflyeffect.R;
import csid.butterflyeffect.game.filter.PlayFilter;
import csid.butterflyeffect.game.filter.ReadyFilter;
import csid.butterflyeffect.game.model.KeyPoint;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.network.HandleReceiveData;
import csid.butterflyeffect.activity.MainActivity;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

public class BattleWorms implements HandleReceiveData {
    private MainActivity activity;
    private ArrayList<UserInfo> userInfos;
    private PlayFilter playFilter;
    private ReadyFilter readyFilter;
    //private boolean isPlaying;
    private int state;

    public BattleWorms(MainActivity activity) {
        this.activity = activity;
        userInfos = new ArrayList<>();
        //isPlaying = false;
        state = Constants.STATE_WAIT;
        readyFilter = new ReadyFilter(userInfos, this);
        playFilter = new PlayFilter(userInfos, this);
    }
    public ArrayList<UserInfo> getUserInfos() {
        return userInfos;
    }

    @Override
    public void handleReceiveData(String jsonData) {
        //Log.d("#####","receive:"+jsonData);
        //modify activity
        //activity.showData(data);
        ArrayList<KeyPoint> userKeyPoints = Utils.getKeyPointsFromJsonData(jsonData);
        if (state == Constants.STATE_WAIT) {
            //game ready logic
            ArrayList<KeyPoint> filteredData = readyFilter.filter(userKeyPoints);
            activity.drawSkeleton(filteredData);
            //it will be called before state change to play
            if (userInfos.size() == Constants.PLAYER_NUMBER) {
                //isPlaying = true;
                state = Constants.STATE_READY;
                playFilter.saveFirstUserInfo();
                state = Constants.STATE_START;
            }
        } else if (state == Constants.STATE_READY) {
            Log.d("#####","ready state!!!");
            ArrayList<KeyPoint> filteredData = readyFilter.filter(userKeyPoints);
            activity.drawSkeleton(filteredData);
        } else if (state == Constants.STATE_START) {
            //game start..(at this moment, It is decided how many people will play)
            //filteredKeyPoints guarantees the order of user.
            ArrayList<KeyPoint> filteredKeyPoints = playFilter.distanceFilter(userKeyPoints);
            int people = filteredKeyPoints.size();
            double[] userAngle = new double[people];
            boolean[] userBoost = new boolean[people];
            for (int person = 0; person < people; person++) {
                userAngle[person] = Utils.getDegree(filteredKeyPoints.get(person).getSkeleton());
                userBoost[person] = Utils.isRaisingHands(filteredKeyPoints.get(person).getSkeleton());
            }
            //update boost view
            for (int i = 0; i < Constants.PLAYER_NUMBER; i++) {
                for (int j = 0; j < userBoost.length; j++) {
                    if (userInfos.get(i).getUserNumber() == j) {
                        userInfos.get(i).setBoost(userBoost[j]);
                        j = userBoost.length;
                    }
                }
            }
            //draw skeleton
            activity.drawSkeleton(filteredKeyPoints);
        }
    }
    public int getState(){
        return state;
    }
}