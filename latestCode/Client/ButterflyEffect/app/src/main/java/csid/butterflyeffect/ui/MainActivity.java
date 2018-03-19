package csid.butterflyeffect.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import csid.butterflyeffect.PreviewSurface;
import csid.butterflyeffect.R;
import csid.butterflyeffect.game.BattleWorms;
import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.network.HandleReceiveData;
import csid.butterflyeffect.network.HandleSocketError;
import csid.butterflyeffect.network.SocketClient;
import csid.butterflyeffect.ui.adapter.UserAdapter;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;

public class MainActivity extends AppCompatActivity implements PreviewSurface.FrameHandler, HandleSocketError {
    private PreviewSurface mPriviewSurface;

    private Button mBtn;
    private ImageView mBitmapView;
    private TextView mTcpDataView,mUserAngleView;
    private SocketClient mSocket;
    private UnityPlayer mUnityPlayer;
    private SkeletonView mSkeleton;
    private FrameLayout mPreview;
    private BattleWorms mBattleWorms;
    private RecyclerView mRecyclerView;
    private UserAdapter mAdapter;
    private Toast mToast;
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
        mUserAngleView = (TextView)findViewById(R.id.tv_angle);
        mSkeleton = (SkeletonView)findViewById(R.id.skeleton_view);
        mPreview = (FrameLayout)findViewById(R.id.fr_preview);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_user);

        getWindow().setFormat(PixelFormat.UNKNOWN);


        mPriviewSurface = (PreviewSurface) findViewById(R.id.sv);
        mPriviewSurface.setFrameHandler(this);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPriviewSurface.refreshFocus();

            }
        });


        //get preview screen size
        ViewTreeObserver vto = mPreview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                Constants.PREVIEW_WIDTH  = mPreview.getMeasuredWidth();
                Constants.PREVIEW_HEIGHT = mPreview.getMeasuredHeight();

            }
        });

        //BattleWorms 초기화
        mBattleWorms = new BattleWorms(this);

        //setting recyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new UserAdapter(this,mBattleWorms.getUserInfos());
        mRecyclerView.setAdapter(mAdapter);


        // TCP & UDP 연결
        mSocket = SocketClient.getInstance();
        mSocket.setErrorCallback(this);
        mSocket.setReceiveCallback(mBattleWorms);
        mSocket.startTcpService();


        //Unity in FrameLayout
        mUnityPlayer = new UnityPlayer(this);
        int glesMode = mUnityPlayer.getSettings().getInt("gles_mode", 1);
        boolean trueColor8888 = false;
        mUnityPlayer.init(glesMode, trueColor8888);

        FrameLayout layout = (FrameLayout)findViewById(R.id.fr_unityView);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layout.addView(mUnityPlayer.getView(), 0, lp);
        //mUnityPlayer.windowFocusChanged(true);
        mUnityPlayer.resume();

        //

    }

    public void drawSkeleton(final ArrayList<Point2D[]> keyPoints){
        //draw skeleton
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mBattleWorms.getState()==Constants.STATE_START)
                   mSkeleton.setPlaying(true);

                mSkeleton.drawSkeletons(keyPoints);

            }
        });
    }
    public void showData(final String data) {
        //draw skeleton
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSkeleton.drawSkeletons(Utils.stringToKeyPoints(data));
                mTcpDataView.setText(data);
                String userAngle = Utils.stringToDegree(data);
                mUserAngleView.setText(userAngle);
            }
        });
    }

    //Unity Utils
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


/*API12*/

 public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }


    @Override
    public void getJpegFrame(final byte[] frame) {
        //Log.d("#####","Mainactivity frame size:"+frame.length);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mSocket.isConnected()) {
                    mSocket.sendUdpPacket(frame);
                    //Log.d("#####","length:"+frame.length);

                }

                //mBitmapView.setImageBitmap(bit);
                Bitmap bit = BitmapFactory.decodeByteArray(frame, 0, frame.length);
                setUserProfile(bit);
            }
        });
    }

    public void setUserProfile(Bitmap wholePicture){
        ArrayList<UserInfo> users = mBattleWorms.getUserInfos();
        for(int i=0;i<users.size();i++){
            UserInfo user = users.get(i);
            if(user.getUserProfile()==null) {
                user.setUserProfile(Utils.getUserFace(wholePicture, user.getKeyPoints()[Constants.NOSE]));
                updateUser(i);
            }
        }
    }

    @Override
    public void infoHandler(final String msg) {
        showToast(msg);
    }

    public void showToast(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mToast!=null)
                    mToast.cancel();
                mToast = Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT);
                mToast.show();
            }
        });

    }

    public void updateUser(final int position){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    public void updateUser(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    //it will be called from unity when a worm eat food.
    //variable "1 27300" "ID SCORE"
    public void updateScore(String str){
        //Log.d("#####","updateScore!!!:"+str);

        StringTokenizer st = new StringTokenizer(str," ");
        int id = Integer.parseInt(st.nextToken());
        int score = Integer.parseInt(st.nextToken());

        int index = getItemIndex(id);
        if(index != -1){
            mBattleWorms.getUserInfos().get(index).setScore(score);
        }
        Collections.sort(mBattleWorms.getUserInfos());
        updateUser();

    }
    public void updateDie(String str){
        int id = Integer.parseInt(str);
        int index = getItemIndex(id);
        if(index != -1){
            mBattleWorms.getUserInfos().get(index).setPlaying(false);
            Log.d("#####","worms die:"+index);
        }
        updateUser();
    }

    public int getItemIndex(int id){;
        int index = -1;
        ArrayList<UserInfo> userInfos = mBattleWorms.getUserInfos();
        for(int i=0;i<userInfos.size();i++){
            if(userInfos.get(i).getUserNumber()==id){
                return i;
            }
        }
        return index;
    }



}
