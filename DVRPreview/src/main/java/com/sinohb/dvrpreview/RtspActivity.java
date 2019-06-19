package com.sinohb.dvrpreview;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.sinohb.dvrpreview.rtp.H264StreamInterface;
import com.sinohb.dvrpreview.rtp.TCP4RtspUtil;
import com.sinohb.dvrpreview.rtp.VideoStreamImpl;
import com.sinohb.dvrpreview.rtsp.RtspDecoder;
import com.sinohb.dvrpreview.utils.ChannelBean;
import com.sinohb.dvrpreview.utils.LogUtils;
import com.sinohb.dvrpreview.utils.TaskCenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//RTSP协议实时播放播放H264视频
public class RtspActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener {
	private static final String TAG = RtspActivity.class.getName();
	private Socket mSocket;
	private String sessionId;
	private SurfaceView mSurfaceView;
	private ImageView mFloat_Button,mBack_system;
	private Socket clientSocket = null;

	private TextView mChannel1, mChannel41, mChannel42, mChannel43, mChannel44,
			mChannel61, mChannel62, mChannel63, mChannel64, mChannel65, mChannel66;
	private ConstraintLayout mTouch_view, cl_type4, cl_type6;
	static boolean previewing = false;

	public static boolean getPreviewStatus() {
		return previewing;
	}

	private boolean isFirst = true;
	private int mCount = 0;
	//边播放边保存到SD卡文件目录
	private static final String filePath = Environment.getExternalStorageDirectory() + "/dvr_rtsp.h264";
//    Handler handler = new Handler();

	File encodedFile = new File(filePath);
	InputStream is;
	private RtspDecoder mPlayer = null;
	private String rtsp_url;
	private TCP4RtspUtil client;

	private byte[] mByteParm = new byte[1];
	private byte[] mByteParm2 = new byte[2];
	private int mType = 6;/*默认通道6(2*3)*/
	private List<ChannelBean> mList;


	private static final int MSG_LOOP_DATA = 0x01;
	private static final int MSG_CONNECT_SERVICE = 0x02;
	private static final int MSG_CHANNEL_MODE = 0x03;
	private static final int MSG_HIDE_OSD = 0x04;
	private static final int MSG_BACK_SYSTEM = 0x05;

	private Thread getDataThread;

	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MSG_LOOP_DATA:
					LogUtils.d(TAG, "MSG_LOOP_DATA");
					handler.sendEmptyMessageDelayed(MSG_CONNECT_SERVICE, 3000);
					handler.sendEmptyMessageDelayed(MSG_LOOP_DATA, 3000);
					break;
				case MSG_CONNECT_SERVICE:
					LogUtils.d(TAG, "MSG_CONNECT_SERVICE");
					if (mSurfaceView != null) {
						if (client != null) {
							client.pause();
							client.doStop();
							client = null;
						}
						if (getDataThread != null) {
							LogUtils.d(TAG, "getDataThread = null");
							getDataThread = null;
						}
						getDataThread = new Thread() {
							@Override
							public void run() {
								super.run();

								try {
									getRtspStream();
									client.play();
								} catch (IOException e) {
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						};
						getDataThread.start();
					}
					break;
				case MSG_CHANNEL_MODE:
					currChannelType();
					break;
				case MSG_HIDE_OSD:
					setHideView();
					break;
				case MSG_BACK_SYSTEM:
					LogUtils.d(TAG,"MSG_BACK_SYSTEM");
					new Thread() {
						public void run() {
							try {
								Instrumentation inst = new Instrumentation();
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
							} catch (Exception e) {
								LogUtils.e("TAG", e.toString());
							}
						}
					}.start();
					break;

			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.e(TAG, "onCreate");
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_rtsp);
		mList = new ArrayList<>();

		mByteParm[0] = 0x01;
		sendMessage(mByteParm);

		TaskCenter.sharedCenter().setDisconnectedCallback(new TaskCenter.OnServerDisconnectedCallbackBlock() {
			@Override
			public void callback(IOException e) {
				LogUtils.d(TAG, "断开连接");

			}
		});
		TaskCenter.sharedCenter().setConnectedCallback(new TaskCenter.OnServerConnectedCallbackBlock() {
			@Override
			public void callback() {
				LogUtils.d(TAG, "连接成功");

			}
		});
		TaskCenter.sharedCenter().setReceivedCallback(new TaskCenter.OnReceiveCallbackBlock() {
			@Override
			public void callback(byte[] receicedMessage) {
				LogUtils.d(TAG, "receicedMessage=" + byteToHexStr(receicedMessage));
				int msg = receicedMessage[0];
				if (receicedMessage != null && msg == 0x02) {
					mType = receicedMessage[1];
					LogUtils.d(TAG, "mType=" + mType);
					mList.clear();
					for (int i = 0; i < mType; i++) {
						ChannelBean mChannelBean = new ChannelBean();
						mChannelBean.channelNum = (int) receicedMessage[2 + 5 * i];
						mChannelBean.channelName = String.valueOf((char) ((int) (receicedMessage[2 + 5 * i + 1]))) + String.valueOf((char) ((int) (receicedMessage[2 + 5 * i + 2]))) + String.valueOf((char) ((int) (receicedMessage[2 + 5 * i + 3])));
						LogUtils.d(TAG, "channelNum=" + mChannelBean.channelNum + ",channelName=" + mChannelBean.channelName);
						mList.add(mChannelBean);
					}
					handler.removeMessages(MSG_CHANNEL_MODE);
					handler.sendEmptyMessageDelayed(MSG_CHANNEL_MODE, 0);

				}
//
			}
		});

		init();

	}

