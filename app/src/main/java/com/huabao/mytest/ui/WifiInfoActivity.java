package com.huabao.mytest.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huabao.mytest.R;
import com.huabao.mytest.bean.WifiInfo;
import com.huabao.mytest.utils.WifiManagerUtil;

import java.util.List;

public class WifiInfoActivity extends AppCompatActivity {

    private WifiManagerUtil wifiManagerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiinfo);

        wifiManagerUtil = new WifiManagerUtil();
        try {
            Init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public void Init() throws Exception {
        List<WifiInfo> wifiInfos = wifiManagerUtil.Read();
        ListView wifiInfosView=(ListView)findViewById(R.id.wifi_listview);
        WifiAdapter ad = new WifiAdapter(wifiInfos,WifiInfoActivity.this);
        wifiInfosView.setAdapter(ad);
    }

    public class WifiAdapter extends BaseAdapter {

        List<WifiInfo> wifiInfos =null;
        Context con;

        public WifiAdapter(List<WifiInfo> wifiInfos,Context con){
            this.wifiInfos =wifiInfos;
            this.con = con;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return wifiInfos.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return wifiInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            convertView = LayoutInflater.from(con).inflate(android.R.layout.simple_list_item_1, null);
            TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
            tv.setText("Wifi:"+wifiInfos.get(position).Ssid+"\n密码:"+wifiInfos.get(position).Password);
            return convertView;
        }

    }

}
