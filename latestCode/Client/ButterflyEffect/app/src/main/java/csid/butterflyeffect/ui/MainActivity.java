package csid.butterflyeffect.ui;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import csid.butterflyeffect.FirebaseTasks;
import csid.butterflyeffect.PreviewSurface;
import csid.butterflyeffect.R;
import csid.butterflyeffect.game.BattleWorms;
import csid.butterflyeffect.game.Point2D;
import csid.butterflyeffect.game.UnityConnector;
import csid.butterflyeffect.game.model.Famer;
import csid.butterflyeffect.game.model.KeyPoint;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.network.HandleSocketError;
import csid.butterflyeffect.network.SocketClient;
import csid.butterflyeffect.ui.adapter.FamerAdapter;
import csid.butterflyeffect.ui.adapter.UserAdapter;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements PreviewSurface.FrameHandler, HandleSocketError {
    private PreviewSurface mPriviewSurface;

    private Button mBtn;
    private FrameLayout mUnityView;
    private ImageView mPhotoZoneView;
    private CircleImageView mWinnerView;
    private LinearLayout mWinnerScoreLayout;
    private FrameLayout mPhotoZoneLayout;
    //private TextView mWinnerScore;
    private TextView mTcpDataView, mUserAngleView;
    private SocketClient mSocket;
    private UnityPlayer mUnityPlayer;
    private SkeletonView mSkeleton;
    private FrameLayout mPreview;
    private BattleWorms mBattleWorms;
    private RecyclerView mGamerRv, mFamerRv;
    private UserAdapter mGamerAdapter;
    private FamerAdapter mFamerAdapter;
    private Toast mToast;
    public static boolean isSocketConnected = false;

    private DatabaseReference mReference;
    private ArrayList<Famer> mFamers;
    private TimerTask mFamerTimer;
    private int timerPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mUnityView = (FrameLayout) findViewById(R.id.fl_unityView);

        mBtn = (Button) findViewById(R.id.btn_capture);
        mPhotoZoneView = (ImageView) findViewById(R.id.iv_photozone_view);
        mWinnerView = (CircleImageView) findViewById(R.id.iv_winner);
        mWinnerScoreLayout = (LinearLayout) findViewById(R.id.ll_winner);
        mPhotoZoneLayout = (FrameLayout) findViewById(R.id.fl_victory_photo_zone);
        //mWinnerScore = (TextView) findViewById(R.id.tv_winner_score);

        mTcpDataView = (TextView) findViewById(R.id.tv_tcp);
        mUserAngleView = (TextView) findViewById(R.id.tv_angle);
        mSkeleton = (SkeletonView) findViewById(R.id.skeleton_view);
        mPreview = (FrameLayout) findViewById(R.id.fr_preview);

        mGamerRv = (RecyclerView) findViewById(R.id.rv_user);
        mFamerRv = (RecyclerView) findViewById(R.id.rv_fame);

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
                Constants.PREVIEW_WIDTH = mPreview.getMeasuredWidth();
                Constants.PREVIEW_HEIGHT = mPreview.getMeasuredHeight();

            }
        });

        //BattleWorms 초기화
        mBattleWorms = new BattleWorms(this);

        //setting recyclerView
        mGamerRv.setHasFixedSize(true);
        mGamerRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mGamerAdapter = new UserAdapter(this, mBattleWorms.getUserInfos());
        mGamerRv.setAdapter(mGamerAdapter);

        mFamers = new ArrayList<>();
        mFamerRv.setHasFixedSize(true);
        mFamerRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mFamerAdapter = new FamerAdapter(this, mFamers);
        mFamerRv.setAdapter(mFamerAdapter);

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

        FrameLayout layout = (FrameLayout) findViewById(R.id.fl_unityView);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layout.addView(mUnityPlayer.getView(), 0, lp);
        //mUnityPlayer.windowFocusChanged(true);
        mUnityPlayer.resume();

        //setting firebase
        setFirebase();
        setRotateFamer();
    }
    public void initRestartSetting(){
        mPhotoZoneView.setImageResource(R.drawable.image);
        mWinnerView.setImageResource(R.drawable.ic_user);

        //BattleWorms
        mBattleWorms.init();
        mGamerAdapter.swapData(mBattleWorms.getUserInfos());

        // TCP & UDP callback setting
        mSocket.setErrorCallback(this);
        mSocket.setReceiveCallback(mBattleWorms);
    }

    public void setFirebase(){
        mReference = FirebaseTasks.getDatabaseInstance().getReference(getString(R.string.table_famer));
        mReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("#####", "onChildAdded!");
                Famer famer = dataSnapshot.getValue(Famer.class);
                mFamers.add(famer);
                mFamerAdapter.orderData();
                mFamerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("#####", "onChildChanged!");
                Famer famer = dataSnapshot.getValue(Famer.class);
                int index = getFamerIndex(famer.getPhoneNumber());
                if (index != Constants.NOT_FOUND) {
                    mFamers.get(index).setScore(famer.getScore());
                    mFamers.get(index).setUpdatedTime(famer.getUpdatedTime());
                    mFamers.get(index).setImageUrl(famer.getImageUrl());
                    mFamerAdapter.orderData();
                    mFamerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void drawSkeleton(final ArrayList<KeyPoint> keyPoints) {
        //draw skeleton
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBattleWorms.getState() == Constants.STATE_START)
                    mSkeleton.setPlaying(true);

                mSkeleton.drawSkeletons(keyPoints);

            }
        });
    }

    //Unity Utils
    // Quit Unity
    @Override
    protected void onDestroy() {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }


    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }


