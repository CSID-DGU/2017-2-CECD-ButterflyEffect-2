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


public class SocketClient {
    private Socket tcpSocket;
    public static DatagramSocket udpSocket;
    private String ip;
    private int port;

    public SocketClient(String ip, int port) throws Exception {
        this.ip = ip;
        this.port = port;
        //tcpSocket = new Socket(ip, port);
        System.out.println("tcpSocket created!");
        udpSocket = new DatagramSocket(port);
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