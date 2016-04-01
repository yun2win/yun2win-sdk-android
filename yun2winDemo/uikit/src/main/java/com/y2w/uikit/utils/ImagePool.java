package com.y2w.uikit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by maa2 on 2016/2/24.
 */
public class ImagePool {

    private static ImagePool _instance;

    public static ImagePool getInstance(Context context) {
        if (_instance == null)
            _instance = new ImagePool(context);
        return _instance;
    }

    ImageLoader _imageLoader;

    private ImagePool(Context context) {
        _imageLoader = ImageLoader.getInstance();
        init(context);
    }

    private void init(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                        // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        if (!_imageLoader.isInited())
            _imageLoader.init(config);
    }

    public ImageLoader getImageLoader(){
        return _imageLoader;
    }

    public Bitmap get(String imageUri) {
        return _imageLoader.loadImageSync(imageUri);
    }

    public void getWithListener(String imageUri,ImageLoadingListener listener,int round,int defaultDrawable) {
        Bitmap bitmap = null;
        bitmap = _imageLoader.loadImageSync(imageUri);
        if(bitmap != null){
            if(listener != null){
                listener.onLoadingComplete(imageUri, null, bitmap);
            }
        }else{
            get(imageUri, listener,0);
        }
    }

    public void get(String imageUri,ImageLoadingListener listener){
        get(imageUri, listener, 0);
    }
    public void get(String imageUri,ImageLoadingListener listener,int round){
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.showImageForEmptyUri(null)
                .showImageOnFail(null).cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565);
        if(round > 0){
            builder.displayer(new RoundedBitmapDisplayer(round));
        }
        DisplayImageOptions options = builder.build();
        _imageLoader.loadImage(imageUri, options, listener);
    }

    public void clearCache() {
        _imageLoader.clearDiscCache();
        _imageLoader.clearMemoryCache();
    }

    public void load(String imageUri, int defaultDrawable) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultDrawable).showImageForEmptyUri(null)
                .showImageOnFail(null).cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        try{
            _imageLoader.loadImage(imageUri, options, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onLoadingFailed(String imageUri, View view,
                                            FailReason failReason) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              Bitmap loadedImage) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    // TODO Auto-generated method stub
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void load(String imageUri, ImageView imageView, int defaultDrawable) {
        load(imageUri, imageView, defaultDrawable, 0);
    }

    public void load(String imageUri, ImageView imageView, int defaultDrawable,
                     int round) {
        load(imageUri, imageView, defaultDrawable, round, null);
    }

    public void load(String imageUri, ImageView imageView, Drawable defaultDrawable,
                     int round,ImageLoadingListener listener){
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.showImageOnLoading(defaultDrawable).showImageForEmptyUri(null)
                .showImageOnFail(null).cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565);
        if (round > 0) {
            builder.displayer(new RoundedBitmapDisplayer(round));
        }
        DisplayImageOptions options = builder.build();

        try{
            if(listener == null){
                _imageLoader.displayImage(imageUri, imageView, options);
            }else{
                _imageLoader.displayImage(imageUri, imageView, options,listener);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void load(String imageUri, ImageView imageView, int defaultDrawable,
                     int round,ImageLoadingListener listener) {
        if(imageUri == null){
            imageUri = "http://www.liyueyun.com";
        }
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.showImageOnLoading(defaultDrawable).showImageForEmptyUri(null)
                .showImageOnFail(null).cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565);
        if (round > 0) {
            builder.displayer(new RoundedBitmapDisplayer(round));
        }
        DisplayImageOptions options = builder.build();

        try{
            if(listener == null){
                _imageLoader.displayImage(imageUri, imageView, options);
            }else{
                _imageLoader.displayImage(imageUri, imageView, options,listener);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
