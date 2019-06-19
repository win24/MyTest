package com.huabao.mytest.media;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Mount {

    public static String getUdiskPath() {
        Log.d("MyTest", "getUdiskPath()");
        String upath = "";
        try {
            Runtime runtime = Runtime.getRuntime();
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                Log.d("MyTest", line);
                // 将常见的linux分区过滤掉
                // SdList.add(line);
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                // 下面这些分区是我们需要的
                if (line.contains("vfat") || line.contains("fuse")
                        || line.contains("fat") || (line.contains("ntfs"))) {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");
                    if (items != null && items.length > 1) {
                        String path = items[2].toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        if (path != null && path.contains("media_rw")) {
                            upath = path;
                            Log.d("MyTest", "media_rw path is  " + path);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return upath;
    }

    public static void getStorageInfo(Context context) {

        Log.d("MyTest", "getStorageInfo()");
        HashSet<String> mPathSet = new HashSet<>();
        mPathSet.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();
            for(StorageVolume volume : storageVolumeList){
                Log.d("MyTest", "MusicPlayServiceManager mounted volume path " + volume.toString()
                        + "\n" + volume.getDescription(context) + ", " + volume.getUuid());
                if(!TextUtils.isEmpty(volume.getUuid())){
                    mPathSet.add("/storage/" + volume.getUuid());
                }
            }
        }else{
            StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class<?> storageVolumeClazz = null;
            try {
                storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
                Method getPath = storageVolumeClazz.getMethod("getPath");
                Object result = getVolumeList.invoke(mStorageManager);
                final int length = Array.getLength(result);
                Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");
                for (int i = 0; i < length; i++) {
                    Object storageVolumeElement = Array.get(result, i);
                    String userLabel = (String) getUserLabel.invoke(storageVolumeElement);
                    String path = (String) getPath.invoke(storageVolumeElement);
                    if(!path.contains(Environment.getExternalStorageDirectory().getPath())){
                        mPathSet.add(path);
                    }
                    Log.d("MyTest", "mounted volume path  is : " + path
                            + "\nuserLabel is " + userLabel);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void getStorageVolume(Context context){
        Log.d("MyTest", "getStorageVolume()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();
            try {
                for (StorageVolume volume : storageVolumeList) {
                    Log.d("MyTest", "mounted volume path " + volume.toString()
                            + "\n" + volume.getDescription(context) + ", " + volume.getUuid());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    public static boolean isUsbExist(Context context) {
        Log.d("MyTest", "isUsbExist()");
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        StringBuilder sb = new StringBuilder();
        while (deviceIterator.hasNext()) {
            UsbDevice usbDevice = deviceIterator.next();
            sb.append("DeviceName=" + usbDevice.getDeviceName() + "\n");
            sb.append("DeviceId=" + usbDevice.getDeviceId() + "\n");
            sb.append("VendorId=" + usbDevice.getVendorId() + "\n");
            sb.append("ProductId=" + usbDevice.getProductId() + "\n");
            sb.append("DeviceClass=" + usbDevice.getDeviceClass() + "\n");
            int deviceClass = usbDevice.getDeviceClass();
            if (deviceClass == 0) {
                UsbInterface anInterface = usbDevice.getInterface(0);
                int interfaceClass = anInterface.getInterfaceClass();

                sb.append("deviceClass为0-------------\n");
                sb.append("Interface.describeContents()=" + anInterface.describeContents() + "\n");
                sb.append("Interface.getEndpointCount()=" + anInterface.getEndpointCount() + "\n");
                sb.append("Interface.getId()=" + anInterface.getId() + "\n");
                //http://blog.csdn.net/u013686019/article/details/50409421
                //http://www.usb.org/developers/defined_class/#BaseClassFFh
                //通过下面的InterfaceClass来判断到底是哪一种的，例如7就是打印机，8就是usb的U盘
                sb.append("Interface.getInterfaceClass()=" + anInterface.getInterfaceClass() + "\n");
                if (anInterface.getInterfaceClass() == 7) {
                    sb.append("此设备是打印机\n");
                } else if (anInterface.getInterfaceClass() == 8) {
                    sb.append("此设备是U盘\n");
                    Log.d("check_usb", "usb : true");
//                    return true;
                }
                sb.append("anInterface.getInterfaceProtocol()=" + anInterface.getInterfaceProtocol() + "\n");
                sb.append("anInterface.getInterfaceSubclass()=" + anInterface.getInterfaceSubclass() + "\n");
                sb.append("deviceClass为0------end-------\n");
            }

            sb.append("DeviceProtocol=" + usbDevice.getDeviceProtocol() + "\n");
            sb.append("DeviceSubclass=" + usbDevice.getDeviceSubclass() + "\n");
            sb.append("+++++++++++++++++++++++++++\n");
            sb.append("\n");

            Log.d("MyTest", "[ConnectyUtil] check_usb usb : " + sb.toString());
        }
        return false;
    }

}