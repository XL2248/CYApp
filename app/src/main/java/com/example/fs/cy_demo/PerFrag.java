package com.example.fs.cy_demo;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fs.cy_demo.struct.Myuser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PerFrag extends Fragment implements View.OnClickListener{
    View per_view;
    private TextView state_tv,sign_tv;
    private Context context;
    private String name;
    private Myuser user;
    private SharedPreferences sp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        per_view=inflater.inflate(R.layout.per_frag,container,false);
        context=per_view.getContext();
        per_initview();
        per_initaction();

        return  per_view;
    }

    private void per_initaction() {
        state_tv.setOnClickListener(this);
    }

    private void per_initview() {
        sp=context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        state_tv= (TextView) per_view.findViewById(R.id.per_state);
        if (sp.getBoolean("login",false)){
            name=sp.getString("user_name",name);
            state_tv.setText(name);
        }
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.per_state:
                //Toast.makeText(context, "点击了", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(),LoginAct.class);
                startActivity(intent);
        }
    }
}
