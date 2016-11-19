package com.example.fs.cy_demo;

import android.app.Fragment;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.example.fs.cy_demo.adapter.BannerAdapter;
import com.example.fs.cy_demo.customer.CollapsedTextView;
public class HomFrag extends Fragment {
    private ViewPager mViewPager;
    private List<ImageView> mlist;
    private LinearLayout mLinearLayout;
    private int[] bannerImages={R.drawable.pic01,R.drawable.pic02,R.drawable.pic03};
    private View view;
    private CollapsedTextView m_text;

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
        Button button= (Button) view.findViewById(R.id.test_bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity()==null){
                    Toast.makeText(context, "activity为空", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, "点击", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(),LoginAct.class));
                }
            }
        });
        m_text.setShowText("因为公司项目需要全文收起的功能，一共有2种UI所以需要写2个全文收起的控件，之前也是用过一个全文收起的控件，但是因为设计原因，在ListView刷新的时候会闪烁，我估计原因是因为控件本身的设计是需要先让TextView绘制完成，然后获取TextView一共有多少行，再判断是否需要全文收起按钮，如果需要，则吧TextView压缩回最大行数，添加全文按钮，这样就会造成ListView的Item先高后低，所以会发生闪烁，后面我也在网上找了几个，发现和之前的设计都差不多，虽然肯定是有解决了这个问题的控件，但是还是决定自己写了，毕竟找到控件后还需要测试，而现在的项目时间不充分啊（另外欢迎指教如何快速的找到自己需要的控件，有时候在Github上面搜索，都不知道具体该用什么关键字），而且自己写，也是一种锻炼。");
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
        m_text= (CollapsedTextView) view.findViewById(R.id.mtext);
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
