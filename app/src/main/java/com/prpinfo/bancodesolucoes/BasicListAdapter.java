package com.prpinfo.bancodesolucoes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public abstract class BasicListAdapter extends BaseAdapter {
    private final String[] values;
    private final LayoutInflater layoutInflater;

    BasicListAdapter(Context inputContext, String[] inputValues) {
        values = inputValues;
        layoutInflater = (LayoutInflater.from(inputContext));
    }

    public void setSelectedIndex(int index) {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int i) {
        return values[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;

        if (rowView == null) {
            rowView = layoutInflater.inflate(R.layout.approval_status_list_adapter, viewGroup, false);
        }
        ImageView image = rowView.findViewById(R.id.image);
        TextView title = rowView.findViewById(R.id.title);
        image.setImageResource(R.drawable.edit);

        try {
            title.setText(values[i]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowView;
    }

    public void callback(String callbackString) {
    }
}