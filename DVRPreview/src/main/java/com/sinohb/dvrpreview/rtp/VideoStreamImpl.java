package com.sinohb.dvrpreview.rtp;

import android.util.Log;

import com.sinohb.dvrpreview.utils.LogUtils;

import org.apache.mina.core.buffer.IoBuffer;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoStreamImpl implements Runnable, VideoStreamInterface {
	private static final String TAG = VideoStreamImpl.class.getCanonicalName();
	private IoBuffer buffer;
	private ConcurrentLinkedQueue<byte[]> streams;
	private IoBuffer frameBuffer;
	private AtomicBoolean status = new AtomicBoolean(false);
	private H264StreamInterface rawStream;

	private int oldL = 0;
	private int newL = 0;


	public VideoStreamImpl(H264StreamInterface rawStream) {
		this.rawStream = rawStream;
		this.buffer = IoBuffer.allocate(65536).setAutoExpand(true).setAutoShrink(true);
		this.frameBuffer = IoBuffer.allocate(65536).setAutoExpand(true).setAutoShrink(true);
//		this.buffer = IoBuffer.allocate(32768).setAutoExpand(true).setAutoShrink(true);
//		this.frameBuffer = IoBuffer.allocate(32768).setAutoExpand(true).setAutoShrink(true);
		this.streams = new ConcurrentLinkedQueue();
		this.status.set(true);
		(new Thread(this)).start();
	}

	public void run() {
		while(this.status.get()) {
			try {
				synchronized(this) {
					this.wait(45L);
//					this.wait(25L);
				}
				this.unpackRtp();
			} catch (Exception var4) {
				var4.printStackTrace();
			}
		}

	}

	private void unpackRtp() {
		if(!this.streams.isEmpty()) {
			while(!this.streams.isEmpty()) {
				this.buffer.put((byte[])this.streams.poll());
			}
		}this.buffer.flip();/*通过flip()方法将Buffer从写模式切换到读模式。在读模式下，可以读取之前写入到buffer的所有数据。*/
		/*丢包，断包处理*/
		/*有包头，包头足够*/
		if(this.buffer.remaining() >= 4) {
			boolean next;
			do {
				next = false;
				int p = this.buffer.position();/*位置，下一个要被读或写的元素的索引，每次读写缓冲区数据时都会改变改值，为下次读写作准备*/
				byte hflag = this.buffer.get();/*相对读，从position位置读取一个byte，并将position+1，为下次读写作准备*/
				if(hflag == 36) {
					this.buffer.get();
                    short len = this.buffer.getShort();
//					int len =  ((int)this.buffer.getShort() & 0xffff);
					next = len <= this.buffer.remaining();
//					LogUtils.d(TAG,"this.buffer.remaining()"+this.buffer.remaining()+",len="+len);
					if(next) {
						byte[] cc = new byte[len];
						this.buffer.get(cc);
						IoBuffer frame = IoBuffer.wrap(cc);
						byte rpth2 = frame.get(1);
						short seq = frame.getShort(2);/*CSRC计数器，占4位，指示CSRC 标识符的个数*/
						boolean m = (rpth2 & 128) == 128; /*Marker标记，占1位，不同的有效载荷有不同的含义，对于视频，标记一帧的结束*/
						LogUtils.i(TAG,"L-o-o-p,seq:[" + seq + "],end:[" + m + "]");
						/*拆包和解包
	                    拆包：当编码器在编码时需要将原有一个NAL按照FU-A进行分片，原有的NAL的单元头与分片后的FU-A的单元头有如下关系：
	                    原始的NAL头的前三位为FU indicator的前三位，原始的NAL头的后五位为FU header的后五位，
	                    FU indicator与FU header的剩余位数根据实际情况决定。
	                    解包：当接收端收到FU-A的分片数据，需要将所有的分片包组合还原成原始的NAl包时，FU-A的单元头与还原后的NAL的关系如下：
	                    还原后的NAL头的八位是由FU indicator的前三位加FU header的后五位组成，即：
	                    nal_unit_type = (fu_indicator & 0xe0) | (fu_header & 0x1f)*/
						frame.position(12);/*位置重置为*/
						byte h1 = frame.get();
						byte h2 = frame.get();
						byte nal = (byte)(h1 & 31);/*获取FU indicator的类型域*/
						int flag = h2 & 224;/*获取FU header的前三位，判断当前是分包的开始、中间或结束*/
						byte nal_fua = (byte)(h1 & 224 | h2 & 31);/*判断是否为I帧的算法为*/

						/*一个rtp包携带了一帧数据(single)
						多个rtp包携带了一帧数据(FU-A)
						一个rtp包携带了多帧数据(STAP-A)*/
						if(nal == 28) {/*判断NAL的类型为0x1c=28，说明是FU-A分片*/
							frame.position(14);
							if(flag == 128) {/*开始*/
								this.frameBuffer.putInt(1);
								this.frameBuffer.put(nal_fua);
								this.frameBuffer.put(frame);
							} else if(flag == 64) {/*结束*/
								this.frameBuffer.put(frame);
							} else {/*中间*/
								this.frameBuffer.put(frame);
							}
						} else {/*单包数据*/
							frame.position(12);
							this.frameBuffer.putInt(1);
							this.frameBuffer.put(frame);
						}

						if(m) {
							this.frameBuffer.flip();
							byte[] newFrame = new byte[this.frameBuffer.remaining()];
							this.frameBuffer.get(newFrame);
							this.rawStream.process(newFrame);
							this.frameBuffer.clear();
						}
					} else {
						this.buffer.position(p);
					}
				}
			} while(next && this.buffer.remaining() >= 4);


			this.buffer.compact();/*compact()方法只会清除已经读过的数据*/
		}

	}

	public void onVideoStream(byte[] stream) {
			this.streams.add(stream);
			this.waiteUp();

//		newL = stream.length;
//		if(stream.length>100){
//			this.streams.add(stream);
//			this.waiteUp();
//			oldL = newL;
//		}else {
//			if(oldL>100){
//				this.streams.add(stream);
//				this.waiteUp();
//			}else{
//				LogUtils.d(TAG,"onVideoStream(stream.length<=100)");
//			}
//		}
	}

	private synchronized void waiteUp() {
		this.notifyAll();
	}

	public void releaseResource() {
		if(this.status.compareAndSet(true, false)) {
			this.streams.clear();
			this.waiteUp();
		}

	}
}
