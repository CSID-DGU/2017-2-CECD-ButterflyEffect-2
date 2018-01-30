package csid.butterflyeffect.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.PixelFormat;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import csid.butterflyeffect.PreviewSurface;
import csid.butterflyeffect.R;
import csid.butterflyeffect.network.HandleReceiveData;
import csid.butterflyeffect.network.SocketClient;
import csid.butterflyeffect.util.Constants;

public class MainActivity extends AppCompatActivity implements PreviewSurface.FrameHandler, HandleReceiveData {


    private PreviewSurface mPriviewSurface;

    private Button mBtn,mConnectBtn;
    private ImageView mBitmapView;
    private TextView mTcpDataView;
    private TextView mTcpConnection;
    private SocketClient mSocket;
    private EditText mPort;
    public static boolean isSocketConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main);

        mBtn = (Button)findViewById(R.id.btn_capture);
        mConnectBtn = (Button)findViewById(R.id.btn_connect);
        mBitmapView = (ImageView) findViewById(R.id.iv_bitmap);
        mTcpDataView = (TextView)findViewById(R.id.tv_tcp);
        mTcpConnection = (TextView)findViewById(R.id.tv_connection);
        mPort = (EditText)findViewById(R.id.et_port);
        getWindow().setFormat(PixelFormat.UNKNOWN);

        mPriviewSurface = (PreviewSurface) findViewById(R.id.sv);
        mPriviewSurface.setFrameHandler(this);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPriviewSurface.refreshFocus();
            }
        });
        mPort.setText(String.valueOf(Constants.PORT_NUM));


        // TCP & UDP 연결
        mSocket = new SocketClient();
        mSocket.setReceiveCallback(this);
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
    }

    @Override
    public void getJpegFrame(final byte[] frame) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSocket.sendUdpPacket(frame);
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
    public void errorHandler() {
        mTcpConnection.setText("socket had a error!");
    }

    public void connect(){
        if(!mPort.getText().equals("")) {
            Constants.PORT_NUM = Integer.parseInt(mPort.getText().toString());
            mTcpConnection.setText("Connecting...");
            new ConnectSocket().execute();
        }
        else{
            Toast.makeText(MainActivity.this,"포트번호를 입력해주세요!",Toast.LENGTH_SHORT).show();
        }
    }
    public class ConnectSocket extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                //connecting tcp,udp
                mSocket.connect();
            }
            catch (Exception e){
                e.printStackTrace();
                return Constants.FAILURE;
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            if(!s.equals(Constants.FAILURE)) {
                Log.d("#####","socket connection success");
                mTcpConnection.setText("socket connection success");
                isSocketConnected = true;
            }
            else
                Log.e("#####","socket connection error");
            mTcpConnection.setText("socket connection error");
            super.onPostExecute(s);
        }
    }



}
