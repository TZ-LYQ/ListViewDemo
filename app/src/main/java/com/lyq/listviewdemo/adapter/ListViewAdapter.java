package com.lyq.listviewdemo.adapter;

import android.content.Context;

import com.lyq.listviewdemo.R;
import com.lyq.listviewdemo.bean.ItemBean;
import com.lyq.listviewdemo.util.CommonAdapter;
import com.lyq.listviewdemo.util.ViewHolder;

import java.util.List;

public class ListViewAdapter extends CommonAdapter<ItemBean> {

    public ListViewAdapter(Context context, List<ItemBean> datas) {
        super(context, datas, R.layout.listview_item);
    }

    @Override
    public void convertView(ViewHolder holder, ItemBean bean) {
        holder.setText(R.id.text_view, bean.getText())
                .setImageResource(R.id.image_view, bean.getImageId());
    }
}

