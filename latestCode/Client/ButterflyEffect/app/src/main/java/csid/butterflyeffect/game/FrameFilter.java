package csid.butterflyeffect.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

public class FrameFilter {
    private ArrayList<UserInfo> userInfos;
    private Queue<ArrayList<Point2D[]>> queue;
    public FrameFilter(ArrayList<UserInfo> userInfos){
        this.userInfos = userInfos;
        queue = new LinkedList<>();
    }


    public void update(ArrayList<UserInfo> userInfo){
        if(queue.size() >= Constants.QUEUE_SIZE) {
            int userSize = userInfos.size();
            Point2D[] sum = new Point2D[userSize];
            for(int i = 0; i < userSize; i++){
                sum[i] = new Point2D();
            }
            for (int q = 0; q < Constants.QUEUE_SIZE; q++) {
                ArrayList<Point2D[]> usersKeyPoints = queue.poll();
                for (int user = 0; user < userSize; user++) {
                    Point2D[] userKeyPoints = usersKeyPoints.get(user);
                    sum[user].x += userKeyPoints[1].x;
                    sum[user].y += userKeyPoints[1].y;
                }
            }
            if (userSize != 0) {
                for (int user = 0; user < userSize; user++) {
                    userInfo.get(user).setNeck(new Point2D(sum[user].x / Constants.QUEUE_SIZE, sum[user].y / Constants.QUEUE_SIZE));
                }
            }
        }else{
            Log.d("QUEUE","Queue size is smaller than QUEUE_SIZE");
        }
    }

    public ArrayList<Point2D[]> filter(ArrayList<Point2D[]> peopleKeyPoints){
        int userSize = userInfos.size();
        ArrayList<Point2D[]> result = new ArrayList<>();
        if(queue.size() == Constants.QUEUE_SIZE){
            update(userInfos);
        }

        for(int user = 0; user < userSize; user++){
            int candidate = 0;
            Point2D neck = userInfos.get(user).getNeck();

            if(neck.x ==0 && neck.y==0) continue;
            int peopleSize = peopleKeyPoints.size();
            double min = 10000;

            for(int people = 0; people < peopleSize; people++){
                Point2D[] keyPoints = peopleKeyPoints.get(people);
                double distance = Utils.getDistance(neck, keyPoints[1]);
                if(distance <= Constants.PLAYER_RADIUS && distance < min){
                    min = distance;
                    candidate = people;
                }
            }
            if(candidate == 0)
                continue;
            result.add(peopleKeyPoints.get(candidate));
        }
        queue.offer(result);
        return result;
    }
}
