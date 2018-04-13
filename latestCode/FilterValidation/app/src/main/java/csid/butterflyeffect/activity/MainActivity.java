package csid.butterflyeffect.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import csid.butterflyeffect.camera.PreviewSurface;
import csid.butterflyeffect.R;
import csid.butterflyeffect.game.theme.BattleWorms;
import csid.butterflyeffect.game.model.KeyPoint;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.network.HandleSocketError;
import csid.butterflyeffect.network.SocketClient;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;
import csid.butterflyeffect.video.VideoExtractor;
import csid.butterflyeffect.view.SkeletonView;

public class MainActivity extends AppCompatActivity implements VideoExtractor.FrameCallback, HandleSocketError {
    private FrameLayout mPriviewSurface;

    private Button mBtn;
    private ImageView mBitmapView;
    private TextView mTcpDataView,mUserAngleView;
    private SocketClient mSocket;
    private SkeletonView mSkeleton;
    private FrameLayout mPreview;
    private BattleWorms mBattleWorms;
    private Toast mToast;
    private VideoExtractor extractor;
    public static boolean isSocketConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mBtn = (Button)findViewById(R.id.btn_capture);
        mBitmapView = (ImageView) findViewById(R.id.image_view);
        mTcpDataView = (TextView)findViewById(R.id.tv_tcp);
        mUserAngleView = (TextView)findViewById(R.id.tv_angle);
        mSkeleton = (SkeletonView)findViewById(R.id.skeleton_view);
        mPreview = (FrameLayout)findViewById(R.id.fr_preview);

        getWindow().setFormat(PixelFormat.UNKNOWN);

        mPriviewSurface = (FrameLayout) findViewById(R.id.sv);

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

        // TCP & UDP 연결
        mSocket = SocketClient.getInstance();
        mSocket.setErrorCallback(this);
        mSocket.setReceiveCallback(mBattleWorms);
        mSocket.startTcpService();

        //init frame extractor
        extractor = new VideoExtractor(getBaseContext(), "/Android_Studio/hd_00_01.mp4");
        extractor.setFrameCallback(this);
        extractor.extract();
    }

    public void drawSkeleton(final ArrayList<KeyPoint> keyPoints){
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

    public void setUserProfile(Bitmap wholePicture){
        ArrayList<UserInfo> users = mBattleWorms.getUserInfos();
        for(int i=0;i<users.size();i++){
            UserInfo user = users.get(i);
            if(user.getUserProfile()==null) {
                user.setUserProfile(Utils.getUserFace(wholePicture, user.getKeyPoint().getSkeleton()[Constants.NOSE]));
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

    @Override
    public void getVideoFrame(final byte[] bytes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mSocket.isConnected()){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                    Matrix matrix = new Matrix();
                    matrix.preScale(-1.0f, 1.0f);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    mBitmapView.setImageBitmap(bitmap);

                    mSocket.sendUdpPacket(bytes);
                }
            }
        });
    }
}
