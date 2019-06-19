package com.huabao.mytest.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huabao.mytest.R;
import com.huabao.mytest.bean.FreqBean;
import com.huabao.mytest.dialog.AirDialog;
import com.huabao.mytest.media.CommomBroadcastReceiver;
import com.huabao.mytest.media.Mount;
import com.huabao.mytest.utils.IpUtils;
import com.huabao.mytest.utils.WifiManagerUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WifiAPActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifiap);

        findViewById(R.id.hotspot_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AirDialog airDialog = new AirDialog(WifiAPActivity.this, R.style.DialogTheme);
                airDialog.show();
//                wifiAp1();
            }
        });

        findViewById(R.id.hotspot_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiAp2();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void wifiAp1(){
        if(WifiManagerUtil.isWifiApOpen(this)){
            String[] apInfo = WifiManagerUtil.getApInfo(this);
            Log.d("wifi_ap", "wiif ap is open ssid is " + apInfo[0] + ", password is " + apInfo[1]);
            WifiManagerUtil.openOrCloseAp(this, false);
        }else{
            Log.d("wifi_ap", "wiif ap is close");
            WifiManagerUtil.openOrCloseAp(this, true);
        }
    }

    private void wifiAp2(){
        if(WifiManagerUtil.isWifiApOpen(this)){
            String[] apInfo = WifiManagerUtil.getApInfo(this);
            Log.d("wifi_ap", "wiif ap is open ssid is " + apInfo[0] + ", password is " + apInfo[1]);
            WifiManagerUtil.openOrCloseAp(this, false);
        }else{
            Log.d("wifi_ap", "wiif ap is close");
            WifiManagerUtil.setWifiApEnabled(this, true);
        }
    }

}
