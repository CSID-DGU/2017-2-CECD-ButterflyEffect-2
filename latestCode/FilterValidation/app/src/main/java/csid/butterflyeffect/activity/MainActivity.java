package csid.butterflyeffect.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.InterruptedIOException;
import java.util.ArrayList;

import csid.butterflyeffect.R;
import csid.butterflyeffect.game.theme.BattleWorms;
import csid.butterflyeffect.game.model.KeyPoint;
import csid.butterflyeffect.network.HandleSocketError;
import csid.butterflyeffect.network.SocketClient;
import csid.butterflyeffect.util.Constants;
import csid.butterflyeffect.util.Utils;
import csid.butterflyeffect.video.VideoExtractor;
import csid.butterflyeffect.view.SkeletonView;

public class MainActivity extends AppCompatActivity implements VideoExtractor.FrameCallback, HandleSocketError,Player.EventListener {
    private FrameLayout mPriviewSurface;
    private SimpleExoPlayer mPlayer;
    private boolean durationSet;


    private final int REQUEST_TAKE_GALLERY_VIDEO = 1;


    private TextView mTestview;
    private Button mLoadVideoBtn, mSendFrameBtn;
    private TextureView mTextureView;
    private TextView mTcpDataView, mUserAngleView;
    private SocketClient mSocket;
    private SkeletonView mSkeleton;
    private FrameLayout mPreview;
    private BattleWorms mBattleWorms;
    private Toast mToast;
    private Bitmap packet;

    private boolean isReady;
    public static boolean isSocketConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mTestview = (TextView) findViewById(R.id.tv_test);
        mLoadVideoBtn = (Button) findViewById(R.id.btn_load);
        mLoadVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
            }
        });

        mSendFrameBtn = (Button) findViewById(R.id.btn_send);
        mSendFrameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReady = false;
            }
        });

        mTextureView = (TextureView) findViewById(R.id.texture_view);

        mSkeleton = (SkeletonView) findViewById(R.id.skeleton_view);
        mPreview = (FrameLayout) findViewById(R.id.fr_preview);

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
                Constants.PREVIEW_WIDTH = mPreview.getMeasuredWidth();
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

        settingExoPlayer();

    }

    public void settingExoPlayer() {

        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the mPlayer
        mPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        // Bind the mPlayer to the view.
        mPlayer.setVideoTextureView(mTextureView);
        mPlayer.addListener(this);
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

    @Override
    public void getVideoFrame(final byte[] bytes, final int now, final int total) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSocket.isConnected()) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Matrix matrix = new Matrix();
                    matrix.preScale(-1.0f, 1.0f);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    //mTextureView.setImageBitmap(bitmap);

                }
            }
        });
        Log.d("#####", now + "/" + total);
        //while(stopSend){};
        mSocket.sendUdpPacket(bytes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri selectedVideoUri = data.getData();

                if (selectedVideoUri != null) {
                    DefaultBandwidthMeter bandwidthMeter1 = new DefaultBandwidthMeter();
                    // Produces DataSource instances through which media data is loaded.
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                            Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter1);
                    // This is the MediaSource representing the media to be played.
                    MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(selectedVideoUri);

                    // Prepare the mPlayer with the source.

                    durationSet = false;
                    mPlayer.prepare(mediaSource);
                    //mPlayer.setPlayWhenReady(true);
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }

    public void startExtract(final int extractFPS) {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final int CAMERA_FPS = 30;
                int frameIncreseRate = CAMERA_FPS / extractFPS;

                for (int seekPos = 0; seekPos < mPlayer.getDuration(); seekPos += frameIncreseRate * 50) {

                    isReady = true;
                    while (isReady) ;

                    mPlayer.seekTo(seekPos);
                }


            }
        });
        t.start();


    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_READY && !durationSet) {
            long realDurationMillis = mPlayer.getDuration();
            durationSet = true;
            isReady = true;
            startExtract(10);
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

        packet = mTextureView.getBitmap();
        if(packet!=null) {
            Constants.CAMERA_WIDTH = packet.getWidth();
            Constants.CAMERA_HEIGHT = packet.getHeight();

            if (mSocket.isConnected()) {
                mSocket.sendUdpPacket(Utils.bitmapToByteArray(packet));
            }
        }
    }


}
