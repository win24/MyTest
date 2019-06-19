package com.huabao.mytest.utils;

import android.util.Log;

//import com.sinohb.coreservice.state.MCUState;

import java.io.FileInputStream;
import java.io.IOException;

//import sinohb.core.mcu.UartDataHandler;

public class ByteTools {


    /**
     * print log
     *
     * @param b
     */
    public static void printHexString(String Tag, byte[] b) {
        String logout = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            logout = logout + " " + hex.toUpperCase();
        }
        Log.i("MCU", Tag + " Data:" + logout);
    }


    public static void printHexString(byte[] b, int size) {
        String logout = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            logout = logout + " " + hex.toUpperCase();
        }
//        L.i("MCU", "Data:" + logout);
    }


    /**
     * 两位byte转int值
     *
     * @param high
     * @param low
     * @return the frame length
     */
    public static int getSizeFromBytes(byte high, byte low) {
        int tmp_high = high & 0xFF;
        int tmp_low = low & 0xFF;
        tmp_high = tmp_high << 8;
        return (tmp_high + tmp_low);
    }

    /**
     * byte 数组转为int值
     *
     * @param data
     * @return
     */
    public static int getSizeFromBytes(byte[] data) {
        int size = 0;
        for (int i = 0; i < data.length; i++) {
            int tmp = data[i] & 0xFF;
            int drift = (data.length - 1 - i) * 8;
            tmp = tmp << drift;
            size += tmp;
        }
        return size;
    }

    /**
     * int 转两位长度byte[]
     *
     * @param value
     * @return
     */
    public static byte[] get2BytesFromInt(int value) {
        byte[] b = new byte[2];
        b[0] = (byte) (value >> 8 & 0xff);
        b[1] = (byte) (value & 0xff);
        return b;
    }

    /**
     * int 转三位长度byte[]
     *
     * @param value
     * @return
     */
    public static byte[] get3BytesFromInt(int value) {
        byte[] b = new byte[3];
        b[0] = (byte) (value >> 16 & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value & 0xff);
        //printHexString(b,"Test");
        return b;
    }

    /**
     * int 转四位长度byte[]
     *
     * @param value
     * @return
     */
    public static byte[] get4BytesFromInt(int value) {
        byte[] b = new byte[4];
        b[1] = (byte) (value >> 24 & 0xff);
        b[1] = (byte) (value >> 16 & 0xff);
        b[2] = (byte) (value >> 8 & 0xff);
        b[3] = (byte) (value & 0xff);
        //printHexString(b,"Test");
        return b;
    }

    /**
     * 获取校验码
     *
     * @param data
     * @param from
     * @return
     */
    public static int getCheckSunFromBytes(byte[] data, int from, int to) {
        int checksum = 0x00;
        for (int i = from; i < to; i++) {
            checksum ^= data[i] & 0xFF;
        }
        return checksum;
    }

    /**
     * int 转8位byte数组，代表开关状态
     *
     * @param b
     * @return
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i > 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }


    public static byte[] combine2Bytes(byte[] front, byte[] back) {
        byte[] bt3 = new byte[front.length + back.length];
        System.arraycopy(front, 0, bt3, 0, front.length);
        System.arraycopy(back, 0, bt3, front.length, back.length);
        return bt3;
    }


    /**
     * 读取MCU升级文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] readFileSdcardFile(String path) throws IOException {
        try {
            if (path != null) {
                FileInputStream fin = new FileInputStream(path);
                int length = fin.available();
                Log.i("MCU", "Total Length:" + length);
                byte[] buffer = new byte[length];
                fin.read(buffer);
                fin.close();
                return buffer;
            }
        } catch (Exception e) {
            Log.d("MCU", "read file exception");
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readMcuVersion(String path) throws IOException {
        try {
            if (path != null) {
                FileInputStream fin = new FileInputStream(path);
                byte[] temp = new byte[256];
                fin.read(temp);
                byte[] buffer = new byte[32];
                fin.read(buffer);
                //test
                // printHexString(buffer, "MCU Version:");
                int length = 0;
                for (int i = buffer.length - 1; i > 0; i--) {
                    if (buffer[i] != 0) {
                        length = i + 1;
                        break;
                    }
                }
                fin.close();
                if (length == 0) {
                    return null;
                } else {
                    byte[] result = new byte[length];
                    System.arraycopy(buffer, 0, result, 0, length);
                    //  printHexString(result, "MCU Version:");
                    return result;
                }
            }
        } catch (Exception e) {
            Log.d("MCU", "read file exception");
            e.printStackTrace();
        }
        return null;
    }

//    public synchronized  static void  composWriteData(byte groupID, byte commandID, int totalframe, int curframe, byte[] datas) {
//        int length = datas.length + 9;
//        if(MCUState.isUpgrade&&groupID!=GroupID.GROUPID_UPGRADE){
//            return;
//        }
//        if (length < 65535) {
//            byte[] comDatas = new byte[length];
//            comDatas[0] = FrameInfos.HEAD_ONE;
//            comDatas[1] = FrameInfos.HEAD_TWO;
//            comDatas[2] = groupID;
//            comDatas[3] = commandID;
//            byte[] tmpLength = ByteTools.get2BytesFromInt(length);
//            comDatas[4] = tmpLength[0];
//            comDatas[5] = tmpLength[1];
//            comDatas[6] = (byte) totalframe;
//            comDatas[7] = (byte) curframe;
//            System.arraycopy(datas, 0, comDatas, 8, datas.length);
//            int checksun = ByteTools.getCheckSunFromBytes(comDatas, 2, length - 1);
//            comDatas[length - 1] = (byte) checksun;
//            UartDataHandler.getInstance().sendComm(comDatas);
//        }
//    }
}
