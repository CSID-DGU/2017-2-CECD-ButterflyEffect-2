package csid.butterflyeffect;

/**
 * Created by hanseungbeom on 2018. 1. 15..
 */

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

import csid.butterflyeffect.network.HandleReceiveData;
import csid.butterflyeffect.ui.MainActivity;
import csid.butterflyeffect.util.Constants;

public class PreviewSurface extends CameraSurface implements
        Camera.PreviewCallback {

    private FrameHandler mFrameHandler;

    public interface FrameHandler {
        void getJpegFrame(byte[] frame);
    }

    private static final String TAG = "PreviewSurface:";

    public void setFrameHandler(FrameHandler frameHandler) {
        this.mFrameHandler = frameHandler;
    }

    public PreviewSurface(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);

    }

    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera) {
        //Log.d("#####", "size:" + paramArrayOfByte.length);
        try {
            Size size = paramCamera.getParameters().getPreviewSize();
            // use "image.compressToJpeg()" to change image data format from "YUV" to "jpg"
            //Bitmap bitmap = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length);
            //bitmap = bitmap.createScaledBitmap(bitmap, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT, false);
            Constants.CAMERA_WIDTH = size.width;
            Constants.CAMERA_HEIGHT = size.height;

            YuvImage image = new YuvImage(paramArrayOfByte, ImageFormat.NV21, size.width, size.height, null);

            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 60, outstream);
            outstream.flush();
            mFrameHandler.getJpegFrame(outstream.toByteArray());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        super.surfaceCreated(paramSurfaceHolder);
        //TODO 이거 임시방편임 각도에 맞게 돌아가게 해야하는데 일단 90도 임시로 돌려놓음.
        //this.camera.setDisplayOrientation(90);
        //this.camera.autoFocus();
        this.camera.setPreviewCallback(this);
    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        this.camera.setPreviewCallback(null);
        super.surfaceDestroyed(paramSurfaceHolder);
    }
    public FrameHandler getFrameHandler(){
        return mFrameHandler;
    }
}