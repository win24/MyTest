package com.huabao.mytest.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * @author Evan_zch
 * @date 2018/9/17 19:13
 * <p>
 * 存储设备管理类
 */
public class StorageManagerUtils {

    private static final String TAG = "StorageManagerUtils";
//    private final StorageManager mStorageManager;
//    private static long totalBytes = 0;
//    private static long usedBytes = 0;

    private static final class StorageManagerHolder {
        private static final StorageManagerUtils INSTANCE = new StorageManagerUtils();
    }

    public static StorageManagerUtils getInstance() {
        return StorageManagerHolder.INSTANCE;
    }

//    private StorageManagerUtils() {
//        mStorageManager = SApplication.getInstance().getSystemService(StorageManager.class);
//    }
//
//    public List<VolumeInfo> getStorageDeviceList() {
//        if (mStorageManager == null) {
//            throw new RuntimeException("StorageManagerUtils not init");
//        }
//        List<VolumeInfo> volumes = mStorageManager.getVolumes();
//        List<VolumeInfo> publicVolumes = new ArrayList<>();
//        publicVolumes.clear();
//        for (VolumeInfo info : volumes) {
//            int type = info.getType();
//            if (info.getType() == VolumeInfo.TYPE_PUBLIC) {
//                Logutils.d(TAG + "--refresh  type is public");
//                String bestVolumeDescription = mStorageManager.getBestVolumeDescription(info);
//                File path = info.getPath();
//                Logutils.d(TAG + "--refresh  type=" + type + ",bestVolumeDescription=" + bestVolumeDescription + ",path=" + path);
//                publicVolumes.add(info);
//            }
//        }
//        return publicVolumes;
//    }
//
//    public boolean isMounted(VolumeInfo vol, int oldState, int newState) {
//        return (isInteresting(vol) && oldState != newState && newState == VolumeInfo.STATE_MOUNTED);
//    }
//
//    private static boolean isInteresting(VolumeInfo vol) {
//        switch (vol.getType()) {
//            //case VolumeInfo.TYPE_PRIVATE:
//            case VolumeInfo.TYPE_PUBLIC:
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    public String getTotalSize(VolumeInfo vol) {
//        if (vol.isMountedReadable()) {
//            final File path = vol.getPath();
//            if (totalBytes <= 0) {
//                totalBytes = path.getTotalSpace();
//            }
//        }
//        return Formatter.formatFileSize(SApplication.getInstance(), totalBytes);
//    }
//
//    public String getUsedSize(VolumeInfo vol) {
//        if (vol.isMountedReadable()) {
//            final File path = vol.getPath();
//            final long freeBytes = path.getFreeSpace();
//            usedBytes = totalBytes - freeBytes;
//        }
//        return Formatter.formatFileSize(SApplication.getInstance(), usedBytes);
//    }

    public void startListen(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.setPriority(1000);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentFilter.addDataScheme("file");
        context.registerReceiver(broadcastRec, intentFilter);
    }

    private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String path = intent.getData().getPath();
            Log.d("MediaAction", "data path " + path);
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                Log.d("MediaAction", "action eject");
            } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                Log.d("MediaAction", "action mount");
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                Log.d("MediaAction", "action unmount");
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                Log.d("MediaAction", "action removed");
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
                Log.d("MediaAction", "action scanner started");
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                Log.d("MediaAction", "action scanner finished");
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)) {
                Log.d("MediaAction", "action scan file");
            } else if (action.equals(Intent.ACTION_MEDIA_SHARED)) {
                Log.d("MediaAction", "action shared");
            } else if (action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
                Log.d("MediaAction", "action bad removal");
            } else if (action.equals(Intent.ACTION_MEDIA_CHECKING)) {
                Log.d("MediaAction", "action checking");
            } else if (action.equals(Intent.ACTION_MEDIA_NOFS)) {
                Log.d("MediaAction", "action nofs");
            } else if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
                Log.d("MediaAction", "action button");
            } else {
                Log.d("MediaAction", action);
            }
        }
    };

    public void stopListen(Context context) {
        context.unregisterReceiver(broadcastRec);
    }

}