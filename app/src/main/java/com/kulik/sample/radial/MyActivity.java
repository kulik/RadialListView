package com.kulik.sample.radial;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;

import com.kulik.radial.RadialListView;

public class MyActivity extends Activity {
    RadialListView bc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        BaseAdapter adapter = new MyAdapter(this);
        bc = (RadialListView) findViewById(R.id.bookcase);
        bc.setAdapter(adapter);
    }

    public void scrolTo1(View b) {
        bc.setSelection(1);
    }

    public void scrolTo3(View b) {
        bc.setSelection(3);
    }
}

