package csid.butterflyeffect.network;

/**
 * Created by hanseungbeom on 2018. 1. 16..
 */

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
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private String ip;
    private int port;
    private HandleReceiveData callback;

    public void setReceiveCallback(HandleReceiveData callback) {
        this.callback = callback;
    }


    public SocketClient() {
        this.ip = Constants.ADDR;
        this.port = Constants.PORT_NUM;
    }

    public void connect() throws Exception {
        tcpSocket = new Socket(ip, port);
        System.out.println("tcpSocket created!");
        udpSocket = new DatagramSocket(port);
        System.out.println("udpSocket created!");
    }
    public void tcpService() throws IOException{
        while (true) {
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            callback.handleReceiveData(reader.readLine());
        }
    }

    public void sendUdpPacket(final byte[] data) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (udpSocket != null) {
                        SocketAddress socketAddres = new InetSocketAddress(ip, port);
                        udpSocket.send(new DatagramPacket(data, data.length, socketAddres));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    /*
    This function blocks.
    */
    public String readLine() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    tcpSocket.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    /*
     * Ready for use.
     */
    public void close() {
        try {
            if (tcpSocket != null && !tcpSocket.isClosed())
                tcpSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}