package csid.butterflyeffect;

/**
 * Created by hanseungbeom on 2018. 1. 15..
 */


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;


/**
 * Created by han sb on 2017-02-17.
 */

public class AppPermissions {
    public static final String[] APP_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    public static boolean hasAppPermission(Context context) {
        for (String permission : APP_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


}
