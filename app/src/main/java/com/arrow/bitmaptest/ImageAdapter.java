package com.arrow.bitmaptest;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.arrow.test.MainActivity;
import com.arrow.test.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private List<String> mUrList;
    private Context mContext;
    private Drawable mDefaultBitmapDrawable;
    private boolean mIsGridViewIdle;
    private boolean mCanGetBitmapFromNetWork;
    ImageLoader mImageLoader;
    private int mImageWidth=0;

    public ImageAdapter(List<String> mUrList, Context mContext,boolean mIsGridViewIdle,boolean mCanGetBitmapFromNetWork) {
        this.mUrList = mUrList;
        this.mContext = mContext;
        this.mIsGridViewIdle = mIsGridViewIdle;
        this.mCanGetBitmapFromNetWork = mCanGetBitmapFromNetWork;
        mImageLoader = ImageLoader.build(mContext);
        mDefaultBitmapDrawable = mContext.
                getResources().getDrawable(R.drawable.ic_launcher_background);
    }

    @Override
    public int getCount() {
        return mUrList.size();
    }

    @Override
    public Object getItem(int i) {
        return mUrList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.photowall_item,viewGroup,false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.image);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
            ImageView imageView = holder.imageView;
            String tag = (String) imageView.getTag();
            String uri = (String) getItem(i);
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            int screenWidth = dm.widthPixels;
            int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f,
                    mContext.getResources().getDisplayMetrics());
            mImageWidth = (screenWidth -space)/3;
            if (!uri.equals(tag)){
                imageView.setImageDrawable(mDefaultBitmapDrawable);
            }
            if (mIsGridViewIdle && mCanGetBitmapFromNetWork){
                imageView.setTag(uri);
                mImageLoader.bindBitmap(uri,imageView,mImageWidth,mImageWidth);
            }
        }
        return view;
    }

    static class ViewHolder{
        public ImageView imageView;
    }
}
