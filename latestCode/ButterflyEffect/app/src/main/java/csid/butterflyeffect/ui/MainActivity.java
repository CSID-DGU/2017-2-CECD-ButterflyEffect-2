package csid.butterflyeffect.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import csid.butterflyeffect.PreviewSurface;
import csid.butterflyeffect.R;
import csid.butterflyeffect.network.HandleReceiveData;
import csid.butterflyeffect.network.SocketClient;

public class MainActivity extends AppCompatActivity implements PreviewSurface.FrameHandler, HandleReceiveData {


    private PreviewSurface mPriviewSurface;

    private Button mBtn;
    private ImageView mBitmapView;
    private TextView mTcpDataView;
    private SocketClient mSocket;
    public static boolean isSocketConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mBtn = (Button)findViewById(R.id.btn_capture);
        mBitmapView = (ImageView) findViewById(R.id.iv_bitmap);
        mTcpDataView = (TextView)findViewById(R.id.tv_tcp);
        getWindow().setFormat(PixelFormat.UNKNOWN);

        mPriviewSurface = (PreviewSurface) findViewById(R.id.sv);
        mPriviewSurface.setFrameHandler(this);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPriviewSurface.refreshFocus();
            }
        });

        // TCP & UDP 연결
        mSocket = SocketClient.getInstance();
        mSocket.setReceiveCallback(this);
        mSocket.startTcpService();
    }

    @Override
    public void getJpegFrame(final byte[] frame) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mSocket.isConnected()) {
                    mSocket.sendUdpPacket(frame);
                    Log.d("#####","length:"+frame.length);

                }
                Bitmap bit = BitmapFactory.decodeByteArray(frame, 0, frame.length);
                mBitmapView.setImageBitmap(bit);
            }
        });
    }

    @Override
    public void handleReceiveData(final String data) {
        Log.d("#####","receive:"+data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTcpDataView.setText(data);
            }
        });
    }

    @Override
    public void infoHandler(final String msg) {
        showToast(msg);
    }

    public void showToast(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
            }
        });

    }




}
