package com.lyq.listviewdemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {

    private SparseArray<View> mViews;
    private View mConvertView;

    public ViewHolder(Context context, ViewGroup parent, int layoutId) {
        this.mViews = new SparseArray<View>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static ViewHolder getViewHolder(
            Context context, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            return holder;
        }
    }

    // 通过ID获取控件
    public <T extends View>T getViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getmConvertView() {
        return mConvertView;
    }

    // 通过字符串设置TextView的值
    public ViewHolder setText(int viewId, String text) {
        TextView tv = getViewById(viewId);
        tv.setText(text);
        return this;
    }

    // 通过资源文件设置ImageView的值
    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView iv = getViewById(viewId);
        iv.setImageResource(resId);
        return this;
    }

    // 通过位图设置ImageView的值
    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getViewById(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    public ViewHolder setImageURI(int viewId, String url) {
        ImageView iv = getViewById(viewId);
        // 通过网络图片设置ImageView的值
        return this;
    }
}

