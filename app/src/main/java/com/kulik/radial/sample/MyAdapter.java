package com.kulik.radial.sample;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kulik.sample.radial.R;

public class MyAdapter extends BaseAdapter {

    private static final String TAG = MyAdapter.class.getSimpleName();

    /**
     * Remember our context so we can use it when constructing views.
     */
    private Context mContext;

    /**
     * Hold onto a copy of the entire Contact List.
     */
//    private int[] icons = new int[]{
//            R.drawable.photo_acqure_ic_camera,
//            R.drawable.photo_acqure_ic_facebook,
//            R.drawable.photo_acqure_ic_files,
//            R.drawable.photo_acqure_ic_twitter,
//            R.drawable.photo_crop_ic_block,
//            R.drawable.photo_crop_ic_scissors,
//            R.drawable.photo_corrections_ic_auto,
//            R.drawable.photo_corrections_ic_bright,
//            R.drawable.photo_corrections_ic_contrast,
//    };
    private int[] icons = new int[]{
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p5,
            R.drawable.p6,
            R.drawable.p7,
            R.drawable.p8,
            R.drawable.p9,
            R.drawable.p10,
            R.drawable.p11
    };


    private final LayoutInflater mInflater;

    public MyAdapter(Context context) {

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return icons.length;
    }

    public Object getItem(int position) {
        return position;
    }

    /**
     * Use the array index as a unique id.
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * @param convertView The old view to overwrite, if one is passed
     * @returns a ContactEntryView that holds wraps around an ContactEntry
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView() at pos: " + position);
        View rl;

        //ToggleButton bt;
        if (convertView == null) {
            rl = mInflater.inflate(R.layout.item, parent, false);
        } else {
            rl = convertView;
        }
//        rl.setBackgroundColor(0x0fdedede);
        ((ImageView) rl.findViewById(R.id.icon)).setImageResource(icons[position]);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "VIew.Click");
            }
        });
        return rl;
    }


}