	private void init() {
		rtsp_url = getIntent().getStringExtra("rtsp_url");
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);

		mTouch_view = findViewById(R.id.touch_view);
		mTouch_view.setOnTouchListener(this);

		cl_type4 = findViewById(R.id.cl_type4);
		cl_type6 = findViewById(R.id.cl_type6);

		mChannel1 = findViewById(R.id.tv_channel1);
		mChannel41 = findViewById(R.id.tv_channel41);
		mChannel42 = findViewById(R.id.tv_channel42);
		mChannel43 = findViewById(R.id.tv_channel43);
		mChannel44 = findViewById(R.id.tv_channel44);
		mChannel61 = findViewById(R.id.tv_channel61);
		mChannel62 = findViewById(R.id.tv_channel62);
		mChannel63 = findViewById(R.id.tv_channel63);
		mChannel64 = findViewById(R.id.tv_channel64);
		mChannel65 = findViewById(R.id.tv_channel65);
		mChannel66 = findViewById(R.id.tv_channel66);

		mFloat_Button = findViewById(R.id.float_button);
		mFloat_Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LogUtils.d(TAG,"onClick(Float_Button)");
					mByteParm2[0] = 0x04;
					mByteParm2[1] = 0x01;
					sendMessage(mByteParm2);
			}
		});

		mBack_system = findViewById(R.id.back_system);
		mBack_system.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LogUtils.d(TAG,"onClick(BACK)");
				handler.removeMessages(MSG_BACK_SYSTEM);
				handler.sendEmptyMessageDelayed(MSG_BACK_SYSTEM, 0);
			}
		});

		mSurfaceView.getHolder().addCallback(this);

	}



	public void sendMessage(byte[] bytes) {
		TaskCenter.sharedCenter().send(bytes);
		handler.removeMessages(MSG_HIDE_OSD);
		handler.sendEmptyMessageDelayed(MSG_HIDE_OSD, 0);
	}

	private void setHideView() {
		if(TaskCenter.sharedCenter().isConnected()){
			if(cl_type4 != null){
				cl_type4.setVisibility(View.GONE);
			}
			if(cl_type6 != null ){
				cl_type6.setVisibility(View.GONE);
			}
		}

	}

	private void getRtspStream() throws Exception {
		LogUtils.d(TAG, "getRtspStream()");
//
//	    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyyMMddHHmmss");
//        //获取当前时间并作为时间戳给文件夹命名
//        String timeStamp1=simpleDateFormat.format(new Date());
//        File myPath = new File( "./tmp/"+timeStamp1 );
//        final String currfilePath = Environment.getExternalStorageDirectory() +"/"+timeStamp1+".h264";
//        LogUtils.d(TAG,"currfilePath="+currfilePath);

		client = new TCP4RtspUtil(rtsp_url, new VideoStreamImpl(new H264StreamInterface() {
			//            private OutputStream out = new FileOutputStream(currfilePath);
			public void process(byte[] stream) {

				try {
//	                this.out.write(stream);
					onReceiveVideoData(stream);
				} catch (Exception e) {
//	                try {
//		                out.close();
//	                } catch (IOException e1) {
//		                e1.printStackTrace();
//	                }
					e.printStackTrace();
				}
			}
		}));
		client.doStart();
		handler.removeMessages(MSG_LOOP_DATA);
		handler.sendEmptyMessageDelayed(MSG_LOOP_DATA, 1000);
	}

	/*根据当前通道类型切换相应显示已经通道名称*/
	private void currChannelType() {
		switch (mType) {
			case 1:/*(单一通道)*/
				cl_type4.setVisibility(View.GONE);
				cl_type6.setVisibility(View.GONE);
				mChannel1.setVisibility(View.VISIBLE);
				if (mList.size() > 0) {
					mChannel1.setText(mList.get(0).channelName);
				}
				break;
			case 4:/*(2*2画面)*/
				cl_type4.setVisibility(View.VISIBLE);
				mChannel1.setVisibility(View.GONE);
				cl_type6.setVisibility(View.GONE);
				if (mList.size() > 0) {
					mChannel41.setText(mList.get(0).channelName);
					mChannel42.setText(mList.get(1).channelName);
					mChannel43.setText(mList.get(2).channelName);
					mChannel44.setText(mList.get(3).channelName);
				}
				break;
			case 6:/*(2*3画面)*/
				cl_type6.setVisibility(View.VISIBLE);
				cl_type4.setVisibility(View.GONE);
				mChannel1.setVisibility(View.GONE);
				if (mList.size() > 0) {
					mChannel61.setText(mList.get(0).channelName);
					mChannel62.setText(mList.get(1).channelName);
					mChannel63.setText(mList.get(2).channelName);
					mChannel64.setText(mList.get(3).channelName);
					mChannel65.setText(mList.get(4).channelName);
					mChannel66.setText(mList.get(5).channelName);
				}

				break;
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			/**
			 * 点击的开始位置
			 */
			case MotionEvent.ACTION_DOWN:
				break;
			/**
			 * 触屏实时位置
			 */
			case MotionEvent.ACTION_MOVE:
				break;
			/**
			 * 离开屏幕的位置
			 */
			case MotionEvent.ACTION_UP:
				LogUtils.d(TAG, "mType=" + mType + ",结束位置,X=" + event.getX() + ",Y=" + event.getY());
				int x = (int) event.getX();
				int y = (int) event.getY();
				switch (mType) {
					case 1:/*(单一通道)*/
						LogUtils.d(TAG, "Channel--0");

						break;
					case 4:/*(2*2画面)*/
						if (y > 300) {
							if (x > 612) {
								LogUtils.d(TAG, "Channel--(4-4)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x04;
								sendMessage(mByteParm2);
							} else {
								LogUtils.d(TAG, "Channel--(4-3)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x03;
								sendMessage(mByteParm2);
							}
						} else {
							if (x > 612) {
								LogUtils.d(TAG, "Channel--(4-2)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x02;
								sendMessage(mByteParm2);
							} else {
								LogUtils.d(TAG, "Channel--(4-1)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x01;
								sendMessage(mByteParm2);
							}
						}

						break;
					case 6:/*(2*3画面)*/
						if (y > 300) {
							if (x < 341) {
								LogUtils.d(TAG, "Channel--(6--4)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x04;
								sendMessage(mByteParm2);
							} else if (683 < x) {
								LogUtils.d(TAG, "Channel--(6--6)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x06;
								sendMessage(mByteParm2);
							} else {
								LogUtils.d(TAG, "Channel--(6--5)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x05;
								sendMessage(mByteParm2);
							}
						} else {
							if (x < 341) {
								LogUtils.d(TAG, "Channel--(6--1)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x01;
								sendMessage(mByteParm2);
							} else if (683 < x) {
								LogUtils.d(TAG, "Channel--(6--3)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x03;
								sendMessage(mByteParm2);
							} else {
								LogUtils.d(TAG, "Channel--(6--2)");
								mByteParm2[0] = 0x03;
								mByteParm2[1] = 0x02;
								sendMessage(mByteParm2);
							}
						}
						break;
				}
				break;
			default:
				break;
		}
		/**
		 *  注意返回值
		 *  true：view继续响应Touch操作；
		 *  false：view不再响应Touch操作，故此处若为false，只能显示起始位置，不能显示实时位置和结束位置
		 */
		return true;
	}


	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.e(TAG, "onResume()");
	}


	@Override
	protected void onPause() {
		super.onPause();
		LogUtils.e(TAG, "onPause()");

	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtils.e(TAG, "onPause()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtils.e(TAG, "onDestroy");
		handler.removeMessages(MSG_LOOP_DATA);
		handler.removeMessages(MSG_CONNECT_SERVICE);
		handler.removeMessages(MSG_CHANNEL_MODE);
		handler.removeMessages(MSG_HIDE_OSD);
		handler.removeMessages(MSG_BACK_SYSTEM);
		handler.removeCallbacksAndMessages(null);
//        getMainLooper().quitSafely();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		LogUtils.e(TAG, "surfaceCreated()");
        isFirst = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
                    getRtspStream();
					/*调用播放开关*/
                    client.play();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		//初始化实时流解码器
		mPlayer = new RtspDecoder(holder.getSurface(), 0);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
	                           int height) {
		LogUtils.e(TAG, "in surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		LogUtils.d(TAG, "surfaceDestroyed()");
		//关闭操作
		if (client != null) {
			client.pause();
			client.doStop();
			client = null;
		}
		if (mPlayer != null) {
			mPlayer.stopRunning();
			mPlayer = null;
		}
		this.finish();
	}

	/*00 00 00 01 67 (SPS)
	00 00 00 01 68 (PPS)
	00 00 00 01 65 ( IDR 帧)
	00 00 00 01 61 (P帧)*/
	private void onReceiveVideoData(byte[] video) {
		handler.removeMessages(MSG_CONNECT_SERVICE);
		if (isFirst) {
			if (video[4] == 0x67) {
				byte[] tmp = new byte[video.length];
				//把video中索引0开始的15个数字复制到tmp中索引为0的位置上
				System.arraycopy(video, 0, tmp, 0, video.length);
				try {
					mPlayer.initial();
				} catch (Exception e) {
					return;
				}
			} else {
				return;
			}
			isFirst = false;
		}
		if (mPlayer != null)
			mPlayer.setVideoData(video);
	}

	@NonNull
	public String byteToHexStr(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			result.append("0x");
			result.append(hex);
			if (i == bytes.length - 1) {
				break;
			}
			result.append(" ");
		}
		return result.toString();
	}
}
