package csid.butterflyeffect;

/**
 * Created by hanseungbeom on 2018. 1. 15..
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import csid.butterflyeffect.ui.MainActivity;
import csid.butterflyeffect.util.Constants;


public class PreviewSurface extends CameraSurface implements
        Camera.PreviewCallback {

    private FrameHandler mFrameHandler;
    public interface FrameHandler{
        void getBitmap(Bitmap bitmap);
    }


    private static final String TAG = "PreviewSurface:";

    private String ipname = "192.168.0.2";

    public void setFrameHandler(FrameHandler frameHandler){
        this.mFrameHandler = frameHandler;
    }
    public PreviewSurface(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera) {
        Size size = paramCamera.getParameters().getPreviewSize();
        // use "image.compressToJpeg()" to change image data format from "YUV" to "jpg"
        YuvImage image = new YuvImage(paramArrayOfByte, ImageFormat.NV21,
                size.width, size.height, null);
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        try {
            if (image != null) {

                if(MainActivity.isOpenCvLoaded) {
                    Mat mRgba = new Mat();
                    Mat frame = new Mat(size.width + size.height / 2, size.width, CvType.CV_8UC1);
                    frame.put(0, 0, paramArrayOfByte);
                    Imgproc.cvtColor(frame, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
                    Bitmap bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(frame, bitmap);
                    mFrameHandler.getBitmap(bitmap);
                }
               // image.compressToJpeg(new Rect(0, 0, size.width, size.height),80, outstream);
                image.compressToJpeg(new Rect(0, 0, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT),60, outstream);
                outstream.flush();
                //start thread to send image data
                //MainActivity.mSocket.sendUdpPacket(outstream);
                //Thread th = new SendDataThread(outstream, Constants.ADDR, Constants.PORT_NUM);

                //th.start(); //TODO 전송할때 여기서 하기
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error:" + ex.getMessage());
        }

    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        super.surfaceCreated(paramSurfaceHolder);
        //TODO 이거 임시방편임 각도에 맞게 돌아가게 해야하는데 일단 90도 임시로 돌려놓음.
        this.camera.setDisplayOrientation(90);
        //this.camera.autoFocus();
        this.camera.setPreviewCallback(this);

    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        this.camera.setPreviewCallback(null);
        super.surfaceDestroyed(paramSurfaceHolder);
    }

    public void setIP(String ipname) {
        this.ipname = ipname;
    }



}