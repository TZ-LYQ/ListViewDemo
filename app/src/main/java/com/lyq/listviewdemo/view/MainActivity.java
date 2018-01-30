package com.lyq.listviewdemo.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.lyq.listviewdemo.R;
import com.lyq.listviewdemo.adapter.ListViewAdapter;
import com.lyq.listviewdemo.bean.ItemBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<ItemBean> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListView();
    }

    private void initListView() {
        initData();
        ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, datas);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            ItemBean bean = new ItemBean(
                    this.getString(R.string.app_name) + i, R.mipmap.ic_launcher);
            datas.add(bean);
        }
    }
}
