package com.sinohb.dvrpreview;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sinohb.dvrpreview.utils.LogUtils;
import com.sinohb.dvrpreview.utils.TaskCenter;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private EditText rtsp_edt;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.e(TAG,"onCreate()");
		setContentView(R.layout.activity_main);
		connect();
		rtsp_edt = (EditText) findViewById(R.id.rtsp_edt);
		findViewById(R.id.rtsp_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String url = "rtsp://192.168.1.5:8554/live/ch00_0";
				if (TextUtils.isEmpty(url) || !url.startsWith("rtsp://")) {
					Toast.makeText(MainActivity.this, "RTSP视频流地址错误！", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(MainActivity.this, RtspActivity.class);
				intent.putExtra("rtsp_url", url);
				startActivity(intent);
			}
		});
		findViewById(R.id.local_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, LocalH264Activity.class));
			}
		});
		findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new Thread(){
					public void run() {
						try{
							Instrumentation inst = new Instrumentation();
							inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
						}
						catch (Exception e) {
							LogUtils.e("TAG", e.toString());
						}
					}
				}.start();
			}
		});
	}


	public void connect() {
		LogUtils.d(TAG,"connect()");
		TaskCenter.sharedCenter().connect("192.168.1.5",8555);
	}

	public void disconnect() {
		LogUtils.d(TAG,"disconnect()");
		TaskCenter.sharedCenter().disconnect();
	}


	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.e(TAG,"onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtils.e(TAG,"onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtils.e(TAG,"onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		disconnect();
		LogUtils.e(TAG,"onDestroy()");
	}
}
