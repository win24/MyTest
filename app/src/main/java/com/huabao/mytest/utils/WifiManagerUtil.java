package com.huabao.mytest.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.huabao.mytest.bean.WifiInfo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Looper.getMainLooper;

public class WifiManagerUtil {

    public List<WifiInfo> Read() throws Exception {
        List<WifiInfo> wifiInfos=new ArrayList<WifiInfo>();

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                throw e;
            }
        }


        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConf.toString() );
        while (networkMatcher.find() ) {
            String networkBlock = networkMatcher.group();
            Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssid.matcher(networkBlock);

            if (ssidMatcher.find() ) {
                WifiInfo wifiInfo=new WifiInfo();
                wifiInfo.Ssid=ssidMatcher.group(1);
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (pskMatcher.find() ) {
                    wifiInfo.Password=pskMatcher.group(1);
                } else {
                    wifiInfo.Password="无密码";
                }
                wifiInfos.add(wifiInfo);
            }

        }

        return wifiInfos;
    }

    public static boolean isWifiApOpen(Context context) {
        try {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //通过放射获取 getWifiApState()方法
            Method method = manager.getClass().getDeclaredMethod("getWifiApState");
            //调用getWifiApState() ，获取返回值
            int state = (int) method.invoke(manager);
            //通过放射获取 WIFI_AP的开启状态属性
            Field field = manager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
            //获取属性值
            int value = (int) field.get(manager);
            //判断是否开启
            if (state == value) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String[] getApInfo(Context context){
        String[] info = new String[2];
        try {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //拿到getWifiApConfiguration()方法
            Method method = manager.getClass().getDeclaredMethod("getWifiApConfiguration");
            //调用getWifiApConfiguration()方法，获取到 热点的WifiConfiguration
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(manager);
            info[0] = configuration.SSID;
            info[1] = configuration.preSharedKey;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static void closeWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) { //如果wifi打开关闭wifi
            wifiManager.setWifiEnabled(false);
        }
    }

    public static void openOrCloseAp(Context context, boolean openOrClose){
        //先关闭wifi
        closeWifi(context);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1){
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Field iConnMgrField = null;
            try {
                iConnMgrField = connManager.getClass().getDeclaredField("mService");
                iConnMgrField.setAccessible(true);
                Object iConnMgr = iConnMgrField.get(connManager);
                Class<?> iConnMgrClass = Class.forName(iConnMgr.getClass().getName());
                if (openOrClose) { //开启
                    Method startTethering = iConnMgrClass.getMethod("startTethering", int.class
                            , ResultReceiver.class,   boolean.class);
                    startTethering.invoke(iConnMgr, 0, null, true);
                }else { //关闭
                    Method startTethering = iConnMgrClass.getMethod("stopTethering", int.class);
                    startTethering.invoke(iConnMgr, 0);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            try {
                String[] apInfo = WifiManagerUtil.getApInfo(context);
                WifiConfiguration apConfig = new WifiConfiguration();
//                //配置热点的名称
//                apConfig.SSID = "HB-Test1";
//                //配置热点的密码(至少8位)
//                apConfig.preSharedKey = "12345678";
                //配置热点的名称
                apConfig.SSID = apInfo[0];
                //配置热点的密码(至少8位)
                apConfig.preSharedKey = apInfo[1];
//                apConfig.allowedKeyManagement.set(4);
                //通过反射调用设置热点
                Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
                Boolean rs = (Boolean) method.invoke(wifiManager, apConfig, openOrClose);//true开启热点 false关闭热点
                Log.d("wifi_ap", (openOrClose ? "开启" : "关闭") + "是否成功:" + rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean setWifiApEnabled(Context context, boolean enabled) {
        Log.d("wifi_ap", "setWifiApEnabled()");
        //先关闭wifi
        closeWifi(context);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Handler handler = new Handler(getMainLooper());
        try {
            String[] apInfo = WifiManagerUtil.getApInfo(context);
//            mResultReceiver = new ResultReceiver(handler);
            WifiConfiguration apConfig = new WifiConfiguration();
            /*apConfig.SSID = "HB-Test1";
            //配置热点的密码(至少8位)
            apConfig.preSharedKey = "12345678";*/
            apConfig.SSID = apInfo[0];
            //配置热点的密码(至少8位)
            apConfig.preSharedKey = apInfo[1];
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                Method mMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
                mMethod.invoke(wifiManager, apConfig);

                ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                Field mField = connectivityManager.getClass().getDeclaredField("TETHERING_WIFI");
                mField.setAccessible(true);
                int mTETHERING_WIFI = (int)mField.get(connectivityManager);
                Log.v("mTETHERING_WIFI:",String.valueOf(mTETHERING_WIFI));

                Field iConnMgrField = connectivityManager.getClass().getDeclaredField("mService");
                iConnMgrField.setAccessible(true);
                Object iConnMgr = iConnMgrField.get(connectivityManager);
                Class<?> iConnMgrClass = Class.forName(iConnMgr.getClass().getName());
                Method mStartTethering1 = iConnMgrClass.getMethod("startTethering", int.class,ResultReceiver.class,boolean.class);
                mStartTethering1.setAccessible(true);
                mStartTethering1.invoke(iConnMgr, mTETHERING_WIFI,new ResultReceiver(handler){
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        super.onReceiveResult(resultCode, resultData);
                    }
                }, true);
                Log.d("wifi_ap", "setWifiApEnabled(1)");
                return true;
            } else {
                Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
                Log.d("wifi_ap", "setWifiApEnabled(2)");
                return (Boolean) method.invoke(wifiManager, apConfig, enabled);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.d("wifi_ap", "NoSuchMethodException");
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.d("wifi_ap", "InvocationTargetException");
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.d("wifi_ap", "IllegalAccessException");
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("wifi_ap", "ClassNotFoundException");
            return false;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Log.d("wifi_ap", "NoSuchFieldException");
            return false;
        }
    }

}