package com.huabao.mytest.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huabao.mytest.utils.Constants;

public class CommomBroadcastReceiver extends BroadcastReceiver {

	private CommomInfoCallBack mCommomInfoCallBack;


	@Override
	public void onReceive(Context context, Intent intent) {
		/*if(Constants.NAVI_BROADCAST_FIXED.equals(intent.getAction())){
			if(mCommomInfoCallBack != null){
				String city = intent.getStringExtra("City");
				mCommomInfoCallBack.fixed(city);
			}
		}else if(Constants.NAVI_BROADCAST_SPEED.equals(intent.getAction())){
			int speed = intent.getIntExtra("speed", 40);
			if(mCommomInfoCallBack != null){
				mCommomInfoCallBack.speed(speed);
			}
		}else if(Constants.NAVI_BROADCAST_EDOG.equals(intent.getAction())){
			handleEdog(intent);
		}else if(Constants.NAVI_BROADCAST_NAVIINFO.equals(intent.getAction())){
			handleNaviInfo(intent);
		}else if(Constants.NAVI_BROADCAST_NAVISTATE.equals(intent.getAction())){
			int state = intent.getIntExtra("navistate", 0);
			if(mCommomInfoCallBack != null){
				mCommomInfoCallBack.naviState(state);
			}
		}else if(Constants.MUSIC_META_CHANGED.equals(intent.getAction())){
			String name = intent.getStringExtra("track");
			boolean state = intent.getBooleanExtra("isplaying", false);
			//boolean stop = intent.getBooleanExtra("isplaystop",false); //播放状态
            if(mCommomInfoCallBack != null){
                mCommomInfoCallBack.musicInfo(name, state);
            }
		}else*/ if(Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())){
			String path = intent.getData().getPath();
			if(mCommomInfoCallBack != null){
				if(Constants.STORAGE_SDCARD.equals(path)) {
					mCommomInfoCallBack.sdcard(true);
				}else if(Constants.STORAGE_USB.equals(path)) {
					mCommomInfoCallBack.usb(true);
				}
			}
		}else if(Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())){
			String path = intent.getData().getPath();
			if(mCommomInfoCallBack != null){
				if(Constants.STORAGE_SDCARD.equals(path)) {
					mCommomInfoCallBack.sdcard(false);
				}else if(Constants.STORAGE_USB.equals(path)) {
					mCommomInfoCallBack.usb(false);
				}
			}
		}/*else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())){
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			String name = device.getName();
			if(mCommomInfoCallBack != null){
				mCommomInfoCallBack.bluetooth(name, true);
			}
		}else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())){
			if(mCommomInfoCallBack != null){
				mCommomInfoCallBack.bluetooth(null, false);
			}
		}else if(WifiManagerUtil.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())
				|| WifiManagerUtil.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
			if(mCommomInfoCallBack != null){
				mCommomInfoCallBack.wifiChanged();
			}
		}*/
	}

	private void handleEdog(Intent intent){
		int type = intent.getIntExtra("edogType", -1);
		float value = 0;
		if(type != -1){
			value = intent.getFloatExtra("limitNum", 0);
		}
		if(mCommomInfoCallBack != null){
			mCommomInfoCallBack.edog(type, value);
		}
	}

	private void handleNaviInfo(Intent intent){
		int arrowId = intent.getIntExtra("arrow_id", 15);
		String streetName = intent.getStringExtra("next_street_name");
		streetName = streetName.replace("下一道路:", "");
		int needTime = intent.getIntExtra("all_need_time", 0);
		int streetDistence = intent.getIntExtra("next_street_distence", 0);
		int allNeedDistence = intent.getIntExtra("all_need_distence", 0);
		String carLane = intent.getStringExtra("carLane");
		//carLane = "[1,2,2,4]";
		if(mCommomInfoCallBack != null){
			mCommomInfoCallBack.naviInfo(arrowId, streetName, needTime, streetDistence
					, allNeedDistence, carLane);
		}
	}

	public interface CommomInfoCallBack {
		void fixed(String city);
		void speed(int speed);
		void edog(int type, float value);
		void naviInfo(int arrowId, String streetName, int needTime, int streetDistence
                , int allDistence, String carLane);
		void naviState(int state);
		void musicInfo(String name, boolean state);
		void sdcard(boolean mounted);
		void usb(boolean mounted);
		void bluetooth(String name, boolean state);
		void wifiChanged();
	}

	public void setNaviInfoCallBack(CommomInfoCallBack callBack){
		this.mCommomInfoCallBack = callBack;
	}

}