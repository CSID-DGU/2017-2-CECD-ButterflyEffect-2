package csid.butterflyeffect.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

import csid.butterflyeffect.util.Constants;

/**
 * Created by sy081 on 2018-04-11.
 */

public class VideoExtractor {
    private File videoFile;
    private Uri videoFileUri;
    private MediaMetadataRetriever retriever;
    private MediaPlayer mediaPlayer;
    private Bitmap bitmap;
    private Thread extractor;
    FrameCallback frameCallback;
    final private int FPS = 10;
    final private int MICROSECOND = 1000000;
    long totalMilliseconds;

    public interface FrameCallback{
        void getVideoFrame(final byte[] bytes,int now, int total);
    }

    public void setFrameCallback(FrameCallback frameCallback){
        this.frameCallback = frameCallback;
    }

    public VideoExtractor(Context context, Uri videoFileUri){
        try {
            this.videoFileUri = videoFileUri;
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context,videoFileUri);
            mediaPlayer = MediaPlayer.create(context, videoFileUri);
            totalMilliseconds = mediaPlayer.getDuration();
        }catch(IllegalArgumentException e) {
            e.printStackTrace();
            Log.d("DEBUG", "Can't load the video file");
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    public void extract(){
        extractor = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int totalFrame = (int)((double)totalMilliseconds*1000 - MICROSECOND)/(MICROSECOND/FPS);
                    int i=1;

                    for(long start = MICROSECOND; start<totalMilliseconds*1000 ;start += MICROSECOND/FPS){
                        bitmap = retriever.getFrameAtTime(start, MediaMetadataRetriever.OPTION_CLOSEST);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);

                        Constants.CAMERA_WIDTH = bitmap.getWidth();
                        Constants.CAMERA_HEIGHT = bitmap.getHeight();
                        outputStream.flush();
                        frameCallback.getVideoFrame(outputStream.toByteArray(),(int)i,totalFrame);
                        i++;
                        Thread.sleep(1000);

                    }
                    /*for (long microseconds = MICROSECOND; microseconds < totalMilliseconds * 1000; microseconds += MICROSECOND / FPS) {
                        bitmap = retriever.getFrameAtTime(microseconds, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);

                        Constants.CAMERA_WIDTH = bitmap.getWidth();
                        Constants.CAMERA_HEIGHT = bitmap.getHeight();
                        outputStream.flush();
                        frameCallback.getVideoFrame(outputStream.toByteArray(),(int)i,totalFrame);
                        i++;
                    }*/
                }catch (Exception e){
                    e.printStackTrace();
                }
                retriever.release();
            }
        });
        extractor.start();
    }
}
