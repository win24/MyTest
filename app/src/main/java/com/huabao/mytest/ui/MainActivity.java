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
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huabao.mytest.R;
import com.huabao.mytest.dialog.AirDialog;
import com.huabao.mytest.media.Mount;
import com.huabao.mytest.utils.GpsSatelliteUtil;
import com.huabao.mytest.utils.WifiManagerUtil;
import com.huabao.mytest.bean.FreqBean;
import com.huabao.mytest.media.CommomBroadcastReceiver;
import com.huabao.mytest.utils.IpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private WifiManagerUtil wifiManagerUtil;
    private Animation mAlphaAnimation;
    private RelativeLayout mEdogInfoRelativeLayout;

    private CommomBroadcastReceiver mCommomBroadcastReceiver;

    private TextView mTipsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
//                .detectDiskWrites().detectNetwork() // or .detectAll() for
//                // all detectable
//                // problems
//                .penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.adb_wifi_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execShell();

//                Intent intentw = new Intent(Intent.ACTION_MAIN);
//                intentw.addCategory(Intent.CATEGORY_HOME);
//                intentw.setClassName("android", "com.android.internal.app.ResolverActivity");
//                startActivity(intentw);
                xmlParse(MainActivity.this, null);
            }
        });
        mTipsTextView = (TextView) findViewById(R.id.adb_wifi_textview);

        test();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mEdogInfoRelativeLayout.clearAnimation();
        //stopBreath();

