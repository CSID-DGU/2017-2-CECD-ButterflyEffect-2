package csid.butterflyeffect.network;

/**
 * Created by hanseungbeom on 2018. 1. 29..
 */

public interface HandleReceiveData {
    public abstract void handleReceiveData(String data);
    public abstract void infoHandler(String msg);
}