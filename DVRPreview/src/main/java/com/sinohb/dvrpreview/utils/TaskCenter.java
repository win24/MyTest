package com.sinohb.dvrpreview.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created AY 2019-6-8
 */
public class TaskCenter {
	private static TaskCenter instance;
	private static final String TAG = "TaskCenter";
	//    Socket
	private Socket socket;
	//    IP地址
	private String ipAddress = "192.168.1.5";
	//    端口号
	private int port = 8555;
	private Thread thread;
	//    Socket输出流
	private OutputStream outputStream;
	//    Socket输入流
	private InputStream inputStream;
	//    连接回调
	private OnServerConnectedCallbackBlock connectedCallback;
	//    断开连接回调(连接失败)
	private OnServerDisconnectedCallbackBlock disconnectedCallback;
	//    接收信息回调
	private OnReceiveCallbackBlock receivedCallback;
	//    构造函数私有化
	private TaskCenter() {
		super();
	}
	//    提供一个全局的静态方法
	public static TaskCenter sharedCenter() {
		if (instance == null) {
			synchronized (TaskCenter.class) {
				if (instance == null) {
					instance = new TaskCenter();
				}
			}
		}
		return instance;
	}
	/**
	 * 通过IP地址(域名)和端口进行连接
	 *
	 * @param ipAddress  IP地址(域名)
	 * @param port       端口
	 */
	public void connect(final String ipAddress, final int port) {
		LogUtils.d(TAG,"connect"+",ipAddress="+ipAddress+",port="+port+",isConnected()"+isConnected());

//		this.ipAddress = ipAddress;
//		this.port = port;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					socket = new Socket(ipAddress, port);
                    socket.setSoTimeout ( 5 * 1000 );//设置超时时间
					if (isConnected()) {
						TaskCenter.sharedCenter().ipAddress = ipAddress;
						TaskCenter.sharedCenter().port = port;
						if (connectedCallback != null) {
							connectedCallback.callback();
						}
						outputStream = socket.getOutputStream();
						inputStream = socket.getInputStream();
						LogUtils.i(TAG,"--连接成功");
						receive();

					}else {
						LogUtils.i(TAG,"连接失败");
						if (disconnectedCallback != null) {
							disconnectedCallback.callback(new IOException("连接失败"));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					LogUtils.e(TAG,"连接异常");
					if (disconnectedCallback != null) {
						disconnectedCallback.callback(e);
					}
				}
			}
		});
		thread.start();
	}
	/**
	 * 判断是否连接
	 */
	public  boolean isConnected() {
		if(socket != null){
//			LogUtils.d(TAG,"socket.isConnected()="+socket.isConnected()+",socket.close()"+socket.isClosed());
			LogUtils.d(TAG,"isConnected(1)="+!socket.isClosed());
//			return socket.isConnected();
			return !socket.isClosed();

		}
		LogUtils.d(TAG,"isConnected(2)=false");
		return false;
	}
	/**
	 * 连接
	 */
	public void connect() {
		connect(ipAddress,port);
	}
	/**
	 * 断开连接
	 */
	public void disconnect() {
		LogUtils.d(TAG,"disconnect()");
		if (isConnected()) {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				socket.close();
				if (socket.isClosed()) {
					if (disconnectedCallback != null) {
						disconnectedCallback.callback(new IOException("断开连接"));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 接收数据
	 */
	public  void receive() {
		while (isConnected()) {
			try {
				/**得到的是16进制数，需要进行解析*/
				byte[] bt = new byte[1024];
//                获取接收到的字节和字节数
				int length = inputStream.read(bt);
//                获取正确的字节
				LogUtils.d(TAG,"length="+length);
				if(length>0){
					byte[] bs = new byte[length];
					System.arraycopy(bt, 0, bs, 0, length);
//				LogUtils.d(TAG,byteToHexStr(bs));

//				String str = new String(bs, "UTF-8");
					if (bs != null) {
						if (receivedCallback != null) {
							receivedCallback.callback(bs);
						}
					}
					LogUtils.i(TAG,"接收成功");
				}else{
					LogUtils.i(TAG,"网络断开");
					disconnect();
					return;
				}
//				if (str != null) {
//					if (receivedCallback != null) {
//						receivedCallback.callback(str);
//					}
//				}

			} catch (IOException e) {
//				LogUtils.i(TAG,"接收失败");
			}
		}
	}
	/**
	 * 发送数据
	 *
	 * @param data  数据
	 */
	public void send(final byte[] data) {
		LogUtils.d(TAG,"send(data)="+byteToHexStr(data));
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (isConnected()) {
					try {
						outputStream.write(data);
						outputStream.flush();
						LogUtils.i(TAG,"发送成功");
					} catch (IOException e) {
						e.printStackTrace();
						LogUtils.i(TAG,"发送失败");
					}
				} else {
					connect();
				}
			}
		}).start();

	}
	/**
	 * 回调声明
	 */
	public interface OnServerConnectedCallbackBlock {
		void callback();
	}
	public interface OnServerDisconnectedCallbackBlock {
		void callback(IOException e);
	}
	public interface OnReceiveCallbackBlock {
		void callback(byte[] receicedMessage);
	}

	public void setConnectedCallback(OnServerConnectedCallbackBlock connectedCallback) {
		this.connectedCallback = connectedCallback;
	}

	public void setDisconnectedCallback(OnServerDisconnectedCallbackBlock disconnectedCallback) {
		this.disconnectedCallback = disconnectedCallback;
	}

	public void setReceivedCallback(OnReceiveCallbackBlock receivedCallback) {
		this.receivedCallback = receivedCallback;
	}
	/**
	 * 移除回调
	 */
	private void removeCallback() {
		connectedCallback = null;
		disconnectedCallback = null;
		receivedCallback = null;
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

