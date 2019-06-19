package com.huabao.mytest.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.Iterator;

public class StateUtils {

    private Context mContext;

    public StateUtils(Context context){
        this.mContext = context;
    }

    /**
     *  静音模式
     */
    public boolean isMuteMode(){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT){   //静音
            return true;
        }
        return false;
    }

    /**
     * 判断定位服务是否开启
     *
     * @param
     * @return true 表示开启
     */
    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * 检测存储路径是否存在
     */
    private boolean isExistsStorage(String path) {
        File file = new File(path);
        boolean isError = false;
        if (file.exists()) {
            if (file.canRead()) {
                isError = false;
            } else {
                isError = true;
            }
        }
        return isError;
    }

    private boolean isWifiConnect() {
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * Android 7.0及以上版本
     */
    public void setWifiConnectListener(){
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI);
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mConnectivityManager.registerNetworkCallback(builder.build(), mNetworkCallback);
    }

    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback =  new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            isWifiConnect();
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            isWifiConnect();
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
        }

        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }
    };

    /**
     * 获取wifi信号强度
     *
     * @return
     */
    private int obtainWifiInfo() {
        // Wifi的连接速度及信号强度：
        int strength = 0;
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            // 链接信号强度，5为获取的信号强度值在5以内
            strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
            // 链接速度
            int speed = info.getLinkSpeed();
            // 链接速度单位
            String units = WifiInfo.LINK_SPEED_UNITS;
            // Wifi源名称
            String ssid = info.getSSID();
        }
        return strength;
    }

    /**
     * 信号强度监听
     */
    public void getCurrentNetDBM() {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener mylistener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);

                int level = signalStrength.getLevel();

//                String signalInfo = signalStrength.toString();
//                String[] params = signalInfo.split(" ");
//                int Itedbm = Integer.parseInt(params[9]);
//
//                int asu = signalStrength.getGsmSignalStrength();
//                int dbm = -113 + 2 * asu;
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
//                    Log.d(TAG, "LTE:" + Itedbm + "dBm,Detail:" +signalInfo );
//                }else if(tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA ||
//                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA ||
//                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ||
//                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS){
//                    Log.d(TAG, "MCDMA:" + dbm + "dBm,Detail:" +signalInfo );
//                }else {
//                    Log.d(TAG, "GSM:" + dbm + "dBm,Detail:" + signalInfo);
//                }
            }
        };
        //开始监听
        tm.listen(mylistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    /**
     * 网络切换监听
     */
    public class NetChangeReceiver extends BroadcastReceiver {

        private static final String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)){
                Toast.makeText(context, "Net Changed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}