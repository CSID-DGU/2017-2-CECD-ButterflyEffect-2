package csid.butterflyeffect.game;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;

import csid.butterflyeffect.R;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.network.HandleReceiveData;
import csid.butterflyeffect.ui.MainActivity;
import csid.butterflyeffect.util.Utils;

public class BattleWorms implements HandleReceiveData {
    private MainActivity activity;
    private ArrayList<UserInfo> userInfos;
    private FrameFilter frameFilter;
    private ReadyFilter readyFilter;
    private boolean isPlaying;
    public BattleWorms(MainActivity activity){
        this.activity = activity;
        userInfos = new ArrayList<>();
        isPlaying = false;

        readyFilter = new ReadyFilter(userInfos);
    }

    public ArrayList<UserInfo> getUserInfos() {
        return userInfos;
    }

    @Override
    public void handleReceiveData(String data) {
        Log.d("#####","receive:"+data);

        //modify activity
        //activity.showData(data);

        if(!isPlaying && userInfos.size()<3) {
            //game ready logic
            ArrayList<Point2D[]> filteredData = readyFilter.filter(Utils.stringToKeyPoints(data));
            activity.drawSkeleton(filteredData);
        }
        else {
            //TODO game start..(at this moment, It is decided how many people will play)
            //activity.showData(data);
            ArrayList<Point2D[]> filteredKeyPoints = frameFilter.filter(Utils.stringToKeyPoints(data));
            int people = filteredKeyPoints.size();
            double[] userAngle = new double[people];
            for(int person = 0; person < people; person++) {
                userAngle[person] = Utils.getDegree(filteredKeyPoints.get(person));
            }
            //format : "Usercount angle1 angle2 angle3 angle3 ... "
            UnityPlayer.UnitySendMessage("Camera","WormMoveAngle", Utils.degreesToStr(userAngle));
        }
    }
}
