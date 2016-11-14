package com.example.fs.cy_demo;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by fs on 2016/11/3.
 */
public class BannerAdapter extends PagerAdapter {
    //数据源
    private List<ImageView> mList;
    public BannerAdapter(List<ImageView> list){
        this.mList=list;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position%=mList.size();
        if (position<0){
            position = mList.size()+position;
        }
        ImageView view = mList.get(position);
        ViewParent vp =view.getParent();
        if (vp!=null){
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
            //container.removeView(mList.get(position%mList.size()));
    }
}
