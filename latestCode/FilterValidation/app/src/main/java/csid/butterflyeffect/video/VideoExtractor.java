package csid.butterflyeffect.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

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
        void getVideoFrame(final byte[] bytes);
    }

    public void setFrameCallback(FrameCallback frameCallback){
        this.frameCallback = frameCallback;
    }

    public VideoExtractor(Context context, String videoPath){
        try {
            videoFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + videoPath);
            videoFileUri = Uri.parse(videoFile.toString());
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoFile.toString());
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
                    for (long microseconds = MICROSECOND; microseconds < totalMilliseconds * 1000; microseconds += MICROSECOND / FPS) {
                        bitmap = retriever.getFrameAtTime(microseconds, MediaMetadataRetriever.OPTION_CLOSEST);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                        outputStream.flush();
                        frameCallback.getVideoFrame(outputStream.toByteArray());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                retriever.release();
            }
        });
        extractor.start();
    }
}
