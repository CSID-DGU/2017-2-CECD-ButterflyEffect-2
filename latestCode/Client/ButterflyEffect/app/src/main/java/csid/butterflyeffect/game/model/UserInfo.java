package csid.butterflyeffect.game.model;

import java.util.ArrayList;

import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.util.Constants;

public class UserInfo {
    private Point2D nose,neck;
    private int userNumber;
    private boolean isPlaying;

    public UserInfo(int userNumber) {
        this.userNumber = userNumber;
        isPlaying = true;
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
}
