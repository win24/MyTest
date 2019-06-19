package com.huabao.mytest.bean;

import android.support.annotation.NonNull;

/**
 * Created by HB-YFTongYH on 2018-10-17.
 */

public class FreqBean implements Comparable<FreqBean> {

    private int freq;
    private boolean isPressed;
    private int channelType;//am、fm
    private int type;//收藏1，频段2
    private boolean isCollect;
    public FreqBean(){

      }
//    public FreqBean(int channelType, int type, int freq,boolean isCollect,boolean isPressed) {
//        this.channelType = channelType;
//        this.type = type;
//        this.freq = freq;
//        this.isPressed = isPressed;
//        this.isCollect = isCollect;
//    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    @Override
    public int compareTo(@NonNull FreqBean another) {
        if (this.freq > another.freq) {
            return 1;
        } else if (this.freq == another.freq) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof FreqBean) {
            FreqBean other = (FreqBean) o;
            return this.freq == other.freq && this.channelType == other.channelType && this.type == other.type;
        } else {
            return false;
        }
    }

}
