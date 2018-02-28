package csid.butterflyeffect.game.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.util.Constants;

public class UserInfo implements Comparable<UserInfo>{
    private Point2D nose,neck;
    private int userNumber;
    private boolean isPlaying;
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public UserInfo(int userNumber) {
        this.userNumber = userNumber;
        isPlaying = true;
        score = 0;
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

    public Point2D getNose() {
        return nose;
    }

    public void setNose(Point2D nose) {
        this.nose = nose;
    }

    public Point2D getNeck() {
        return neck;
    }

    public void setNeck(Point2D neck) {
        this.neck = neck;
    }

    @Override
    public int compareTo(@NonNull UserInfo o) {
        return o.score-score;
    }
}
