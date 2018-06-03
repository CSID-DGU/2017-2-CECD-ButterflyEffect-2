package csid.butterflyeffect.game;

import android.hardware.Camera;

import java.util.ArrayList;

import csid.butterflyeffect.R;
import csid.butterflyeffect.game.filter.PlayFilter;
import csid.butterflyeffect.game.filter.ReadyFilter;
import csid.butterflyeffect.game.model.KeyPoint;
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
    private boolean isCameraChanged;
    //private boolean isPlaying;
    private int state;

    public BattleWorms(MainActivity activity) {
        this.activity = activity;
        userInfos = new ArrayList<>();
        //isPlaying = false;
        state = Constants.STATE_WAIT;
        readyFilter = new ReadyFilter(userInfos, this);
        playFilter = new PlayFilter(userInfos, this);
        isCameraChanged = false;
    }

    public void init(){
        userInfos = null;
        userInfos = new ArrayList<>();
        //isPlaying = false;
        state = Constants.STATE_WAIT;
        readyFilter = new ReadyFilter(userInfos, this);
        playFilter = new PlayFilter(userInfos, this);
        isCameraChanged = false;
    }
    public void requestUserUpdate(int position) {
        activity.updateUser(position);
    }

    public void requestUserUpdate() {
        activity.updateUser();
    }

    public ArrayList<UserInfo> getUserInfos() {
        return userInfos;
    }

    @Override
    public void handleReceiveData(String jsonData) {

        //modify activity
        //activity.showData(data);
        ArrayList<KeyPoint> userKeyPoints = Utils.getKeyPointsFromJsonData(jsonData);

        if (state == Constants.STATE_WAIT) {
            //game ready logic
            ArrayList<KeyPoint> filteredData = readyFilter.filter(userKeyPoints);
            activity.drawWorms(userInfos);

            //it will be called before state change to play
            if (userInfos.size() == Constants.PLAYER_NUMBER) {
                //isPlaying = true;
                state = Constants.STATE_READY;
                UnityConnector.startGame(Constants.TIME_OUT);
                playFilter.saveFirstUserInfo();

                // If the number of players is over 1, then calculate appropriate player radius of them
                if (Constants.PLAYER_NUMBER > 1) {
                    Constants.PLAYER_RADIUS = Utils.calcPlayerRadius(userInfos);
                }
                Thread startThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for(int i = 0; i<Constants.GAME_WAITING_TIME; i++){
                                activity.showToast(Constants.GAME_WAITING_TIME -i+activity.getResources().getString(R.string.remaining_wait_time));
                                Thread.sleep(1000);
                            }
                            Thread.sleep(1000);
                            state = Constants.STATE_START;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                startThread.start();
            }
        } else if (state == Constants.STATE_READY) {
            ArrayList<KeyPoint> filteredData = readyFilter.filter(userKeyPoints);
            activity.drawWorms(userInfos);
        } else if (state == Constants.STATE_START) {
            //game start..(at this moment, It is decided how many people will play)
            //filteredKeyPoints guarantees the order of user.
            ArrayList<KeyPoint> filteredKeyPoints = playFilter.filter(userKeyPoints);
            int people = filteredKeyPoints.size();
            double[] userAngle = new double[people];
            boolean[] userBoost = new boolean[people];
            for (int person = 0; person < people; person++) {
                userAngle[person] = Utils.getDegree(filteredKeyPoints.get(person).getSkeleton());
                userBoost[person] = Utils.isRaisingHands(filteredKeyPoints.get(person).getSkeleton());
            }
            //format : "Usercount angle1 angle2 angle3  ... "
            UnityConnector.updateUserAngle(Utils.degreesToStr(userAngle));
            UnityConnector.updateUserBoost(Utils.boostToStr(userBoost));

            //update boost view
            for (int i = 0; i < Constants.PLAYER_NUMBER; i++) {
                for (int j = 0; j < userBoost.length; j++) {
                    if (userInfos.get(i).getUserNumber() == j) {
                        userInfos.get(i).setBoost(userBoost[j]);
                        j = userBoost.length;
                    }
                }
            }
            activity.updateUser();

            //draw skeleton
            activity.drawWorms(userInfos);
        }
        else if(state == Constants.STATE_END){
           /* if(isCameraChanged) {
                ArrayList<KeyPoint> filteredKeyPoints = playFilter.filter(userKeyPoints);
                activity.drawWorms(userInfos);
            }*/
            //TODO show winner's face to winner's photoZone

        }
    }
    public void setState(int state){
        this.state = state;
    }
    public int getState(){
        return state;
    }


    //TODO 게임이 종료되고 재 실행될때 BattleWorms가 플레이어가 교체되지 않는거 수정
    public int getPlayerNumber(){
        return userInfos.size();
    }
    public UserInfo getWinner(){return
            userInfos.get(0);
    }

    public void showRankingView(){
        activity.showRankingView();
    }
    public void hideRankingView(){
        activity.hideRankingView();
    }

    public void changeForceUserCoordinates(Camera.Size before, Camera.Size after){
        for(UserInfo users : userInfos){
            Point2D[] afterSkeleton = Utils.cvtKeyPointToRatio(before,after, users.getKeyPoint().getSkeleton());
            users.getKeyPoint().setSkeleton(afterSkeleton);
        }
        isCameraChanged = true;
    }
}