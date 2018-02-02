package csid.butterflyeffect.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import csid.butterflyeffect.AppPermissions;
import csid.butterflyeffect.R;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        if(AppPermissions.hasAppPermission(this)){
            Intent i = new Intent(PermissionActivity.this, IpPortActivity.class);
            startActivity(i);
            finish();

        }
        else{
            setUpTedPermission();
        }

    }


    private void setUpTedPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(PermissionActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PermissionActivity.this, IpPortActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(PermissionActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(AppPermissions.APP_PERMISSION)
                .check();
    }
}
