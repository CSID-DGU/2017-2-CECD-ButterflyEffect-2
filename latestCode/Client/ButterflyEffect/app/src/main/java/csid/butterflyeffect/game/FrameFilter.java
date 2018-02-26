package csid.butterflyeffect.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;

public class FrameFilter {
    private ArrayList<UserInfo> userInfos;
    private Queue<ArrayList<Point2D[]>> queue;
    public FrameFilter(ArrayList<UserInfo> userInfos){
        this.userInfos = userInfos;
        queue = new LinkedList<>();
    }

    public void insert(ArrayList<Point2D[]> data){
        if(queue.size() == Constants.QUEUE_SIZE)
            queue.poll();
        queue.add(data);
    }

    public void update(ArrayList<UserInfo> userInfo){
        if(queue.size() >= Constants.QUEUE_SIZE) {
            int userSize = userInfos.size();
            Point2D[] sum = new Point2D[userSize];
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
                    userInfo.get(user).setNeck(new Point2D(sum[user].x / userSize, sum[user].y / userSize));
                }
            }
        }else{
            Log.d("QUEUE","Queue size is smaller than QUEUE_SIZE");
        }
    }

    public ArrayList<Point2D[]> filter(ArrayList<Point2D[]> peopleKeyPoints){
        int userSize = userInfos.size();
        ArrayList<Point2D[]> result = new ArrayList<>();
        for(int user = 0; user < userSize; user++){
            int candidate = 0;
            Point2D neck = userInfos.get(user).getNeck();
            int peopleSize = peopleKeyPoints.size();
            double min = Double.MAX_VALUE;

            for(int people = 0; people < peopleSize; people++){
                Point2D[] keyPoints = peopleKeyPoints.get(people);
                double distance = Math.sqrt(Math.pow((neck.x - keyPoints[1].x),2) + Math.pow((neck.y - keyPoints[1].y),2));
                if(distance < min){
                    min = distance;
                    candidate = people;
                }
            }
            result.add(peopleKeyPoints.get(candidate));
        }
        insert(result);
        return result;
    }
}
