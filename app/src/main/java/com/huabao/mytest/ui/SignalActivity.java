package com.huabao.mytest.ui;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.huabao.mytest.R;

import java.lang.reflect.Method;


public class SignalActivity extends AppCompatActivity {

    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;
    private TextView mSignalTextView;
    private boolean isSimInstalled = false;
    private SimReceiver mSimReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);

        mSignalTextView = findViewById(R.id.signal_textview);

        isSimInstalled = isSimInstalled(this);
        mSimReceiver = new SimReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        registerReceiver(mSimReceiver, intentFilter);
        setSignalStrengthsListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(mSimReceiver);
    }

    /**
     * 信号强度监听
     */
    public void setSignalStrengthsListener() {
        Log.d("SignalActivity", "setSignalStrengthsListener()");
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);

                String info = "Sim卡 : "  + (isSimInstalled ? "已安装" : "未安装");

                info += "\n\n信号格 ： ";

                if(signalStrength != null) {
                    info += signalStrength.getLevel();
                    try {
                        info  = info +  "\n\n移动数据 ： " + (isMobile(SignalActivity.this) ? "已经打开" : "不可用");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

//                    int asu = signalStrength.getGsmSignalStrength();
//                    int dbm = -113 + 2 * asu;
//                    int cdmadbm = signalStrength.getCdmaDbm();
//                    int edvodbm = signalStrength.getEvdoDbm();

//                    info += ("\n\nasu :  " + asu);
//                    info += ("\n\ndbm :  " + dbm);
//                    info += ("\n\ncdmadbm :  " + cdmadbm);
//                    info += ("\n\nedvodbm :  " + edvodbm);

                    final int INVALID = 0x7FFFFFFF;
                    int dbm = INVALID;
                    int asuLevel = INVALID;

                    try {
                        Class<SignalStrength> clz = SignalStrength.class;
                        Method getDbmMethod = clz.getDeclaredMethod("getDbm");
                        dbm = (Integer) getDbmMethod.invoke(signalStrength);

                        Method getAsuLevelMethod = clz.getDeclaredMethod("getAsuLevel");
                        asuLevel = (int) getAsuLevelMethod.invoke(signalStrength);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    info += ("\n\ndbm :  " + dbm);
                    info += ("\n\nasu :  " + asuLevel);
                }

                mSignalTextView.setText(info);

                Log.d("SignalActivity", info);

//                String signalInfo = signalStrength.toString();
//                String[] params = signalInfo.split(" ");
//                int Itedbm = Integer.parseInt(params[9]);
//                if(tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
//                    try {
//                        Method method1 = null;
//                        method1 = signalStrength.getClass().getMethod("getDbm");
//                        dbm = (int) method1.invoke(signalStrength);
//                        Method method2 = signalStrength.getClass().getMethod("getLteLevel");
//                        int level = (int) method2.invoke(signalStrength);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    Log.d("HBLauncher2", "[HomeFragment] LTE:" + Itedbm + "dBm,Detail:" +signalInfo );
//                }else if(tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA ||
//                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA ||
//                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ||
//                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS){
//                    Log.d("HBLauncher2", "[HomeFragment] MCDMA:" + dbm + "dBm,Detail:" + signalInfo );
//                }else {
//                    Log.d("HBLauncher2", "[HomeFragment] GSM:" + dbm + "dBm,Detail:" + signalInfo);
//                }
            }
        };
        //开始监听
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    //判断移动数据是否打开
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    public class SimReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())){
                setSimInfoByChange(context, intent);
            }
        }

        public void setSimInfoByChange(Context context, Intent intent) {
            isSimInstalled(context);
        }

    }

    public boolean isSimInstalled(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        int state = tm.getSimState();
        switch (state) {
            case TelephonyManager.SIM_STATE_READY:
                Log.i("SignalActivity", "SIM_STATE_READY");
                isSimInstalled = true;
                return true;
            case TelephonyManager.SIM_STATE_UNKNOWN:
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            default:
                Log.i("SignalActivity", "SIM_STATE_NOT_READY");
                isSimInstalled = false;
                return false;
        }
    }

}
