package com.example.fs.cy_demo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fs.cy_demo.adapter.PostAdapter;
import com.example.fs.cy_demo.struct.Post;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class HelpFrag extends Fragment {
    private View help_view;
    ListView help_list;
    RelativeLayout help_nomore;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        help_view=inflater.inflate(R.layout.help_frag,container,false);
        initview();
        getHelpPost();
        return help_view;
    }

    private void getHelpPost() {
        BmobQuery<Post> query=new BmobQuery<>();
        query.addWhereEqualTo("post_type","求助");
        query.include("author");
        query.order("-createdAt");
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e==null){
                    ArrayList<Post> posts=new ArrayList<Post>();
                    for (int i = 0; i < list.size(); i++) {
                        Post post ;
                        post=list.get(i);
                        posts.add(post);
                    }
                    PostAdapter adapter=new PostAdapter(help_list,posts,getActivity());
                    help_list.setAdapter(adapter);
                    help_nomore.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(getActivity(), "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initview() {
        help_list= (ListView) help_view.findViewById(R.id.help_list);
        help_nomore= (RelativeLayout) help_view.findViewById(R.id.help_nomore);
    }
}
