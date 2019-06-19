package com.huabao.mytest.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TestProvider extends ContentProvider {

    private Context mContext;
    DBHelper mDbHelper = null;
    SQLiteDatabase db = null;

    // 设置ContentProvider的唯一标识
    public static final String AUTHORITY = "com.huabao.testprovider";
    //数据改变后指定通知的Uri
    private static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY + "/" + DBHelper.TABLE_NAME);

    public static final int MATCH_CODE = 1;
    // UriMatcher类使用:在ContentProvider 中注册URI
    private static final UriMatcher mMatcher;
    static{
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 初始化
        mMatcher.addURI(AUTHORITY, DBHelper.TABLE_NAME, MATCH_CODE);
    }

    /**
     * 初始化ContentProvider
     */
    @Override
    public boolean onCreate() {

        mContext = getContext();
        // 在ContentProvider创建时对数据库进行初始化
        // 运行在主线程，故不能做耗时操作,此处仅作展示
        mDbHelper = new DBHelper(getContext());
        db = mDbHelper.getWritableDatabase();

        return true;
    }

    /**
     * 添加数据
     */

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // 根据URI匹配 URI_CODE，从而匹配ContentProvider中相应的表名
        String table = getTableName(uri);
        if(table != null) {
            // 向该表添加数据
            long id = db.insert(table, null, values);
            // 当该URI的ContentProvider数据发生变化时，通知外界（即访问该ContentProvider数据的访问者）
            mContext.getContentResolver().notifyChange(NOTIFY_URI, null);

            // 通过ContentUris类从URL中获取ID
//        long personid = ContentUris.parseId(uri);
//        System.out.println(personid);

            return ContentUris.withAppendedId(uri, id);
        }else{
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    /**
     * 查询数据
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection
            , String[] selectionArgs, String sortOrder) {

        String table = getTableName(uri);
        if(table != null) {
            return db.query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
        }else{
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    /**
     * 更新数据
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        String table = getTableName(uri);
        if(table != null) {
            int i = db.update(table, values, selection, selectionArgs);
            mContext.getContentResolver().notifyChange(NOTIFY_URI, null);
            return i;
        }else{
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    /**
     * 删除数据
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        String table = getTableName(uri);
        if(table != null) {
            int i = db.delete(table, selection, selectionArgs);
            mContext.getContentResolver().notifyChange(NOTIFY_URI, null);
            return i;
        }else{
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (mMatcher.match(uri)) {
        case MATCH_CODE:
            return "com.huabao.vehicledoctor/value";
        default:
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    /**
     * 根据URI匹配 URI_CODE，从而匹配ContentProvider中相应的表名
     */
    private String getTableName(Uri uri){
        String tableName = null;
        switch (mMatcher.match(uri)) {
            case MATCH_CODE:
                tableName = DBHelper.TABLE_NAME;
                break;
        }
        return tableName;
    }

}