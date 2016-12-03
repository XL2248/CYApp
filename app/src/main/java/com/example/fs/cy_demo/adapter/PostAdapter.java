package com.example.fs.cy_demo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fs.cy_demo.R;
import com.example.fs.cy_demo.customer.CircleImageView;
import com.example.fs.cy_demo.customer.CollapsedTextView;
import com.example.fs.cy_demo.struct.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {
    private AsyncImageLoader asyncImageLoader;
    private ListView listView;
    private ArrayList<Post> posts;
    private Context context;
    private LayoutInflater inflater;
    private String purl,picurl;
    LinearLayout line;
    CircleImageView user_head;


    public PostAdapter(ListView listView, ArrayList<Post> posts, Context context){
        this.listView = listView;
        this.posts = posts;
        this.context = context;
        inflater=LayoutInflater.from(this.context);
        asyncImageLoader=new AsyncImageLoader();
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=inflater.inflate(R.layout.post_list,null);
        }
        convertView.setTag(position);
        Post post= (Post) getItem(position);

        //找控件
        user_head= (CircleImageView) convertView.findViewById(R.id.post_user_head);
        TextView user_name= (TextView) convertView.findViewById(R.id.post_user_name);
        TextView post_time= (TextView) convertView.findViewById(R.id.post_time);
        CollapsedTextView post_detail= (CollapsedTextView) convertView.findViewById(R.id.post_detail);


        user_head.setImageResource(R.drawable.head);
        user_name.setText(post.getAuther().getUser_name());
        post_time.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(post.getDate()));
        post_detail.setShowText(post.getContent());

        if (post.getAuther().getUser_head()==null){
            user_head.setImageResource(R.drawable.head);
        }
        else {
            purl=post.getAuther().getUser_head_url();
        }

        asyncImageLoader.loadDrawable(position, purl, new AsyncImageLoader.ImageCallback() {
            @Override
            public void onImageLoad(Integer t, Drawable drawable) {
                View view=listView.findViewWithTag(t);
                if (view!=null){
                    CircleImageView imageView= (CircleImageView) view.findViewById(R.id.post_user_head);
                    imageView.setImageDrawable(drawable);
                }
            }

            @Override
            public void onError(Integer t) {
                View view=listView.findViewWithTag(t);
                if (view!=null){
                    CircleImageView imageView= (CircleImageView) view.findViewById(R.id.post_user_head);
                    imageView.setImageResource(R.drawable.head);
                }
            }
        });

        return convertView;
    }
}
