package csid.butterflyeffect.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import csid.butterflyeffect.R;
import csid.butterflyeffect.network.HandleReceiveData;
import csid.butterflyeffect.network.SocketClient;
import csid.butterflyeffect.util.Constants;

public class IpPortActivity extends AppCompatActivity implements HandleReceiveData {

    private EditText mIpView;
    private EditText mPortView;
    private LinearLayout mBtn;
    private SocketClient mSocket;
    private FrameLayout mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_port);

        mIpView = (EditText)findViewById(R.id.et_ip);
        mPortView = (EditText)findViewById(R.id.et_port);
        mProgress = (FrameLayout)findViewById(R.id.fr_progress);
        mBtn = (LinearLayout)findViewById(R.id.btn_connect);

        mIpView.setText(Constants.ADDR);
        mPortView.setText(String.valueOf(Constants.PORT_NUM));
        mSocket = SocketClient.getInstance();
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.setReceiveCallback(IpPortActivity.this);
                new ConnectSocket().execute();
            }
        });

    }

    @Override
    public void handleReceiveData(String data) {
        //nothing
    }

    @Override
    public void infoHandler(final String msg) {
        showToast(msg);
    }

    public class ConnectSocket extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            //connecting tcp,udp
            showProgressBar();
            mSocket.connect();
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideProgressBar();
            if(mSocket.isConnected()){
                Intent i = new Intent(IpPortActivity.this,MainActivity.class);
                startActivity(i);
            }
        }
    }
    public void showProgressBar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.VISIBLE);
            }
        });


    }
    public void hideProgressBar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void showToast(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(IpPortActivity.this,str,Toast.LENGTH_SHORT).show();
            }
        });

    }
}
