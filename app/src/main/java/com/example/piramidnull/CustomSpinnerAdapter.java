package com.example.piramidnull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] values;
    private final int[] icons;
    private final int arrowIconResId;

    public CustomSpinnerAdapter(Context context, String[] values, int[] icons, int arrowIconResId) {
        super(context, R.layout.spinner_item, values);
        this.context = context;
        this.values = values;
        this.icons = icons;
        this.arrowIconResId = arrowIconResId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, R.layout.spinner_item, true);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, R.layout.spinner_dropdown_item, false);
    }

    private View createView(int position, View convertView, ViewGroup parent, int layoutId, boolean isSelectedView) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            holder = new ViewHolder();

            if (isSelectedView) {
                holder.text = convertView.findViewById(R.id.spinnerText);
                holder.icon = convertView.findViewById(R.id.spinnerIcon);
            } else {
                holder.text = convertView.findViewById(R.id.spinnerText);
                holder.icon = convertView.findViewById(R.id.spinnerIcon);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set text
        holder.text.setText(values[position]);

        // Set icon
        if (isSelectedView) {
            holder.icon.setImageResource(icons[position]);
        } else {
            if (position == 0) {
                holder.icon.setImageResource(arrowIconResId);
            } else {
                holder.icon.setImageResource(icons[position]);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView text;
        ImageView icon;
    }
}
