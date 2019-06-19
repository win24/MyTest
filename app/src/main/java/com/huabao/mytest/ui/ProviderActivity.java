package com.huabao.mytest.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.huabao.mytest.R;
import com.huabao.mytest.provider.DBHelper;
import com.huabao.mytest.provider.TestObserver;
import com.huabao.mytest.provider.TestProvider;

public class ProviderActivity extends AppCompatActivity {

    private static final Uri uri = Uri.parse("content://" + TestProvider.AUTHORITY + "/" + DBHelper.TABLE_NAME);
    private ContentObserver contentObserver;
    ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiinfo);

        contentResolver = getContentResolver();
        contentObserver = new TestObserver(new Handler());
        // 查找id为1的数据
        Cursor cursor = contentResolver.query(uri, null, null,null, null);
        if(cursor != null && cursor.getCount() != 0){
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", 1);
            contentValues.put("time", System.currentTimeMillis());
            contentValues.put("mileage", 20000);
            contentValues.put("interval", 6000);
            contentValues.put("tip", 12);
            //int id = contentResolver.update(uri, contentValues, "_ID=?", new String[]{"3"});
            int id = contentResolver.update(uri, contentValues, null, null);
        }else{
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", 1);
            contentValues.put("time", System.currentTimeMillis());
            contentValues.put("mileage", 20000);
            contentValues.put("interval", 4000);
            contentValues.put("tip", 1);
            contentResolver.insert(uri, contentValues);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentResolver.unregisterContentObserver(contentObserver);
    }
}
