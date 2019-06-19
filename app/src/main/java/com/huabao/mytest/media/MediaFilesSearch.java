package com.huabao.mytest.media;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 媒体文件检索
 *
 */
public class MediaFilesSearch {

    private final static String TAG = "MediaFilesSearch";




    /**
     * 检索视频列表
     *
     */
    public static void getAllVideoFiles(Context mContext) {
        //首先检错SDcard上所有的video
        //cursor = this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, MediaStore.Video.Media.DATE_ADDED + " desc");
            while (cursor.moveToNext()) {

                if (cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)) != 0) {
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    String createTime = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    //获取当前Video对应的ID, 然后根据该ID获取缩略图
//                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
//                    String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
//                    String[] selectionArgs = new String[]{
//                            id + ""
//                    };
//                    ContentResolver crThumb = mContext.getContentResolver();
//                    BitmapFactory.Options options=new BitmapFactory.Options();
//                    options.inSampleSize = 1;
//                    Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检索音频列表
     *
     */
    public static void getAllAudioFiles(Context mContext) {
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, MediaStore.Video.Media.DATE_ADDED + " desc");
            while (cursor.moveToNext()) {
                if (cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) != 0) {
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String createTime = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检索图片列表
     *
     */
    public static void getAllPictureFiles(Context mContext) {
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, MediaStore.Video.Media.DATE_ADDED + " desc");
            while (cursor.moveToNext()) {
                //if (cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DURATION)) != 0) {
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String time = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                //}
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新媒体库
     */
    private void updataMedia(Context context, String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//版本号的判断  4.4为分水岭，发送广播更新媒体库
            MediaScannerConnection.scanFile(context, new String[]{path}, null
                    , new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d(TAG, "文件：" + path + "已更新");
                        }
                    });
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(new File(path))));
        }

    }

    /**
     * 检索视频文件信息
     *
     */
    public static String[] getVideoInfo(String path) {
        String[] info = new String[4];
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(path);
        info[0] = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
        info[1] = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
        info[2] = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
        info[3] = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE); // 视频帧率
        retr.release();

        return info;
    }

    /**
     * 检索视频列表
     * @param path 检索路径
     */
    public static void getVideoFilesList(String path) {

        File file = new File(path);
        if (file == null) {
            Log.d(TAG, "The file (" + file + ") is not exist!");
            return;
        }
        File[] files = file.listFiles();
        if (files == null) {
            Log.d(TAG, "The files under dir(" + file.getAbsolutePath() + ") is not null!");
            return;
        }
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            fileList.add(files[i]);
        }
        //Collections.sort(fileList, new FileComparatorName());
        Collections.sort(fileList, new FileComparatorTime());
        //Collections.sort(fileList, new FileComparatorSize());
        Set<String> dateSet = new HashSet<String>();
        for (int i = 0; i < fileList.size(); i++) {
            String filePath = fileList.get(i).getAbsolutePath();
            String lowPath = filePath.toLowerCase();
            if (lowPath.endsWith(".mp4") || lowPath.endsWith(".h264")
                    || lowPath.endsWith(".asf")) {


            }
        }
    }

    /**
     * 检索音频列表
     * @param path 检索路径
     */
    public static void getAudioFilesList(String path) {

        File file = new File(path);
        if (file == null) {
            Log.d(TAG, "The file (" + file + ") is not exist!");
            return ;
        }
        File[] files = file.listFiles();
        if (files == null) {
            Log.d(TAG, "The files under dir(" + file.getAbsolutePath() + ") is not null!");
            return ;
        }
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            fileList.add(files[i]);
        }
        Collections.sort(fileList, new FileComparatorTime());
        Set<String> dateSet = new HashSet<String>();
        for (int i = 0; i < fileList.size(); i++) {
            String filePath = fileList.get(i).getAbsolutePath();
            String lowPath = filePath.toLowerCase();
            if (lowPath.endsWith(".wav") || lowPath.endsWith(".aac")
                    || lowPath.endsWith(".amr")) {


            }
        }
    }

    /**
     * 检索图片列表
     * @param path 检索路径
     */
    public static void getPictureFilesList(String path) {

        File file = new File(path);
        if (file == null) {
            Log.d(TAG, "The file (" + file + ") is not exist!");
            return ;
        }
        File[] files = file.listFiles();
        if (files == null) {
            Log.d(TAG, "The files under dir(" + file.getAbsolutePath() + ") is not null!");
            return ;
        }
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            fileList.add(files[i]);
        }
        Collections.sort(fileList, new FileComparatorTime());
        Set<String> dateSet = new HashSet<String>();
        for (int i = 0; i < fileList.size(); i++) {
            String filePath = fileList.get(i).getAbsolutePath();
            String lowPath = filePath.toLowerCase();
            if (lowPath.endsWith(".jpg") || lowPath.endsWith(".png")
                    || lowPath.endsWith(".jpeg")) {


            }
        }
    }

    /**
     * 检索最新日志的文件路径
     * @param path 检索路径
     */
    public static String getLastLogFilePath(String path) {

        File file = new File(path);
        if (file == null) {
            Log.d(TAG, "The file (" + file + ") is not exist!");
            return null;
        }
        File[] files = file.listFiles();
        if (files == null) {
            Log.d(TAG, "The files under dir(" + file.getAbsolutePath() + ") is not null!");
            return null;
        }
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            fileList.add(files[i]);
        }
        Collections.sort(fileList, new FileComparatorTime());
        for (int i = 0; i < fileList.size(); i++) {
            String filePath = fileList.get(i).getAbsolutePath();
            String lowPath = filePath.toLowerCase();
            if (lowPath.endsWith(".log")) {
                return filePath;
            }
        }
        return null;
    }

    /**
     * 检索日志目录文档列表
     * @param path 检索路径
     */
    public static String[] getLogFilesList(String path) {
        File file = new File(path);
        if (file == null) {
            Log.d(TAG, "The file (" + file + ") is not exist!");
            return null;
        }
        File[] files = file.listFiles();
        if (files == null) {
            Log.d(TAG, "The files under dir(" + file.getAbsolutePath() + ") is not null!");
            return null;
        }
        List<String> fileList = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            String filePath = files[i].getName();
            String lowPath = filePath.toLowerCase();
            if (lowPath.endsWith(".txt") || lowPath.endsWith(".log")
                    || lowPath.endsWith(".pdf") || lowPath.endsWith(".doc")
                    || lowPath.endsWith(".docx")) {
                fileList.add(filePath);
            }
        }
        String[] strList = new String[fileList.size()];
        for(int i=0; i<fileList.size(); i++){
            strList[i] = fileList.get(i);
        }
        return strList;
    }

    /**
     * 获取媒体文件时长
     *
     */
    public static long getMediaDuration(MediaMetadataRetriever mmr, String path){
        mmr.setDataSource(path);
        long duration = Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        return duration;
    }

    /**
     * 将文件按名字降序排列
     */
    static class FileComparatorName implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            return file2.getName().compareTo(file1.getName());
        }
    }

    /**
     * 将文件按时间降序排列
     */
    static class FileComparatorTime implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            if (file1.lastModified() < file2.lastModified()) {
                return 1;// 最后修改的文件在前
            } else {
                return -1;
            }
        }
    }

    /**
     * 将文件按文件大小降序排列
     */
    static class FileComparatorSize implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            if (file1.length() < file2.length()) {
                return -1;// 小文件在前
            } else {
                return 1;
            }
        }
    }

    /**
     * 删除文件夹所有文件
     * @param file 要删除的文件夹的所在位置
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            //file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 扫描目录
     * @param ctx
     * @param dir 扫描目录
     */
    public static void scanDirAsync(Context ctx, String dir) {
        Intent scanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR");
        scanIntent.setData(Uri.fromFile(new File(dir)));
        ctx.sendBroadcast(scanIntent);
    }

    /**
     * 扫描文件
     * @param ctx
     * @param path 扫描目录
     */
    public static void scanFileAsync(Context ctx, String path){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        ctx.sendBroadcast(intent);//广播更新相册,查看保存图片
    }


    /**
     * 扫描文件
     * @param context
     * @param path 扫描目录
     */
    public static void scan(Context context, String path){

        //检索MTP.ini文件
        List<MediaScanner.ScanFile> scanFileList = new ArrayList<MediaScanner.ScanFile>();

        //检索hecser文件夹目录
        ArrayList<File> fileArrayList = new ArrayList<File>();
        getAllFiles(path, fileArrayList);
        for(File file1 : fileArrayList){
            String mineType = getMimeType(file1.getName());
            MediaScanner.ScanFile scanFile = new MediaScanner.ScanFile(file1.getAbsolutePath(), mineType);
            scanFileList.add(scanFile);
        }

        //刷新索引
        MediaScanner.getInstace().scanFiles(context, scanFileList);

    }

    /**
     * 屏蔽文件
     * @param context
     * @param path 扫描目录
     */
    public static void disScan(Context context, String path){
        Uri uri = Uri.parse(MediaStore.Files.getContentUri("external").toString());
        String Where = MediaStore.Files.FileColumns.DATA +" not like ? and "+ MediaStore.Files.FileColumns.DATA +" not like ?";
        String par = "%video%";
        String par1 = "%log.ini%";
//	        String Where=" instr("+FileColumns.DATA +", 'H1_') > 0";
        int res = context.getContentResolver().delete(uri, Where, new String[]{par,par1});
    }

    /**
     * 获取指定目录的目录列表
     * @param list 需要查询的文件目录
     */
    public static void getDirList(String dirPath, ArrayList<File> list) {
        File f = new File(dirPath);
        if (!f.exists()) {//判断路径是否存在
            return;
        }

        if(list == null){
            return;
        }

        File[] files = f.listFiles();

        if(files == null){//判断权限
            return;
        }

        for (File _file : files) {//遍历目录
            if(_file.getAbsolutePath().indexOf("MTP.ini") != -1
                    || _file.getAbsolutePath().indexOf("hecser") != -1){
                continue;
            }
            list.add(_file);
        }
    }

    /**
     * 获取指定目录内所有文件列表
     * @param list 需要查询的文件目录
     */
    public static void getAllFiles(String dirPath, ArrayList<File> list) {
        File f = new File(dirPath);
        if (!f.exists()) {//判断路径是否存在
            return;
        }

        if(list == null){
            return;
        }

        File[] files = f.listFiles();

        if(files == null){//判断权限
            return;
        }

        for (File _file : files) {//遍历目录
            if(_file.isFile()){
                list.add(_file);
            } else if(_file.isDirectory()){//查询子目录
                getAllFiles(_file.getAbsolutePath(), list);
            }
        }
    }

    public static String getSuffix(String fileName) {
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    public static String getMimeType(String fileName){
        String suffix = getSuffix(fileName);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if(!TextUtils.isEmpty(type)){
            return type;
        }
        return "file/*";
    }

}