//        StorageManagerUtils.getInstance().stopListen(this);
//        unregisterReceiver(mCommomBroadcastReceiver);
    }

    private void test(){
//        startBrowser();

        //test get instance
//        SystemUIFactory.createFromConfig(this);

        //获取书签
//        getRecords();

//        checkPermission();

//        testUri();

//        testAnimation();

//        loadApps();

//        provider();

//        initInfoListener();

//        testJsonToList();

//        testObserverSettings();

//        setNavigationBar();

//        mediaSessionManager();

//        Log.d("MyTest", "ro.serialno is " + getProperty("ro.serialno", ""));

//        getScreenPixel();

//        isZhLanguageSetting();

//        isBluetoothHeadsetConnected();

//        testUsbMount();
    }

    private void startBrowser(){
//        Intent intent= new Intent(); intent.setAction("android.intent.action.VIEW");
////        String url = "http://www.baidu.com";
////        Uri content_url = Uri.parse(url);
////        intent.setData(content_url);
//        intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
//        startActivity(intent);
//        finish();
    }

    public void getRecords() {
        String records = null;
        StringBuilder recordBuilder = null;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                Uri.parse("content://browser/bookmarks"), new String[] {
                        "title", "url", "date" }, "date!=?",
                new String[] { "null" }, "date desc");
        while (cursor != null && cursor.moveToNext()) {
            String url = null;
            String title = null;
            String time = null;
            String date = null;

            recordBuilder = new StringBuilder();
            title = cursor.getString(cursor.getColumnIndex("title"));
            url = cursor.getString(cursor.getColumnIndex("url"));

            date = cursor.getString(cursor.getColumnIndex("date"));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm;ss");
            Date d = new Date(Long.parseLong(date));
            time = dateFormat.format(d);

            System.out.println(title + url + time);
        }
    }

    private void testUri(){
        Intent returnIt;
        //1，调web浏览器
        Uri myBlogUri = Uri.parse("http://xxxxx.com");
        returnIt = new Intent(Intent.ACTION_VIEW, myBlogUri);
        //2，地图
        Uri mapUri = Uri.parse("geo:38.899533,-77.036476");
        returnIt = new Intent(Intent.ACTION_VIEW, mapUri);
        //3，调拨打电话界面
        Uri telUri = Uri.parse("tel:100861");
        returnIt = new Intent(Intent.ACTION_DIAL, telUri);
        //4，直接拨打电话
        Uri callUri = Uri.parse("tel:100861");
        returnIt = new Intent(Intent.ACTION_CALL, callUri);
        //5，卸载
        Uri uninstallUri = Uri.fromParts("package", "xxx", null);
        returnIt = new Intent(Intent.ACTION_DELETE, uninstallUri);
        //6，安装
        Uri installUri = Uri.fromParts("package", "xxx", null);
        returnIt = new Intent(Intent.ACTION_PACKAGE_ADDED, installUri);
        //7，播放
        Uri playUri = Uri.parse("file:///sdcard/download/everything.mp3");
        returnIt = new Intent(Intent.ACTION_VIEW, playUri);
        //8，调用发邮件
        Uri emailUri = Uri.parse("mailto:xxxx@gmail.com");
        returnIt = new Intent(Intent.ACTION_SENDTO, emailUri);
        //9，发邮件
        returnIt = new Intent(Intent.ACTION_SEND);
        String[] tos = { "xxxx@gmail.com" };
        String[] ccs = { "xxxx@gmail.com" };
        returnIt.putExtra(Intent.EXTRA_EMAIL, tos);
        returnIt.putExtra(Intent.EXTRA_CC, ccs);
        returnIt.putExtra(Intent.EXTRA_TEXT, "body");
        returnIt.putExtra(Intent.EXTRA_SUBJECT, "subject");
        returnIt.setType("message/rfc882");
        Intent.createChooser(returnIt, "Choose Email Client");
        //10，发短信
        Uri smsUri = Uri.parse("tel:100861");
        returnIt = new Intent(Intent.ACTION_VIEW, smsUri);
        returnIt.putExtra("sms_body", "yyyy");
        returnIt.setType("vnd.android-dir/mms-sms");
        //11，直接发邮件
        Uri smsToUri = Uri.parse("smsto://100861");
        returnIt = new Intent(Intent.ACTION_SENDTO, smsToUri);
        returnIt.putExtra("sms_body", "yyyy");
        //12，发彩信
        Uri mmsUri = Uri.parse("content://media/external/images/media/23");
        returnIt = new Intent(Intent.ACTION_SEND);
        returnIt.putExtra("sms_body", "yyyy");
        returnIt.putExtra(Intent.EXTRA_STREAM, mmsUri);
        returnIt.setType("image/png");
    }

    private void testAnimation(){

        mEdogInfoRelativeLayout = (RelativeLayout) findViewById(R.id.edog_relativelayout);
        mAlphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        //mEdogInfoRelativeLayout.startAnimation(mAlphaAnimation);
        startBreath();

    }

    /**
     * 呼吸灯效果
     */
    private final int BREATH_INTERVAL_TIME = 1000; //设置呼吸灯时间间隔
    private AlphaAnimation animationFadeIn;
    private AlphaAnimation animationFadeOut;
    private boolean isStartBreath = false;
    private void startBreath(){
        isStartBreath = true;

        animationFadeIn = new AlphaAnimation(0.1f, 1.0f);
        animationFadeIn.setDuration(BREATH_INTERVAL_TIME);
//        animationFadeIn.setStartOffset(100);

        animationFadeOut = new AlphaAnimation(1.0f, 0.1f);
        animationFadeOut.setDuration(BREATH_INTERVAL_TIME);
//        animationFadeIn.setStartOffset(100);

        animationFadeIn.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation arg0) {
                if(isStartBreath){
                    mEdogInfoRelativeLayout.startAnimation(animationFadeOut);
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });

        animationFadeOut.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation arg0) {
                if(isStartBreath){
                    mEdogInfoRelativeLayout.startAnimation(animationFadeIn);
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });
        mEdogInfoRelativeLayout.startAnimation(animationFadeOut);
    }

    private void stopBreath(){
        isStartBreath = false;
    }

    public void execShell(){
        Process localProcess = null;
        try {
            localProcess = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(localProcess.getOutputStream());
            os.writeBytes("setprop service.adb.tcp.port 5555\n");
//            os.writeBytes("stop adbd\n");
//            os.writeBytes("start adbd\n");
            os.flush();

            android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = IpUtils.intToIp(ipAddress);

            mTipsTextView.setText(ip);

            Toast.makeText(this, ip, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载APP列表
     *
     */
    private List<ResolveInfo> loadApps(){
        PackageManager manager = getPackageManager();

        //home程序
//        Intent homeIntent = new Intent(Intent.ACTION_MAIN, null);
//        homeIntent.addCategory(Intent.CATEGORY_HOME);
//        List<ResolveInfo> homeAppsList = manager.queryIntentActivities(homeIntent, 0);
//        HashSet<String> homeSet = new HashSet<String>();
//        for(ResolveInfo info : homeAppsList){
//            homeSet.add(info.activityInfo.applicationInfo.packageName);
//        }

        //所有程序
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appsList = manager.queryIntentActivities(mainIntent, 0);

        //过滤home程序
        List<ResolveInfo> list = new ArrayList<ResolveInfo>();
        for(ResolveInfo info : appsList){
            Log.d("app_info", info.activityInfo.packageName + "/" + info.activityInfo.name);
//            if(!homeSet.contains(info.activityInfo.applicationInfo.packageName)){
            list.add(info);
//            }
        }
        appsList = list;

        return appsList;
    }

    public void startApp() {

//        Intent intent = getPackageManager().getLaunchIntentForPackage(pck);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        startActivity(intent);

//        List<ResolveInfo> appsList = LauncherApplication.getApplication().getAppsList();
//        for (ResolveInfo info : appsList) {
//            if (info.activityInfo.packageName.equals(pck)) {
//                pck = info.activityInfo.packageName;
//                cls = info.activityInfo.name;
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                intent.setComponent(new ComponentName(pck, cls));
//                context.startActivity(intent);
//                break;
//            }
//        }
    }

    private void provider(){
        Intent intent = new Intent(this, ProviderActivity.class);
        startActivity(intent);
    }

    private void initInfoListener() {
//        StorageManagerUtils.getInstance().startListen(this);

        mCommomBroadcastReceiver = new CommomBroadcastReceiver();
        mCommomBroadcastReceiver.setNaviInfoCallBack(new CommomBroadcastReceiver.CommomInfoCallBack() {

            @Override
            public void fixed(String city) {
                Log.d("navi_info", "city=" + city);
            }

            @Override
            public void speed(int speed) {
                Log.d("navi_road_info", "speed: " + speed);
            }

            @Override
            public void edog(int type, float value) {
                Log.d("navi_road_info", "edog: " + type + ", " +value);
            }

            @Override
            public void naviInfo(int arrowId, String streetName, int needTime, int streetDistence
                    , int allDistence, String carLane) {
                Log.d("navi_road_info", arrowId + ", " + streetName + ", " + needTime
                        + ", " + streetDistence + ", " + allDistence + ", " + carLane);
            }

            @Override
            public void naviState(int state) {
                Log.d("navi_info", "state: " + state);
            }

            @Override
            public void musicInfo(String name, boolean state) {

            }

            @Override
            public void sdcard(boolean mounted) {

            }

            @Override
            public void usb(boolean mounted) {

            }

            @Override
            public void bluetooth(String name, boolean state) {

            }

            @Override
            public void wifiChanged() {

            }

        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(1000);
//        intentFilter.addAction(Constants.NAVI_BROADCAST_FIXED);
//        intentFilter.addAction(Constants.NAVI_BROADCAST_SPEED);
//        intentFilter.addAction(Constants.NAVI_BROADCAST_EDOG);
//        intentFilter.addAction(Constants.NAVI_BROADCAST_NAVIINFO);
//        intentFilter.addAction(Constants.NAVI_BROADCAST_NAVISTATE);
//        intentFilter.addAction(Constants.MUSIC_META_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
//        intentFilter.addAction(WifiManagerUtil.WIFI_STATE_CHANGED_ACTION);
//        intentFilter.addAction(WifiManagerUtil.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addDataScheme("file");
        registerReceiver(mCommomBroadcastReceiver, intentFilter);
    }

    private void testJsonToList() {
        List<FreqBean> freqList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FreqBean freqBean = new FreqBean();
            freqBean.setChannelType(0);
            freqBean.setCollect(false);
            freqBean.setPressed(false);
            freqBean.setFreq(4444);
            freqBean.setType(1);
            freqList.add(freqBean);
        }
        Gson gson = new Gson();
        String str = gson.toJson(freqList);
        Log.d("json_str", "JSON : " + str);
        List<FreqBean> list = gson.fromJson(str, new TypeToken<List<FreqBean>>(){}.getType());
        Log.d("json_str", "list size : " + list.size());
    }

    private void testObserverSettings(){
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("driving_video_switch")
                , false, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                Settings.System.getInt(getContentResolver(),"driving_video_switch", 1);
            }
        });
        Settings.System.putInt(getContentResolver(),"driving_video_switch", 1);
    }

    public void setNavigationBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void mediaSessionManager(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            MediaSessionManager mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
            List<MediaController> mediaControllerList = mediaSessionManager.getActiveSessions(null);
            Log.d("mediasession", "mediaControllerList.size = " + mediaControllerList.size());
        }
    }

    public static String getProperty(String key, String defaultyValue){
        String value = defaultyValue;
        try{
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, "unknown"));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return value;
        }
    }

    public void xmlParse(Context context, String p) {
        //can't create in /data/media/0 because permission
        //can create in /sdcard/hotel
//        File mSettings = new File(HOTEL_PATH_XML);
//        if (!mSettings.exists()) {
//            mSettings.mkdirs();
//        }
//        File settings = new File(mSettings,"settings.xml");
//        Log.i("XmlPullParser-----settings", settings+"+1+");
//        if (!settings.exists()) {
//            Log.i("XmlPullParser-----settings", settings+"+2+");
//            return;
//        }

        try {
//            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//            factory.setNamespaceAware(true);
//            XmlPullParser xpp = factory.newPullParser();
//            xpp.setInput(new FileInputStream(settings), "utf-8");
            int count = 0;
            XmlPullParser xpp = getResources().getXml(R.xml.city_id);
            StringBuilder stringBuilder = new StringBuilder();
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tag = xpp.getName();
                    if (tag.equals("RECORD")) {
                        count += 1;
                        stringBuilder = new StringBuilder();
                    }else if (tag.equals("Fid")) {
                        stringBuilder.append(" \nFid: " + xpp.nextText());
                    }else if (tag.equals("name")) {
                        stringBuilder.append("\nname: " + xpp.nextText());
                    }else if (tag.equals("name_en")) {
                        stringBuilder.append("\nname_en: " + xpp.nextText());
                    }else if (tag.equals("name_py")) {
                        stringBuilder.append("\nname_py: " + xpp.nextText());
                    }else if (tag.equals("Fprovince_cn")) {
                        stringBuilder.append("\nFprovince_cn: " + xpp.nextText());
                    }else if (tag.equals("Fweathercn")) {
                        stringBuilder.append("\nFweathercn: " + xpp.nextText());
                        Log.i("XmlPullParserTAG", stringBuilder.toString());
                    }
                }
                eventType = xpp.next();
            }
            Log.i("XmlPullParserTAG", "city count " + count);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getScreenPixel(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Log.d("density", "Density is "
                + displayMetrics.density + " densityDpi is "
                + displayMetrics.densityDpi+ " height: "
                + displayMetrics.heightPixels + " width: "
                + displayMetrics.widthPixels);
    }

    public void updataLanguage(String s) {
        String language = "zhCN";
        if (!s.equals("")) {
            language = s;
        }

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        String localelanguage = config.locale.getLanguage();
        String localeCountry = config.locale.getCountry();

        Log.e("localelanguage", localelanguage);
        Log.e("localeCountry", localeCountry);

        if (!(localelanguage + localeCountry).equals(language)) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
        }
        Log.e("设置语言:", "设置语言");


        DisplayMetrics dm = resources.getDisplayMetrics();

        switch (language) {
            case "zhCN":
                config.locale = Locale.CHINA;
                break;
            case "zhTW":
                config.locale = Locale.TAIWAN;
                break;
            case "enUS":
                config.locale = Locale.US;
                break;
            case "koKR":
                config.locale = Locale.KOREA;
                break;
            case "deDE":
                config.locale = Locale.GERMANY;
                break;
            case "jaJP":
                config.locale = Locale.JAPAN;
                break;
            case "ruRU":
                config.locale = new Locale("ru", "RU");
                break;
            default:
                config.locale = Locale.CHINA;
                break;
        }
        resources.updateConfiguration(config, dm);
    }

    public boolean isZhLanguageSetting() {
        String language = getLanguageEnv();

        if (language != null
                && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW")))
            return true;
        else
            return false;
    }

    private String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("MyTest", "[ConnectyUtil] isBluetoothHeadsetConnected()");
        if(mBluetoothAdapter != null) {
            Log.d("MyTest", "[ConnectyUtil] isBluetoothHeadsetConnected(1)");
            int a2dp = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
            int headset = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
            int health = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
            Log.d("MyTest", "[ConnectyUtil] isBluetoothHeadsetConnected() a2dp=" + a2dp
                    + ", headset=" + headset + ", health=" + health + ", BluetoothProfile.STATE_CONNECTED=" + BluetoothProfile.STATE_CONNECTED);
            if (a2dp == BluetoothProfile.STATE_CONNECTED || headset == BluetoothProfile.STATE_CONNECTED
                    || health == BluetoothProfile.STATE_CONNECTED) {
                return true;
            }
        }
        return false;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE
                    , Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.ACCESS_NETWORK_STATE
                    , Manifest.permission.CAMERA};
            for(int i=0; i<permissions.length; i++){
                int result = checkSelfPermission(permissions[i]);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 200);
                }
            }
        }
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
            }
        }
    }

    private void testUsbMount(){
        Mount.getUdiskPath();
        Mount.getStorageInfo(MainActivity.this);
        Mount.getStorageVolume(MainActivity.this);
        Mount.isUsbExist(this);
    }

}
