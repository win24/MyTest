package com.huabao.mytest.utils;


import android.net.Uri;
import android.os.Environment;

import java.text.SimpleDateFormat;

/**
 * 常量
 *
 */
public class Constants {


	/** 导航广播 */
	public final static String NAVI_BROADCAST_SPEED = "cn.jyuntech.smynavi.speed";          //实时速度广播
	public final static String NAVI_BROADCAST_EDOG = "cn.jyuntech.smynavi.edog";            //电子眼信息广播
	public final static String NAVI_BROADCAST_NAVIINFO = "cn.jyuntech.smynavi.naviinfo";    //导航信息广播
	public final static String NAVI_BROADCAST_NAVISTATE = "cn.jyuntech.smynavi.navistate";  //导航状态广播
	public final static String NAVI_BROADCAST_FIXED = "cn.jyuntech.smynavi.location";          //定位状态广播


	public final static String MUSIC_META_CHANGED = "com.android.music.metachanged";          //定位状态广播

	public final static String SDCRAD_PATH = Environment.getExternalStorageDirectory().getPath();
	public final static String STORAGE_SDCARD = "/storage/sdcard1";
	public final static String STORAGE_USB = "/storage/usbdisk0";

}