package com.huabao.mytest.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;


public class GpsSatelliteUtil {

    private Location location;
    // 定位管理类
    private LocationManager locationManager;
    private Context mContext;


    public GpsSatelliteUtil(Context context) {
        Log.d("MyTest", "[GpsSatelliteUtil] GpsSatelliteUtil()");
        this.mContext = context;
        // 获取位置管理服务
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    //打开GPS模块
    public void openGPSSettings() {
        Log.d("MyTest", "[GpsSatelliteUtil] openGPSSettings()");
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(mContext, "GPS模块正常", Toast.LENGTH_SHORT).show();
            getLocation();
//            getSatellites();
            return;
        }
        Toast.makeText(mContext, "请开启GPS！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        ((Activity) mContext).startActivityForResult(intent, 1120); //此为设置完成后返回到获取界面
    }

    //位置监听
    public void getLocation() {
        Log.d("MyTest", "[GpsSatelliteUtil] getLocation()");

        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 查找到服务信息    位置数据标准类
        Criteria criteria = new Criteria();
        //查询精度:高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拔:是
        criteria.setAltitudeRequired(true);
        //是否查询方位角:是
        criteria.setBearingRequired(true);
        //是否允许付费
        criteria.setCostAllowed(true);
        // 电量要求:低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //是否查询速度:是
        criteria.setSpeedRequired(true);

        String provider = locationManager.getBestProvider(criteria, true);
        // 获取GPS信息   获取位置提供者provider中的位置信息
        location = locationManager.getLastKnownLocation(provider);
        // 通过GPS获取位置
        updateToNewLocation(location);
        // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
        //实时获取位置提供者provider中的数据，一旦发生位置变化 立即通知应用程序locationListener
        locationManager.requestLocationUpdates(provider, 1000, 0, locationListener);

        Log.d("MyTest", "[GpsSatelliteUtil] getLocation(void)");
    }

    //定位监听类负责监听位置信息的变化情况
    private final LocationListener locationListener = new LocationListener() {
        //当位置改变时调用下面的函数
        @Override
        public void onLocationChanged(Location location) {
            //通过GPS获取位置，新的位置信息放在location中，调用updateToNewLocation函数显示位置信息
            Log.d("MyTest", "[GpsSatelliteUtil] onLocationChanged() " + location);
            updateToNewLocation(location);
        }

        //当Provider不可用时调用下面的函数
        @Override
        public void onProviderDisabled(String arg0) {
            Log.d("MyTest", "[GpsSatelliteUtil] onProviderDisabled() " + arg0);
        }

        @Override
        public void onProviderEnabled(String arg0) {
            Log.d("MyTest", "[GpsSatelliteUtil] onProviderEnabled() " + arg0);
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            Log.d("MyTest", "[GpsSatelliteUtil] onStatusChanged() " + arg0 + ", " + arg1 + ", " + arg2);
            updateToNewLocation(null);
        }
    };

    //位置信息
    private void updateToNewLocation(Location location) {
//        Log.d("MyTest", "[GpsSatelliteUtil] updateToNewLocation()");
        if (location != null) {
            float bear = location.getBearing();   //偏离正北方的度数
            float latitude = (float) location.getLatitude();      //维度
            float longitude = (float) location.getLongitude();     //经度
            float GpsSpeed = (float) location.getSpeed();    //速度
            long GpsTime = location.getTime();  //时间
            Date date = new Date(GpsTime);      //利用Date进行时间的转换
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       //设置时间的显示格式 也可以设置为：yyyy/MM/dd HH:mm:ss
            String gpsTime = df.format(date);
            float GpsAlt = (float) location.getAltitude();       //海拔
        } else {
//            Toast.makeText(this, "无法获取地理信息", Toast.LENGTH_SHORT).show();
        }
    }

    //卫星信息监听
    public void getSatellites() {
        Log.d("MyTest", "[GpsSatelliteUtil] getSatellites()");

        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //监听卫星，statusListener为响应函数
        locationManager.addGpsStatusListener(statusListener);

        getSatellitesInfo();

        Log.d("MyTest", "[GpsSatelliteUtil] getSatellites(void)");
    }

    //添加监听卫星
    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {
//            Log.d("MyTest", "[GpsSatelliteUtil] onGpsStatusChanged()");
            //获取GPS卫星信息，与获取location位置信息一样，还是通过locationManager类，方法为getGpsStatus，返回的是一个GpsStatus类型的结构
            //触发事件event
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d("MyTest", "[GpsSatelliteUtil] onGpsStatusChanged() GPS_EVENT_STARTED");
                    break;
                //第一次定位时间
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d("MyTest", "[GpsSatelliteUtil] onGpsStatusChanged() GPS_EVENT_FIRST_FIX");
                    break;
                //收到卫星信息，并调用DrawMap()函数，进行卫星信号解析并显示到屏幕上
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    getSatellitesInfo();
                    Log.d("MyTest", "[GpsSatelliteUtil] onGpsStatusChanged() GPS_EVENT_SATELLITE_STATUS");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d("MyTest", "[GpsSatelliteUtil] onGpsStatusChanged() GPS_EVENT_STOPPED");
                    break;
            }
        }
    };

    //卫星信息
    protected void getSatellitesInfo() {
//        Log.d("MyTest", "[GpsSatelliteUtil] getSatellitesInfo()");
        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //获取当前状态
        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        //获取卫星颗数的默认最大值
        int maxSatellites = gpsStatus.getMaxSatellites();
        //获取所有的卫星
        Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
        //卫星颗数统计
        int count = 0;
        int scount = 0;
        StringBuilder sb = new StringBuilder();
        while (iters.hasNext() && count <= maxSatellites) {
            count++;
            GpsSatellite s = iters.next();
            //卫星的信噪比
            float snr = s.getSnr();
            sb.append("第").append(count).append("颗").append("：").append(snr).append("\n");
            if(snr != 0){
                scount++;
            }

//            float azi = s.getAzimuth();    //坐标方位角
//            float evt = s.getElevation();  //卫星仰角
        }
//        Log.d("MyTest", "[GpsSatelliteUtil] maxSatellites = " + maxSatellites
//                + ", count = " + count + ", scount = " + scount);
//        Log.d("MyTest", "[GpsSatelliteUtil] " + "\n" + sb.toString());
        if (mGpsSatelliteListener != null) {
            if (scount > 3) {
                mGpsSatelliteListener.onGpsSatelliteListener(true);
            } else {
                mGpsSatelliteListener.onGpsSatelliteListener(false);
            }
        }
    }

    public interface GpsSatelliteListener {
        void onGpsSatelliteListener(boolean isHasSatellite);
    }

    private GpsSatelliteListener mGpsSatelliteListener;

    public void setGpsSatelliteListener(GpsSatelliteListener listener) {
        this.mGpsSatelliteListener = listener;
    }

    public void destroy(){
        locationManager.removeGpsStatusListener(statusListener);
        locationManager.removeUpdates(locationListener);
        locationManager = null;
        mGpsSatelliteListener = null;
        mContext = null;
    }

}