package com.example.fs.cy_demo;


import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ComFrag extends Fragment{
    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentAdapter;
    private List<Fragment> mFragmentDate;
    private View com_view;
    private Context context;
    TextView title;
    ImageView mTabline;
    private int tab_width;
    private int tab_current_index;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com_view=inflater.inflate(R.layout.com_frag,container,false);
        context=com_view.getContext();
        Display display=getActivity().getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics=new DisplayMetrics();
        display.getMetrics(outMetrics);
        tab_width=outMetrics.widthPixels/2;
        initview();

        return com_view;
    }

    private void initview() {
        mTabline= (ImageView) com_view.findViewById(R.id.com_tabline);

        ViewGroup.LayoutParams lp= mTabline.getLayoutParams();
        lp.width=tab_width;
        mTabline.setLayoutParams(lp);



        mViewPager= (ViewPager) com_view.findViewById(R.id.com_vp_post);

        mFragmentDate=new ArrayList<Fragment>();
        HelpFrag mHelp=new HelpFrag();
        TravelFrag mTravel=new TravelFrag();
        mFragmentDate.add(mHelp);
        mFragmentDate.add(mTravel);
        mFragmentAdapter=new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return mFragmentDate.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentDate.size();
            }
        };
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) mTabline.getLayoutParams();
                if (tab_current_index==0 && position==0 && positionOffsetPixels!=0){
                    lp.leftMargin= (int) (positionOffset*tab_width+tab_current_index*tab_width);
                }
                else if (tab_current_index==1 && position==0 && positionOffsetPixels!=0){
                    lp.leftMargin= (int) (positionOffset*tab_width+(positionOffset-1)*tab_width);
                }
                else{
                }
                mTabline.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
               // Toast.makeText(context, "现在位置"+position, Toast.LENGTH_SHORT).show();
                tab_current_index=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
