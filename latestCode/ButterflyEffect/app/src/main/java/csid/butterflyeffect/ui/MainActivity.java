package csid.butterflyeffect.ui;

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
import android.widget.TextView;

import csid.butterflyeffect.PreviewSurface;
import csid.butterflyeffect.R;
import csid.butterflyeffect.network.SocketClient;
import csid.butterflyeffect.util.Constants;

public class MainActivity extends AppCompatActivity implements PreviewSurface.FrameHandler {
    private PreviewSurface mPriviewSurface;

    private Button mBtn;
    private TextView mText;
    public static SocketClient mSocket;
    public static boolean isSocketConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main);

        mBtn = (Button)findViewById(R.id.btn_capture);
        mText = (TextView)findViewById(R.id.tv_bitmap);
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
        new ConnectSocket().execute();

    }

    @Override
    public void getBitmap(String outStream) {
        mText.setText(outStream);
    }

    public class ConnectSocket extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                mSocket = new SocketClient(Constants.ADDR, Constants.PORT_NUM);

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
                isSocketConnected = true;
            }
            else
                Log.e("#####","socket connection error");
            super.onPostExecute(s);
        }
    }
}
