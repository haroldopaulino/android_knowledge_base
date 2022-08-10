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

public abstract class ManageUsersActivityListAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final ArrayList<String> values;
  private JSONObject accessTypesData;
  private Utilities utilities;

  private class ClickHandler implements View.OnClickListener {
    private JSONObject jsonObject;

    ClickHandler(JSONObject inputObject) {
      jsonObject = inputObject;
    }

    @Override
    public void onClick(View view) {
      callback(jsonObject);
    }
  }

  ManageUsersActivityListAdapter(Context inputContext, ArrayList<String> inputValues) {
    super(inputContext, R.layout.activity_manage_users_list_adapter, inputValues);
    context = inputContext;
    values = inputValues;

    utilities = new Utilities(context);
    SqliteCore sqliteCore = new SqliteCore(context);
    try {
      accessTypesData = new JSONObject(sqliteCore.getSetting("accessTypesData"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = convertView;

    if (convertView == null) {
      rowView = inflater.inflate(R.layout.activity_manage_users_list_adapter, parent, false);
    }
    LinearLayout rowContainer = rowView.findViewById(R.id.rowContainer);
    TextView
            accessTypeTextview = rowView.findViewById(R.id.accessTypeTextview),
            fullNameTextview = rowView.findViewById(R.id.fullNameTextview),
            emailTextview = rowView.findViewById(R.id.emailTextview),
            enrolledSinceTextview = rowView.findViewById(R.id.enrolledSinceTextview);
    ImageView iconImageview = rowView.findViewById(R.id.iconImageview);
    try {
      JSONObject itemJsonObject = new JSONObject(values.get(position));
      accessTypeTextview.setText(
              accessTypesData.has(itemJsonObject.getString("access_type")) ?
                      accessTypesData.getString(itemJsonObject.getString("access_type")) :
                      "");
      if (!itemJsonObject.getString("firstname").trim().equals("") ||
          !itemJsonObject.getString("lastname").trim().equals("")) {
        fullNameTextview.setVisibility(View.VISIBLE);
        fullNameTextview.setText(itemJsonObject.getString("firstname") + " " + itemJsonObject.getString("lastname"));
      } else fullNameTextview.setVisibility(View.GONE);

      if (!itemJsonObject.getString("email").trim().equals("")) {
        emailTextview.setVisibility(View.VISIBLE);
        emailTextview.setText(itemJsonObject.getString("email"));
      } else emailTextview.setVisibility(View.GONE);

      if (!itemJsonObject.getString("timestamp_added").trim().equals("")) {
        enrolledSinceTextview.setVisibility(View.VISIBLE);
        enrolledSinceTextview.setText("Added " + utilities.formatMysqlTimestamp(itemJsonObject.getString("timestamp_added")));
      } else enrolledSinceTextview.setVisibility(View.GONE);

      if (accessTypesData.getString(itemJsonObject.getString("access_type")).toLowerCase().contains("administrator")) {
        iconImageview.setImageResource(R.drawable.blue_shield);
      } else if (accessTypesData.getString(itemJsonObject.getString("access_type")).toLowerCase().contains("employee")) {
        iconImageview.setImageResource(R.drawable.active_user);
      } else {
        iconImageview.setImageResource(R.drawable.disabled_user);
      }

      rowContainer.setOnClickListener(new ClickHandler(itemJsonObject));
      fullNameTextview.setOnClickListener(new ClickHandler(itemJsonObject));
      emailTextview.setOnClickListener(new ClickHandler(itemJsonObject));
      enrolledSinceTextview.setOnClickListener(new ClickHandler(itemJsonObject));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return rowView;
  }

  public void callback(JSONObject callbackJSONObject) { }
}