package csid.butterflyeffect.network;

/**
 * Created by hanseungbeom on 2018. 1. 16..
 */

        import android.util.Log;

        import org.opencv.core.Mat;

        import java.io.BufferedReader;
        import java.io.ByteArrayInputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.io.PrintWriter;
        import java.net.DatagramPacket;
        import java.net.DatagramSocket;
        import java.net.InetAddress;
        import java.net.Socket;
        import java.nio.ByteBuffer;

        import csid.butterflyeffect.util.Constants;

        import static csid.butterflyeffect.util.Constants.PACK_SIZE;

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
    public void sendUdpPacket(final Mat frame){
        Runnable r = new Runnable() {
            @Override
            public void run() {

                try{
                    int length = (int) (frame.total() * frame.elemSize());
                    byte encoded[] = new byte[length];
                    frame.get(0, 0, encoded);

                    //udpSocket.send(new DatagramPacket(encoded,encoded.length,InetAddress.getByName(ip),port));
                    //메세지가 너무 커서 줄여서 보내야 한다.
                    int total_pack = 1 + (encoded.length-1)/ PACK_SIZE;
                    Log.d("#####","totalpack:"+total_pack);
                    udpSocket.send(new DatagramPacket(ByteBuffer.allocate(4).putInt(total_pack).array(),4,InetAddress.getByName(ip),port));
                    for (int i = 0; i < total_pack; i++) {
                        Log.d("#####","i:"+i);
                        udpSocket.send(new DatagramPacket(encoded, i * PACK_SIZE, PACK_SIZE, InetAddress.getByName(ip), port));
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        };
        r.run();
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