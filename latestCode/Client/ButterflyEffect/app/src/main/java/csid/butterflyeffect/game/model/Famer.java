package csid.butterflyeffect.game.model;

import android.support.annotation.NonNull;

/**
 * Created by hanseungbeom on 2018. 4. 22..
 */

public class Famer implements Comparable<Famer>{
    private int score;
    private String phoneNumber;
    private long updatedTime;
    private String imageUrl;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int compareTo(@NonNull Famer o) {
        return o.score-score;
    }
}
