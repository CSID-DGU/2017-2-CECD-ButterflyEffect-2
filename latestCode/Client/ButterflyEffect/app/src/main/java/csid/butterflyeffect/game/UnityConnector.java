package csid.butterflyeffect.game;

import com.unity3d.player.UnityPlayer;

import csid.butterflyeffect.util.Utils;

/**
 * Created by hanseungbeom on 2018. 3. 1..
 */

public class UnityConnector {

    //usernumber angle1 angle2 angle3 ...
    public static void updateUserAngle(String angles){
        UnityPlayer.UnitySendMessage("Camera","WormMoveAngle", angles);
    }

    public static void updateUserBoost(boolean[] boosts){
        for(int i=0;i<boosts.length;i++){
            //userindex 1or0 ... (1/0 means true or false)
            if(boosts[i])
               UnityPlayer.UnitySendMessage("Camera","WormBoost", i+" true");
        }
    }
    
    public static void createWorms(){
        UnityPlayer.UnitySendMessage("Camera","Spawn","" );

    }
}
