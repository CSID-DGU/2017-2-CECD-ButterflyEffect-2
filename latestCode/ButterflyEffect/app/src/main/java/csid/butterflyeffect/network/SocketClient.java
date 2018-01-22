package csid.butterflyeffect.network;

/**
 * Created by hanseungbeom on 2018. 1. 16..
 */

        import java.io.BufferedReader;
        import java.io.ByteArrayInputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.io.PrintWriter;
        import java.net.DatagramPacket;
        import java.net.DatagramSocket;
        import java.net.InetAddress;
        import java.net.Socket;

public class SocketClient {
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private String ip;
    private int port;

    public SocketClient(String ip, int port) throws Exception{
        this.ip = ip;
        this.port = port;
        tcpSocket = new Socket(ip, port);
        System.out.println("tcpSocket created!");
        udpSocket = new DatagramSocket(port);
    }
    public void sendUdpPacket(byte[] buf, int len){
        byte[] byteBuffer = new byte[1024];
        try{
            OutputStream outsocket = tcpSocket.getOutputStream();
            ByteArrayInputStream inputstream = new ByteArrayInputStream(buf);
            int amount;
            while ((amount = inputstream.read(byteBuffer)) != -1) {
                outsocket.write(byteBuffer, 0, amount);
            }
            udpSocket.send(new DatagramPacket(buf,len,InetAddress.getByName(ip),port));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void sendTcpPacket(String message) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new OutputStreamWriter(
                    tcpSocket.getOutputStream()), true);
            writer.println(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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