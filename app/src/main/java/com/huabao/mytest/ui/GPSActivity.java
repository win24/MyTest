package com.huabao.mytest.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.huabao.mytest.R;
import com.huabao.mytest.utils.GpsSatelliteUtil;


public class GPSActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gps);

        getGpsState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGpsSatelliteUtil.destroy();
        mGpsSatelliteUtil = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 200) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "请在设置中打开权限后继续", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 200);
                }
            }else if (requestCode == 201) {
                mGpsSatelliteUtil.openGPSSettings();
            }
        }
    }

    private GpsSatelliteUtil mGpsSatelliteUtil;
    private void getGpsState(){
        mGpsSatelliteUtil = new GpsSatelliteUtil(this);
        mGpsSatelliteUtil.setGpsSatelliteListener(new GpsSatelliteUtil.GpsSatelliteListener() {
            @Override
            public void onGpsSatelliteListener(boolean isHasSatellite) {
                Log.d("MyTest", "[GPSActivity] onGpsSatelliteListener( " + isHasSatellite + " )");
            }
        });
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS) != PackageManager.PERMISSION_GRANTED) {
            checkGPSPermission();
        }else{
            mGpsSatelliteUtil.openGPSSettings();
        }
    }

    private void checkGPSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};
            for(int i=0; i<permissions.length; i++){
                int result = checkSelfPermission(permissions[i]);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 201);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1120){
            mGpsSatelliteUtil.getLocation();
//            mGpsSatelliteUtil.getSatellites();
        }
    }

}
