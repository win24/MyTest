package com.huabao.mytest.ui.indicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.huabao.mytest.R;
import com.huabao.mytest.widget.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class CircleIndicatorActivity extends AppCompatActivity {

    private final static String TAG = "OtherAppActivity";

    private ViewPager mViewpager;
    private CircleIndicator mCircleIndicator;

    private List<Fragment> mFragmentList;
    private FragmentStatePagerAdapter mPagerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicator);

        initViews();

        initPage();
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewpager.setAdapter(mPagerAdapter);

        //viewpager是固定页数, 传入viewpager即可
        mCircleIndicator.setViewPager(mViewpager);
        mPagerAdapter.registerDataSetObserver(mCircleIndicator.getDataSetObserver());
        if(mFragmentList != null && mFragmentList.size() < 2){
            mCircleIndicator.setVisibility(View.GONE);
        }

    }

    private void initViews(){
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.indicator);
    }

    private void initPage(){
        if(mFragmentList == null){
            mFragmentList = new ArrayList<>();
        }
        mFragmentList.clear();
        mFragmentList.add(new Page1Fragment());
        mFragmentList.add(new Page2Fragment());
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter{

        private List<Fragment> fragmentList;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int i) {
            if(fragmentList != null) {
                return fragmentList.get(i);
            }else{
                return null;
            }
        }

        @Override
        public int getCount() {
            if(fragmentList != null) {
                return fragmentList.size();
            }else{
                return 0;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPagerAdapter.unregisterDataSetObserver(mCircleIndicator.getDataSetObserver());
    }

}
