package com.prpinfo.bancodesolucoes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class ApprovalStatusListAdapter extends BaseAdapter {
    private final ArrayList<String> values;
    private final LayoutInflater layoutInflater;

    ApprovalStatusListAdapter(Context inputContext, ArrayList<String> inputValues) {
        values = inputValues;
        layoutInflater = (LayoutInflater.from(inputContext));
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int i) {
        return values.get(i);
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
            JSONObject itemJsonObject = new JSONObject(values.get(i));
            title.setText(itemJsonObject.getString("text"));
            if (itemJsonObject.getString("approved").equals("Y")) {
                image.setImageResource(R.drawable.approved);
            } else {
                image.setImageResource(R.drawable.unapproved);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowView;
    }

    public void callback(String callbackString) {
    }
}
