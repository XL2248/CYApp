package com.example.fs.cy_demo;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ComFrag extends Fragment{
    private View com_view;
    private Context context;
    TextView title;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com_view=inflater.inflate(R.layout.com_frag,container,false);
        context=com_view.getContext();

        return com_view;
    }
}
