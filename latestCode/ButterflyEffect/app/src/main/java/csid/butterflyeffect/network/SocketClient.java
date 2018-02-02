package csid.butterflyeffect.network;

/**
 * Created by hanseungbeom on 2018. 1. 16..
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import csid.butterflyeffect.util.Constants;


public class SocketClient {

    private static SocketClient instance;
    public static synchronized SocketClient getInstance () {
        if (instance == null)
            instance = new SocketClient();
        return instance;
    }

    private boolean isConnected;
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private HandleReceiveData callback;
    public void setReceiveCallback(HandleReceiveData callback) {
        this.callback = callback;
    }


    private SocketClient() {
        isConnected = false;
    }

    public void connect()  {
        try {
            callback.infoHandler("connecting..");
            //tcpSocket = new Socket(Constants.ADDR, Constants.PORT_NUM);
            String hostname = Constants.ADDR;
            int port = Constants.PORT_NUM;
            Log.d("#####","port:"+Constants.PORT_NUM);
            Log.d("#####","ip:"+Constants.ADDR);


            SocketAddress socketAddress = new InetSocketAddress(hostname, port);
            tcpSocket = new Socket();
            tcpSocket.connect(socketAddress, Constants.TIME_OUT_FOR_TCP_CONNECTION);

            Log.d("#####", "tcpSocket created!");
            //if connection success, run tcpService
            udpSocket = new DatagramSocket(Constants.PORT_NUM);
            isConnected = true;
            Log.d("#####", "udpSocket created!");
            callback.infoHandler("connected successfully!");

        }catch(Exception e){
            e.printStackTrace();
            callback.infoHandler("connection failed");
        }
    }
    public void startTcpService(){
        Thread tcpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //run tcp service
                    tcpService();
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.infoHandler("tcp service error!");
                }
            }
        });
        tcpThread.start();
    }
    public void tcpService() throws IOException{
        Log.d("#####","tcpService Start!");
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        while (true) {
            String str = reader.readLine();
            if(str==null)
                break;

            callback.handleReceiveData(str);
        }
    }

    public void sendUdpPacket(final byte[] data) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (udpSocket != null) {
                        SocketAddress socketAddres = new InetSocketAddress(Constants.ADDR, Constants.PORT_NUM);
                        udpSocket.send(new DatagramPacket(data, data.length, socketAddres));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.infoHandler("send udp error!");
                }
            }
        });
        thread.start();

    }


    /*
     * Ready for use.
     */
    public void close() {
        try {

            if (tcpSocket != null && !tcpSocket.isClosed())
                tcpSocket.close();

            if(udpSocket != null && !udpSocket.isClosed())
                udpSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}