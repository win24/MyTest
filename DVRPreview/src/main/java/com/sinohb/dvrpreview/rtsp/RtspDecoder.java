package com.sinohb.dvrpreview.rtsp;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import com.sinohb.dvrpreview.utils.LogUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.MAX_PRIORITY;

public class RtspDecoder {
    private static final String TAG = RtspDecoder.class.getSimpleName();
    //处理音视频的编解码的类MediaCodec
    private MediaCodec video_decoder;
    //显示画面的Surface
    private Surface surface;
    // 0: live, 1: playback, 2: local file
    private int state = 0;
    //视频数据
    private BlockingQueue<byte[]> video_data_Queue = new ArrayBlockingQueue<byte[]>(65536);
    //音频数据
    private BlockingQueue<byte[]> audio_data_Queue = new ArrayBlockingQueue<byte[]>(65536);

    private boolean isReady = false;
    private int fps = 0;

    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
    private int frameCount = 0;
    private long deltaTime = 0;
    private long counterTime = System.currentTimeMillis();
    //    private boolean isRuning = false;
    private boolean isRuning = true;

    private Thread runDecoderThread;
//    private Thread runGuardThread;

    private static final int MSG_OPEN_GUARDTHREAD = 0x01;
    private static final int MSG_OPEN_DECODER_THREAD= 0x02;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_OPEN_GUARDTHREAD:
                    handler.sendEmptyMessageDelayed(MSG_OPEN_GUARDTHREAD,3000);
                    LogUtils.d(TAG,"MSG_OPEN_GUARDTHREAD--runDecoderThread="+runDecoderThread);
                    if(!runDecoderThread.isAlive()){
                        handler.removeMessages(MSG_OPEN_DECODER_THREAD);
                        handler.sendEmptyMessageDelayed(MSG_OPEN_DECODER_THREAD,1000);
                    }
                    break;

