package com.cse.tcpclient;

/**
 * Created by sy081 on 2018-01-22.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {
    final public static String SERVER_IP = "127.0.0.1";
    final public static int SERVER_PORT = 9500;
    private String mServerMessage;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;

    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            /*byte[] bytes = message.getBytes();
            int len = bytes.length;
            mBufferOut.println(len);
            for(int i = 0; i < len ; i++) {
                mBufferOut.println(bytes[i]);
            }*/
            mBufferOut.println(message);
            //mBufferOut.write(message);
            mBufferOut.flush();
        }
    }

    public void stopClient() {

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.e("TCP Client", "C: Connecting...");

            Socket socket = new Socket(serverAddr, SERVER_PORT);
            Log.e("TCP Client", serverAddr.toString());

            try {
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (mRun) {
                    mServerMessage = mBufferIn.readLine();
                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mServerMessage);
                    }
                }
                //Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");
            } catch (Exception e) {
                //Log.e("TCP", "S: Error", e);
            } finally {
                socket.close();
            }
        } catch (Exception e) {
            //Log.e("TCP", "C: Error", e);
        }
    }
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
