package com.cse.tcpclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TcpClient mTcpClient;
    EditText e1;
    TextView t1;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e1 = findViewById(R.id.editText);
        t1 = findViewById(R.id.textView);

        new ConnectTask().execute();
    }

    public void send_text(View v) {
        //message = e1.getText().toString();
        String msg = "[12345] abc";
        mTcpClient.sendMessage(msg);
    }
    public class ConnectTask extends AsyncTask<String, String, TcpClient> {
        @Override
        protected TcpClient doInBackground(String... message) {

            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    publishProgress(message);
                }
            });
            mTcpClient.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d("test", "response " + values[0]);
            t1.setText(values[0]);
        }
    }
}