/*API12*/

    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }


    @Override
    public void getJpegFrame(final byte[] frame) {
        //Log.d("#####","Mainactivity frame size:"+frame.length);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSocket.isConnected()) {
                    mSocket.sendUdpPacket(frame);
                    //Log.d("#####","length:"+frame.length);

                }

                //mBitmapView.setImageBitmap(bit);
                Bitmap bit = BitmapFactory.decodeByteArray(frame, 0, frame.length);
                setUserProfile(bit);
                if (mBattleWorms.getState() == Constants.STATE_END) {
                    setWinnerCrop(bit);
                }

            }
        });
    }

    public void setUserProfile(Bitmap wholePicture) {
        ArrayList<UserInfo> users = mBattleWorms.getUserInfos();
        for (int i = 0; i < users.size(); i++) {
            UserInfo user = users.get(i);
            if (user.getUserProfile() == null) {
                user.setUserProfile(Utils.getUserFace(wholePicture, user.getKeyPoint().getSkeleton()[Constants.NOSE]));
                updateUser(i);
            }
        }
    }

    @Override
    public void infoHandler(final String msg) {
        showToast(msg);
    }

    public void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });

    }

    public void updateUser(final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGamerAdapter.notifyItemChanged(position);
            }
        });
    }

    public void updateUser() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGamerAdapter.notifyDataSetChanged();
            }
        });
    }


    //it will be called from unity when a worm eat food.
    //variable "1 27300" "ID SCORE"
    public void updateScore(String str) {
        //Log.d("#####","updateScore!!!:"+str);

        StringTokenizer st = new StringTokenizer(str, " ");
        int id = Integer.parseInt(st.nextToken());
        int score = Integer.parseInt(st.nextToken());

        int index = getItemIndex(id);
        if (index != -1) {
            mBattleWorms.getUserInfos().get(index).setScore(score);
        }
        Collections.sort(mBattleWorms.getUserInfos());
        updateUser();

    }

    public void updateDie(String str) {
        int id = Integer.parseInt(str);
        int index = getItemIndex(id);
        if (index != -1) {
            UserInfo user = mBattleWorms.getUserInfos().get(index);
            //user.setPlaying(false);
            user.setScore(0);
            Log.d("#####", "worms die:" + index);
        }
        updateUser();
    }

    //called when game time ended.
    public void timeOut(String str) {
        Log.d("#####", "timeOut call!!!");
        /* when game is ended , celebrating logic run */
        mBattleWorms.setState(Constants.STATE_END);
        final UserInfo winner = mBattleWorms.getWinner();

        // photo zone
        showPhotoZone();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < Constants.WAITING_TIME; i++) {
                        showToast((Constants.WAITING_TIME-i) + "초 뒤 사진이 촬영됩니다.");
                        Thread.sleep(1000);
                    }

                    Bitmap picture = takePicture();
                    setWinnerView(winner.getScore(), picture);

                    for (int i = 1; i <= Constants.WAITING_TIME; i++) {
                        Thread.sleep(1000);
                    }
                    uploadFamer(winner.getScore(),picture);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initRestartSetting();
                            UnityConnector.restartGame();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    public int getItemIndex(int id) {
        int index = -1;
        ArrayList<UserInfo> userInfos = mBattleWorms.getUserInfos();
        for (int i = 0; i < userInfos.size(); i++) {
            if (userInfos.get(i).getUserNumber() == id) {
                return i;
            }
        }
        return index;
    }


    public int getFamerIndex(String famerPhone) {
        int index = Constants.NOT_FOUND;
        for (int i = 0; i < mFamers.size(); i++) {
            if (mFamers.get(i).getPhoneNumber().equals(famerPhone)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void showPhotoZone() {
        //mUnityView.setVisibility(View.INVISIBLE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhotoZoneLayout.setVisibility(View.VISIBLE);
                mWinnerScoreLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void showWinnerView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhotoZoneLayout.setVisibility(View.INVISIBLE);
                mWinnerScoreLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hidePhotoZoneAndWinnerView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhotoZoneLayout.setVisibility(View.INVISIBLE);
                mWinnerScoreLayout.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void setWinnerView(final int score, final Bitmap bitmap) {
        showWinnerView();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWinnerView.setImageBitmap(bitmap);
                //mWinnerScore.setText(String.valueOf(score));
            }
        });
    }

    public Bitmap takePicture() {
        mPhotoZoneView.buildDrawingCache();
        return mPhotoZoneView.getDrawingCache();
    }


    public void uploadFamer(final int score, final Bitmap bitmap) {
        hidePhotoZoneAndWinnerView();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("1등을 축하드립니다! ("+score+"점)");
                builder.setMessage(getString(R.string.msg_request_phone_number));
                // Set up the input
                final EditText input = new EditText(MainActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                builder.setIcon(drawable);

                // Set up the buttons
                builder.setPositiveButton(getString(R.string.msg_btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumber = input.getText().toString();
                        FirebaseTasks.registerFamer(MainActivity.this,phoneNumber,score,bitmap);
                    }
                });

                builder.show();
            }
        });
    }

    public void setWinnerCrop(Bitmap bitmap) {
        Point2D[] winnerSkeleton = mBattleWorms.getWinner().getKeyPoint().getSkeleton();
        Bitmap croppedWinner = Utils.getUserRectangle(bitmap, winnerSkeleton);
        mPhotoZoneView.setImageBitmap(croppedWinner);
    }

    //TODO auto recycler scroll thread
    public void setRotateFamer() {
        timerPos = 0;

        mFamerTimer = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mFamers.size()==0)
                            return;

                        int nextPosition = (timerPos++)%mFamers.size();
                        if(nextPosition ==0 ) mFamerRv.scrollToPosition(nextPosition);
                        else mFamerRv.smoothScrollToPosition(nextPosition);
                    }
                });

            }
        };

        new Timer().schedule(mFamerTimer,3000,3000);
    }

}
