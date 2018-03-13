package csid.butterflyeffect.game.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.util.Constants;

public class UserInfo implements Comparable<UserInfo>{
    private Point2D nose,neck;
    private int userNumber;
    private boolean isPlaying;
    private boolean isBoost;
    private int score;
    private ArrayList<Integer> colors;
    private Bitmap userProfile;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public UserInfo(int userNumber) {
        this.userNumber = userNumber;
        isPlaying = true;
        isBoost = false;
        score = 0;
        colors = new ArrayList<>();
    }

    public void addColor(int color) {
        colors.add(color);
    }
    public ArrayList<Integer> getColors() {
        return colors;
    }

    public void setColors(ArrayList<Integer> colors) {
        this.colors = colors;
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

    public boolean isBoost() {
        return isBoost;
    }

    public void setBoost(boolean boost) {
        isBoost = boost;
    }

    @Override
    public int compareTo(@NonNull UserInfo o) {
        return o.score-score;
    }

    public Bitmap getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(Bitmap userProfile) {
        this.userProfile = userProfile;
    }

}
