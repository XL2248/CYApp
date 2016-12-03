package com.example.fs.cy_demo.adapter;

import android.graphics.drawable.Drawable;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by fs on 2016/10/9.
 */
public class AsyncImageLoader {
    final Handler handler = new Handler();
    private HashMap<String, SoftReference<Drawable>> imageCache;
    public AsyncImageLoader() {
        imageCache = new HashMap<String, SoftReference<Drawable>>();//图片缓存
    }

    // 回调函数
    public interface ImageCallback {
        public void onImageLoad(Integer t, Drawable drawable);
        public void onError(Integer t);
    }

    public Drawable loadDrawable(final Integer pos, final String imageUrl,
                                 final ImageCallback imageCallback) {
        new Thread() {
            @Override
            public void run() {
                LoadImg(pos, imageUrl, imageCallback);
            }
        }.start();
        return null;
    }// loadDrawable---end

    public void LoadImg(final Integer pos, final String imageUrl,
                        final ImageCallback imageCallback) {
        // 首先判断是否在缓存中
        // 但有个问题是：ImageCache可能会越来越大，以至用户内存用光，所以要用SoftReference（弱引用）来实现
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            final Drawable drawable = softReference.get();
            if (drawable != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageCallback.onImageLoad(pos, drawable);
                    }
                });
                return;
            }
        }
        // 尝试从URL中加载
        try {
            final Drawable drawable = loadImageFromUrl(imageUrl);
            if (drawable != null) {
                imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageCallback.onImageLoad(pos, drawable);
                }
            });
        } catch (IOException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageCallback.onError(pos);
                }
            });
            e.printStackTrace();
        }

    }

    // 根据URL加载图片,如果出现错误throws IOException式的错误，以便在LoadImg中捕获，执行OnError（）函数
    public static Drawable loadImageFromUrl(String url) throws IOException {
        URL m;
        InputStream i = null;
        m = new URL(url);
        i = (InputStream) m.getContent();
        Drawable d = Drawable.createFromStream(i, "src");
        return d;
    }
}
