package csid.butterflyeffect.model;

import java.util.ArrayList;

import csid.butterflyeffect.util.Point2D;
import csid.butterflyeffect.util.Utils;

/**
 * Created by hanseungbeom on 2018. 2. 22..
 */

public class UserInfo {
    private ArrayList<Point2D[]> keyPoints;
    private int userNumber;
    private boolean isPlaying;
    private int score;

    public UserInfo(int userNumber) {
        this.userNumber = userNumber;
        isPlaying = true;
    }

    public ArrayList<Point2D[]> getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(ArrayList<Point2D[]> keyPoints) {
        this.keyPoints = keyPoints;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
