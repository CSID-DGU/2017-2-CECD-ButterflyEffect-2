package csid.butterflyeffect.game.filter;

import android.util.Log;

import java.util.ArrayList;

import csid.butterflyeffect.game.theme.BattleWorms;
import csid.butterflyeffect.util.Point2D;
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

    public ArrayList<KeyPoint> filter(ArrayList<KeyPoint> keyPoints) {
        for(int i=0;i<keyPoints.size();i++){
            UserInfo user = new UserInfo(userInfos.size());
            user.getKeyPoint().setSkeleton(keyPoints.get(i).getSkeleton());
            userInfos.add(user);
        }
        return keyPoints;
    }
}
