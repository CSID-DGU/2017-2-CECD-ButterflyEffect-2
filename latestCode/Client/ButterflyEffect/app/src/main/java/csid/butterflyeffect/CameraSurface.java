package csid.butterflyeffect;

/**
 * Created by hanseungbeom on 2018. 1. 15..
 */
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import csid.butterflyeffect.util.Constants;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraSurface:";
    public Camera camera;
    protected SurfaceHolder holder = getHolder();
    private boolean previewing = false;

    public CameraSurface(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        holder.setType(3);
        holder.addCallback(this);
        setKeepScreenOn(true);
    }

    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1,
                               int paramInt2, int paramInt3) {
        if (camera == null)
            return;
        if (previewing)
            camera.stopPreview();

        Camera.Parameters params = camera.getParameters();

        List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
        Camera.Size bestSize = sizeList.get((sizeList.size()-1)-sizeList.size()/7); // 9/10 index.

        //setting middle of available size.
        params.setPreviewSize(bestSize.width,bestSize.height);
        params.setPreviewFpsRange(Constants.FRAME_RATE,Constants.FRAME_RATE);

        camera.setParameters(params);
        camera.startPreview();
        previewing = true;
    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        try {


        //0 for CAMERA_FACING_BACK
        //1 for CAMERA_FACING_FRONT

            camera = Camera.open(CAMERA_FACING_FRONT);
            camera.setPreviewDisplay(paramSurfaceHolder);
            return;
        } catch (IOException localIOException) {
            Log.e("CameraSurface", "Error setting preview display.");
            camera.release();
            camera = null;
        }
    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        Log.d(TAG,"surfaceDestroyed..");
        if (camera != null) {
            if (previewing) {
                camera.stopPreview();
            }
            previewing = false;
            camera.release();
            camera = null;
        }
    }
    public void refreshFocus(){
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);
    }

}