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
    private boolean isPlaying;
    public BattleWorms(MainActivity activity){
        this.activity = activity;
        userInfos = new ArrayList<>();
        isPlaying = false;
    }

    public ArrayList<UserInfo> getUserInfos() {
        return userInfos;
    }

    @Override
    public void handleReceiveData(String data) {
        Log.d("#####","receive:"+data);

        //modify activity
        activity.showData(data);

        if(isPlaying) {
            //TODO user waiting..
            //activity.showData(data);
        }
        else {
            //TODO game start..(at this moment, It is decided how many people will play)
            //activity.showData(data);
            String userAngle = Utils.stringToDegree(data);
            //format : "Usercount angle1 angle2 angle3 angle3 ... "
            UnityPlayer.UnitySendMessage("Camera","WormMoveAngle", userAngle);
        }

    }


}
