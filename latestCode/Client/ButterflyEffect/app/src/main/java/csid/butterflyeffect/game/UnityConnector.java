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

    public static void updateUserBoost(String boosts){
        UnityPlayer.UnitySendMessage("Camera","WormBoost", boosts);
    }

    public static void createWorms(int r,int g,int b){
        UnityPlayer.UnitySendMessage("Camera","Spawn",r+" "+g+" "+b);

    }
    public static void startGame(int timeOut){
        UnityPlayer.UnitySendMessage("Camera","GameStart", String.valueOf(timeOut));
    }
    public static void restartGame(){
        UnityPlayer.UnitySendMessage("Camera","SceneRestart","");
    }

}