                case MSG_OPEN_DECODER_THREAD:
                    LogUtils.d(TAG,"MSG_OPEN_DECODER_THREAD");
                    runDecoderThread = null;
                    Thread t = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                initial();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                    break;

            }
        }
    };

    public RtspDecoder(Surface surface, int playerState) {
        this.surface = surface;
        this.state = playerState;

    }

    public void stopRunning() {
        LogUtils.d(TAG,"----stopRunning()");
        video_data_Queue.clear();
        audio_data_Queue.clear();
        stopThread();
        handler.removeMessages(MSG_OPEN_GUARDTHREAD);
        handler.removeMessages(MSG_OPEN_DECODER_THREAD);
        handler.removeCallbacksAndMessages(null);

    }

    public synchronized void stopThread() {
        LogUtils.d(TAG,"----stopThread()");
        isRuning = false;
    }

    public boolean isRunning() {
        return isRuning;
    }

    //添加视频数据
    public void setVideoData(byte[] data) {
        try {
            video_data_Queue.put(data);
            LogUtils.d(TAG,"----setVideoData(length)="+data.length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //添加音频数据
    public void setAudioData(byte[] data) {
        try {
            audio_data_Queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public int getFPS() {
        return fps;
    }


    public void initial() throws IOException {
        LogUtils.d(TAG,":initial()");
        MediaFormat format = null;
        format = MediaFormat.createVideoFormat("video/avc",1280,720);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

        byte[] header_sps = {0, 0, 0, 1, 103, 66, 0, 42, (byte) 149, (byte) 168, 30, 0, (byte) 137, (byte) 249, 102, (byte) 224, 32, 32, 32, 64};
        byte[] header_pps = {0, 0, 0, 1, 104, (byte) 206, 60, (byte) 128, 0, 0, 0, 1, 6, (byte) 229, 1, (byte) 151, (byte) 128};

        format.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
        format.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));

        if (video_decoder != null) {
            try {
                video_decoder.stop();
                video_decoder.release();
                video_decoder = null;
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        video_decoder = MediaCodec.createDecoderByType("video/avc");
        if (video_decoder == null) {
            return;
        }

        video_decoder.configure(format, surface, null, 0);
        video_decoder.start();
        inputBuffers = video_decoder.getInputBuffers();
        outputBuffers = video_decoder.getOutputBuffers();
        frameCount = 0;
        deltaTime = 0;
        isRuning = true;
        handler.removeMessages(MSG_OPEN_GUARDTHREAD);
        handler.sendEmptyMessageDelayed(MSG_OPEN_GUARDTHREAD,1000);
        runDecodeVideoThread();
    }

    /**
     * @description 解码视频流数据
     */
    private void runDecodeVideoThread() {
        LogUtils.d(TAG,"runDecodeVideoThread()");
        if(runDecoderThread != null){
            runDecoderThread = null;
        }
        runDecoderThread= new Thread() {

            @SuppressLint("NewApi")
            public void run() {

                while (isRunning()) {
//                while (isRuning) {
                    /*设置解码等待时间，0为不等待，-1为一直等待*/
                    int inIndex = 0;
//                    int inIndex = -1;
                    try {
                        //1 准备填充器/
                        inIndex = video_decoder.dequeueInputBuffer(-1);
//                        inIndex = video_decoder.dequeueInputBuffer(0);
                    } catch (Exception e) {
                        LogUtils.d(TAG,"inIndex="+inIndex);
                        return;
                    }
                    try {

                        if (inIndex >= 0) {
                            ByteBuffer buffer = inputBuffers[inIndex];
                            buffer.clear();
                            if (!video_data_Queue.isEmpty()) {
                                byte[] data;
                                /*取走BlockingQueue里排在首位的对象,若BlockingQueue为空,阻断进入等待状态直到BlockingQueue有新的数据被加入*/
                                data = video_data_Queue.take();
                                buffer.put(data);
                                if (state == 0) {
                                    /*把数据传给解码器*/
                                    video_decoder.queueInputBuffer(inIndex, 0, data.length, 40, 0);
//                                    video_decoder.queueInputBuffer(inIndex, 0, data.length, 66, 0);
                                } else {
                                    video_decoder.queueInputBuffer(inIndex, 0, data.length, 20, 0);
//                                    video_decoder.queueInputBuffer(inIndex, 0, data.length, 33, 0);
                                }
                            } else {
                                if (state == 0) {
                                    video_decoder.queueInputBuffer(inIndex, 0, 0, 40, 0);
//                                    video_decoder.queueInputBuffer(inIndex, 0, 0, 66, 0);
                                } else {
                                    video_decoder.queueInputBuffer(inIndex, 0, 0, 20, 0);
//                                    video_decoder.queueInputBuffer(inIndex, 0, 0, 33, 0);
                                }
                            }
                        } else {
                            LogUtils.d(TAG,"BUFFER_FLAG_END_OF_STREAM");
                            video_decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        }
                        /*开始解码,解码数据到surface*/
                        int outIndex = video_decoder.dequeueOutputBuffer(info, 0);
                        boolean doRender = (info.size != 0);
                        while (outIndex >= 0){
                            LogUtils.d(TAG,"Thread--ID="+Thread.currentThread().getId());
                            /*调用这个api之后，SurfaceView才有图像*/
                            video_decoder.releaseOutputBuffer(outIndex, doRender);
                            frameCount++;
                            deltaTime = System.currentTimeMillis() - counterTime;
                            if (deltaTime > 1000) {
                                fps = (int) (((float) frameCount / (float) deltaTime) * 1000);
                                LogUtils.d(TAG,"fps="+fps+",frameCount="+frameCount+",deltaTime="+deltaTime);
                                counterTime = System.currentTimeMillis();
                                frameCount = 0;
                            }
                        }

                        //所有流数据解码完成，可以进行关闭等操作
                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            LogUtils.e(TAG, "(所有流数据解码完成)BUFFER_FLAG_END_OF_STREAM");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        };
        runDecoderThread.setPriority(MAX_PRIORITY);
        runDecoderThread.start();

    }
}