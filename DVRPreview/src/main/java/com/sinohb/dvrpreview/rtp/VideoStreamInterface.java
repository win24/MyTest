package com.sinohb.dvrpreview.rtp;

public interface VideoStreamInterface {
    void onVideoStream(byte[] var1);
    void releaseResource();
}