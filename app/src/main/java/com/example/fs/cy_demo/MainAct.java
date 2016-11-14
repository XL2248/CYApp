package com.example.fs.cy_demo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.bmob.v3.Bmob;

public class MainAct extends Activity implements View.OnClickListener{
    int black,blue;
    ImageButton ib_hom,ib_com,ib_per;
    TextView tv_hom,tv_com,tv_per;
    LinearLayout line_hom,line_com,line_per;
    Fragment hom_frag,com_frag,per_frag;
    private String name;
    private int n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_act);
        Bundle bundle=getIntent().getExtras();
        Bmob.initialize(this, "e3261c1decdbfe7e562b5d0ba6884245");
        initView();
        initEvents();
        if (bundle!=null){
            name=bundle.getString("name");
            n=bundle.getInt("number");
            setSelectFrag(n);
        }
        else {
            name=null;
            setSelectFrag(0);
        }
    }

    private void setSelectFrag(int i) {
        //切换图片文字颜色，切换fragment
        FragmentManager fm=getFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        hideFragment(transaction);
        switch (i){
            case 0:
                ib_hom.setImageResource(R.mipmap.home1);
                tv_hom.setTextColor(blue);
                if (hom_frag==null){
                    hom_frag=new HomFrag();
                    transaction.add(R.id.m_frag,hom_frag);
                }
                else {
                    transaction.show(hom_frag);
                }
                break;
            case 1:
                ib_com.setImageResource(R.mipmap.community1);
                tv_com.setTextColor(blue);
                if (com_frag==null){
                    com_frag=new ComFrag();
                    transaction.add(R.id.m_frag,com_frag);
                }
                else {
                    transaction.show(com_frag);
                }
                break;

            case 2:
                ib_per.setImageResource(R.mipmap.person1);
                tv_per.setTextColor(blue);
                if (per_frag==null){
                    per_frag=new PerFrag();
                    transaction.add(R.id.m_frag,per_frag);
                }
                else {
                    transaction.show(per_frag);
                }
                break;
        }
        transaction.commit();
    }

    private void initEvents() {
        line_hom.setOnClickListener(this);
        line_com.setOnClickListener(this);
        line_per.setOnClickListener(this);
    }

    private void initView() {
        //初始化控件
        line_hom= (LinearLayout) findViewById(R.id.b_line_hom);
        line_com= (LinearLayout) findViewById(R.id.b_line_com);
        line_per= (LinearLayout) findViewById(R.id.b_line_per);
        ib_hom= (ImageButton) findViewById(R.id.b_id_hom);
        ib_com= (ImageButton) findViewById(R.id.b_id_com);
        ib_per= (ImageButton) findViewById(R.id.b_id_per);
        ib_hom.setClickable(false);
        ib_com.setClickable(false);
        ib_per.setClickable(false);
        tv_hom= (TextView) findViewById(R.id.b_tv_hom);
        tv_com= (TextView) findViewById(R.id.b_tv_com);
        tv_per= (TextView) findViewById(R.id.b_tv_per);
        black=getResources().getColor(R.color.z_gary);
        blue=getResources().getColor(R.color.blue);
    }

    @Override
    public void onClick(View v) {
        resetImage();
        switch (v.getId()){
            case R.id.b_line_hom:
            {
                setSelectFrag(0);
            }
            break;
            case R.id.b_line_com:
            {
                setSelectFrag(1);
            }
            break;
            case R.id.b_line_per:
            {
                setSelectFrag(2);
            }
            break;
        }
    }

    private void resetImage() {
        ib_hom.setImageResource(R.mipmap.home0);
        ib_com.setImageResource(R.mipmap.community0);
        ib_per.setImageResource(R.mipmap.person0);
        tv_hom.setTextColor(black);
        tv_com.setTextColor(black);
        tv_per.setTextColor(black);
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (hom_frag!=null){
            transaction.hide(hom_frag);
        }
        if (com_frag!=null){
            transaction.hide(com_frag);
        }
        if (per_frag!=null){
            transaction.hide(per_frag);
        }
    }


}
