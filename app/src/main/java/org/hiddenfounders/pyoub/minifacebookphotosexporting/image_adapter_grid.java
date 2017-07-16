package org.hiddenfounders.pyoub.minifacebookphotosexporting;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Ayoub on 11/07/2017.
 */

public class image_adapter_grid extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();

    public image_adapter_grid(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.checkBox = (CheckBox) row.findViewById(R.id.checkBox2);
            holder.image = (ImageView) row.findViewById(R.id.imagead);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        image_adapter item = (image_adapter) data.get(position);
        holder.image.setImageBitmap(item.getBitmap());
        holder.checkBox.setChecked(item.getcheck());
        return row;
    }

    static class ViewHolder {
        CheckBox checkBox;
        ImageView image;
    }
}
