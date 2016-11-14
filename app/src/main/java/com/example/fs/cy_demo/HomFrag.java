package com.example.fs.cy_demo;

import android.app.Fragment;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * Created by fs on 2016/11/3.
 */
public class HomFrag extends Fragment {
    private ViewPager mViewPager;
    private List<ImageView> mlist;
    private LinearLayout mLinearLayout;
    private int[] bannerImages={R.drawable.pic01,R.drawable.pic02,R.drawable.pic03};
    private View view;

    private BannerAdapter mAdapter;
    private BannerListener bannerListener;

    private Context context;
    // 圆圈标志位
    private int pointIndex = 0;
    // 线程标志
    private boolean isStop = false;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.home_frag,container,false);
        final Handler handler=new Handler();
        initView();
        initData();
        initAction();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop){
                    SystemClock.sleep(2000);
                    handler.post(updatePager);
                }
            }
        }).start();
        return view;
    }
    Runnable updatePager=new Runnable() {
        @Override
        public void run() {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        }
    };

    private void initAction() {
        bannerListener = new BannerListener();
        mViewPager.setOnPageChangeListener(bannerListener);
        //取中间数来作为起始位置
        int index = (Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2 % mlist.size());
        //用来出发监听器
        mViewPager.setCurrentItem(index);
        mLinearLayout.getChildAt(pointIndex).setEnabled(true);
    }

    private void initData() {
        //新建一个view
        View mview = null;
        mlist = new ArrayList<ImageView>();
        LayoutParams params;
        for (int i = 0; i < bannerImages.length; i++) {
            // 设置广告图
            ImageView imageView=new ImageView(context);
            imageView.setLayoutParams(new LayoutParams(ViewPager.LayoutParams.MATCH_PARENT,ViewPager.LayoutParams.MATCH_PARENT));
            imageView.setBackgroundResource(bannerImages[i]);
            mlist.add(imageView);
            // 设置圆圈点
            mview = new View(context);
            params = new LayoutParams(10,10);
            params.leftMargin=10;
            mview.setBackgroundResource(R.drawable.point);
            mview.setLayoutParams(params);
            mview.setEnabled(false);

            mLinearLayout.addView(mview);
        }
        mAdapter = new BannerAdapter(mlist);
        mViewPager.setAdapter(mAdapter);
    }

    private void initView() {
        context=view.getContext();
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mLinearLayout = (LinearLayout)view. findViewById(R.id.points);
    }


    class BannerListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int newPosition = position % bannerImages.length;
            mLinearLayout.getChildAt(newPosition).setEnabled(true);
            mLinearLayout.getChildAt(pointIndex).setEnabled(false);
            // 更新标志位
            pointIndex = newPosition;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
