package com.huabao.mytest.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

public class LauncherUtil {

    /**
     * 创建匹配的名字
     */
    private StringBuffer createMatchName(ComponentName launcher){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(launcher.getPackageName());
        stringBuffer.append(".");
        stringBuffer.append(launcher.getClassName());
        return  stringBuffer;
    }

    /**
     * 清除当前默认的Launcher
     */
    private void clearDefaultLauncherApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ArrayList<IntentFilter> intentList = new ArrayList<>();
        ArrayList<ComponentName> componetNameList = new ArrayList<>();
        //查询Activity
        packageManager.getPreferredActivities(intentList, componetNameList, null);
        try {
            for (int i=0; i< intentList.size(); i++) {
                IntentFilter intentFilter = intentList.get(i);
                //筛选Launcher中主界面
                if (intentFilter.hasAction(Intent.ACTION_MAIN) && intentFilter.hasCategory(Intent.CATEGORY_HOME)) {
                    packageManager.clearPackagePreferredActivities(componetNameList.get(i).getPackageName());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取到Android系统硬件或者手机上安装的全部的Launcher
     */
    private List<ResolveInfo> getAllLauncherApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        return packageManager.queryIntentActivities(intent, 0);
    }

    /**
     * 设置系统默认的Launcher
     */
    public void setDefaultLauncher1(Context context, String packageName, String mainActivityName) {
        clearDefaultLauncherApps(context);
        List<ResolveInfo> allLauncherList = getAllLauncherApps(context);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        ComponentName launcher = new ComponentName(packageName, mainActivityName);
        ComponentName[] componentNameSet = new ComponentName[allLauncherList.size()];
        int defaultMatchLauncher = 0;
        for (int i=0; i<allLauncherList.size(); i++) {
            ResolveInfo resolveInfo = allLauncherList.get(i);
            componentNameSet[i] = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
            StringBuffer stringBuffer = createMatchName(launcher);
            if (stringBuffer.toString().equals(resolveInfo.activityInfo.name)) {
                defaultMatchLauncher = resolveInfo.match;
            }
        }
        try {
            context.getPackageManager().addPreferredActivity(intentFilter, defaultMatchLauncher, componentNameSet, launcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDefaultLauncher2(Context context){
        PackageManager pm = context.getPackageManager();
        pm.clearPackagePreferredActivities(context.getPackageName());

//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.MAIN");
//        filter.addCategory("android.intent.category.HOME");
//        filter.addCategory("android.intent.category.DEFAULT");
//        ComponentName component = new ComponentName("com.cyanogenmod.trebuchet", "com.android.launcher3.Launcher");
//        ComponentName[] components = new ComponentName[] {new ComponentName(context.getPackageName()
//                , "Launcher"), component};
//        pm.addPreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, components, component);
    }

}
