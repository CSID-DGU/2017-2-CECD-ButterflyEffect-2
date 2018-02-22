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


    /*
    frame을 20장 검사하여 기준과 가장 근접한 포인트를 찾아 유저를 탐색한다.
    탐색하는 과정에서 얻은 x,y 값을 모두 더하고 이에 대한 평균을 구해 새로운 기준을 정의한다.
    */
    public ArrayList<Point2D[]> filter(){
        int userSize = userInfos.size();
        ArrayList<Point2D[]> result = new ArrayList<>();
        for(int user = 0; user < userSize; user++){
            ArrayList<Point2D[]> detector = new ArrayList<>();
            int candidate = 0;
            Point2D[] userInfo = userInfos.get(user).getKeyPoints();
            //number 1 is body of user in OpenPose
            double targetX = userInfo[1].x;
            double targetY = userInfo[1].y;
            //20장의 프레임 검사
            for(int frame = 0; frame < Constants.QUEUE_SIZE; frame++){
                //다수의 키 포인트(ex. 사람1, 사람2, 사람3, 사람4, 사람5....)
                ArrayList<Point2D[]> peopleKeyPoints = queue.poll();
                int peopleSize = peopleKeyPoints.size();
                double min = 2000000000.0;

                //한 명씩 뽑아내 검사
                for(int people = 0; people < peopleSize; people++){
                    Point2D[] keyPoints = peopleKeyPoints.get(people);
                    double distance = Math.sqrt(Math.pow((targetX - keyPoints[1].x),2) + Math.pow((targetY - keyPoints[1].y),2));
                    if(distance < min){
                        min = distance;
                        candidate = people;
                    }
                }
                //가장 가까운 사람 선택
                detector.add(peopleKeyPoints.get(candidate));
            }

            int detectorSize= detector.size();
            result.add(detector.get(detectorSize-1));
            double sumX = 0, sumY = 0, averageX = 0, averageY = 0;
            for(int people = 0; people < detectorSize; people++){
                Point2D[] detected = detector.get(people);
                sumX += detected[1].x;
                sumY += detected[1].y;
            }
            averageX = sumX/detectorSize;
            averageY = sumY/detectorSize;
            //userInfos.get(user).setKeyPoints();
        }
        return result;
    }
}
