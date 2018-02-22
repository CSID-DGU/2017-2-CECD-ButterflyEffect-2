package csid.butterflyeffect.game;

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
        if(queue.size()== Constants.QUEUE_SIZE)
            queue.poll();

        queue.add(data);
    }

    public ArrayList<Point2D[]> filter(){

        return null;
    }
}
