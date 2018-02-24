package com.jackchen.view_day14;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView list_view;

    private List<String> mItems ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_view = (ListView) findViewById(R.id.list_view);
        mItems = new ArrayList<>() ;

        for (int i = 0; i < 200; i++) {
            mItems.add("i -> "+i) ;
        }


        list_view.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mItems.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                //因为xml文件中只有一个TextView控件，所以可以直接强转为TextView
                TextView item = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.item_lv , parent , false);
                item.setText(mItems.get(position));
                return item;
            }
        });

    }
}
