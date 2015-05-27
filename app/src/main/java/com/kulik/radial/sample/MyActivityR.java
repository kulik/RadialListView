package com.kulik.radial.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;

import com.kulik.radial.RadialListView;
import com.kulik.sample.radial.R;

public class MyActivityR extends Activity {
    RadialListView bc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_r);
        BaseAdapter adapter = new MyAdapter(this, MyAdapter.numbers);
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

