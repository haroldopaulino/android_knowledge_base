package com.prpinfo.bancodesolucoes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class MainActivityListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    private class ClickHandler implements View.OnClickListener {
        private String action;

        ClickHandler(String inputAction) {
            action = inputAction;
        }

        @Override
        public void onClick(View view) {
            try {
                callback(new JSONObject().put("action", action));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public MainActivityListAdapter(Context inputContext, ArrayList<String> inputValues) {
        super(inputContext, R.layout.activity_main_list_adapter, inputValues);
        context = inputContext;
        values = inputValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.activity_main_list_adapter, parent, false);
        }

        LinearLayout rowContainer = rowView.findViewById(R.id.rowContainer);
        TextView title = rowView.findViewById(R.id.title);
        ImageView imageView = rowView.findViewById(R.id.icon);
        String itemValue = values.get(position);
        title.setText(itemValue);
        switch(itemValue) {
            case "Search Solution" :
                imageView.setImageResource(R.drawable.search);
                break;
            case "Create New Solution" :
                imageView.setImageResource(R.drawable.add);
                break;
            case "Manage Solutions" :
                imageView.setImageResource(R.drawable.edit);
                break;
            case "New User" :
                imageView.setImageResource(R.drawable.new_user);
                break;
            case "Manage Users" :
                imageView.setImageResource(R.drawable.users);
                break;
            case "New Category" :
                imageView.setImageResource(R.drawable.new_category);
                break;
            case "Manage Categories" :
                imageView.setImageResource(R.drawable.categories);
                break;
            case "My Info" :
                imageView.setImageResource(R.drawable.user_info);
                break;
            case "Import Solutions" :
                imageView.setImageResource(R.drawable.import_solutions);
                break;
            case "Export Solutions" :
                imageView.setImageResource(R.drawable.export_solutions);
                break;
        }
        rowContainer.setOnClickListener(new ClickHandler(itemValue));
        title.setOnClickListener(new ClickHandler(itemValue));
        imageView.setOnClickListener(new ClickHandler(itemValue));

        return rowView;
    }

    public void callback(JSONObject callbackJSONObject) { }
}