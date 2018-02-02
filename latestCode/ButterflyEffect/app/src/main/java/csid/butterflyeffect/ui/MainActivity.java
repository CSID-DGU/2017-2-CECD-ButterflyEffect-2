package csid.butterflyeffect.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.unity3d.player.UnityPlayer;


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
    private UnityPlayer mUnityPlayer;
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


        // Init Unity in FrameLayout
        mUnityPlayer = new UnityPlayer(this);
        int glesMode = mUnityPlayer.getSettings().getInt("gles_mode", 1);
        boolean trueColor8888 = false;
        mUnityPlayer.init(glesMode, trueColor8888);

        setContentView(R.layout.activity_main);
        FrameLayout layout = (    FrameLayout)findViewById(R.id.fr_unityView);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layout.addView(mUnityPlayer.getView(), 0, lp);
        mUnityPlayer.windowFocusChanged(true);
        mUnityPlayer.resume();
    }

    public void initActivity(String tagFromUnity, String messageFromUnity)
    {
        UnityPlayer.UnitySendMessage("AndroidManager", "AndroidLog", "[" + tagFromUnity + "]" + messageFromUnity);

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
                //Bitmap bit = BitmapFactory.decodeByteArray(frame, 0, frame.length);
                //mBitmapView.setImageBitmap(bit);
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
    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override protected void onStart()
    {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override protected void onStop()
    {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }




}
