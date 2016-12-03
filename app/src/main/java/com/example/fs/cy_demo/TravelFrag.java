package com.example.fs.cy_demo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fs on 2016/11/19.
 */
public class TravelFrag extends Fragment {
    private  View travel_view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        travel_view=inflater.inflate(R.layout.post_frag,container,false);
        return travel_view;
    }
}
