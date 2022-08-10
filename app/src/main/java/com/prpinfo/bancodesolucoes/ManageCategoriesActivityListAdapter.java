package com.prpinfo.bancodesolucoes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class ManageCategoriesActivityListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    private class ClickHandler implements View.OnClickListener {
        private final JSONObject jsonObject;

        ClickHandler(JSONObject inputObject) {
            jsonObject = inputObject;
        }

        @Override
        public void onClick(View view) {
            callback(jsonObject);
        }
    }

    ManageCategoriesActivityListAdapter(Context inputContext, ArrayList<String> inputValues) {
        super(inputContext, R.layout.activity_manage_users_list_adapter, inputValues);
        context = inputContext;
        values = inputValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.activity_manage_categories_list_adapter, parent, false);
        }
        LinearLayout rowContainer = rowView.findViewById(R.id.rowContainer);
        ImageView icon = rowView.findViewById(R.id.icon);
        TextView categoryTextview = rowView.findViewById(R.id.categoryTextview);
        try {
            JSONObject itemJsonObject = new JSONObject(values.get(position));
            categoryTextview.setText(itemJsonObject.getString("description"));
            categoryTextview.setOnClickListener(new ClickHandler(itemJsonObject));
            rowContainer.setOnClickListener(new ClickHandler(itemJsonObject));
            icon.setOnClickListener(new ClickHandler(itemJsonObject));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowView;
    }

    public void callback(JSONObject callbackJSONObject) { }
}
