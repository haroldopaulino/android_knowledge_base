package com.prpinfo.bancodesolucoes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class CategoriesListviewListAdapter extends BaseAdapter {
    private final ArrayList<String> values;
    private final LayoutInflater layoutInflater;
    private int selectedIndex;

    CategoriesListviewListAdapter(Context contextInput, ArrayList<String> titlesInput) {
        values = titlesInput;
        layoutInflater = (LayoutInflater.from(contextInput));
        selectedIndex = -1;
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
        notifyDataSetChanged();
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
            rowView = layoutInflater.inflate(R.layout.categories_list_adapter, viewGroup, false);
        }
        LinearLayout rowContainer = rowView.findViewById(R.id.rowContainer);
        TextView title = rowView.findViewById(R.id.title);
        ImageView image = rowView.findViewById(R.id.image);

        if (selectedIndex != i) {
            image.setImageResource(R.drawable.unselected);
        } else {
            image.setImageResource(R.drawable.selected);
        }

        try {
            if (values.get(i).equals("<<<-1>>>")) {
                rowContainer.setBackgroundResource(R.drawable.white_border_transparent_box);
                title.setText("ALL CATEGORIES");
            } else {
                JSONObject rowObject = new JSONObject(values.get(i));
                title.setText(rowObject.getString("description"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowView;
    }

    public void callback(String callbackString) { }
}
