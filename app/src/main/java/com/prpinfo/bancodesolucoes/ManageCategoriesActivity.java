package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ManageCategoriesActivity extends Activity {
    private Activity activity;
    private SqliteCore sqliteCore;
    ArrayList<String> resultsList;
    private ListView mainListView;
    private TextView titleMessage;
    private EditText
            categoryEdittext;
    private LocalManageCategoriesActivityListAdapter localManageCategoriesActivityListAdapter;
    private Utilities utilities;
    private LocalGenericAlert localGenericAlert;

    public class LocalGenericAlert extends GenericAlert {
        LocalGenericAlert(
                Activity activityInput,
                AlertDialog.Builder alertInput,
                View viewInput,
                String tagInput,
                String titleInput,
                String messageInput,
                String button1Input,
                String button2Input) {
            super(activityInput, alertInput, viewInput, tagInput, titleInput, messageInput, button1Input, button2Input);
        }

        public void callback(JSONObject inputResponse) {
            utilities.dismissKeyboard(activity);
            try {
                if (inputResponse.getString("tag").equalsIgnoreCase("register") &&
                    inputResponse.getString("return").equalsIgnoreCase("ok")) {
                    //register();
                }

                if (inputResponse.getString("tag").equalsIgnoreCase("new_company") &&
                    inputResponse.getString("return").equalsIgnoreCase("ok")) {
                    //newCompany();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class LocalManageCategoriesActivityListAdapter extends ManageCategoriesActivityListAdapter {
        LocalManageCategoriesActivityListAdapter(Activity inputContext, ArrayList<String> inputList) {
            super(inputContext, inputList);
        }

        @Override
        public void callback(JSONObject callbackObject) {
            manageCategoryDetailActivity(callbackObject);
        }
    }

    private class ClickHandler implements View.OnClickListener {

        String action;

        ClickHandler(String actionInput) {
            action = actionInput;
        }

        @Override
        public void onClick(View view) {
            switch(action) {
                case "BACK" :
                    activity.finish();
                    break;
                case "RUN_SEARCH" :
                    populateList();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        activity = this;
        utilities = new Utilities(activity);
        sqliteCore = new SqliteCore(activity);

        utilities.dismissKeyboard(this);
        setupViews();
        populateList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sqliteCore.getSetting("reload_categories").equals("YES")) {
            sqliteCore.createSetting("reload_categories", "NO");
            populateList();
        }
    }

    private void setupViews() {
        TextView title = findViewById(R.id.title);
        titleMessage = findViewById(R.id.titleMessage);
        title.setText("Manage Categories");

        mainListView = findViewById(R.id.mainList);

        ImageView backImage = findViewById(R.id.backImage);
        FrameLayout backImageContainer = findViewById(R.id.backImageContainer);
        backImage.setOnClickListener(new ClickHandler("BACK"));
        backImageContainer.setOnClickListener(new ClickHandler("BACK"));

        categoryEdittext = findViewById(R.id.categoryEdittext);

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new ClickHandler("RUN_SEARCH"));

        populateList();
    }

    public void populateList() {
        try {
            String localCategoryData = sqliteCore.getSetting("categoriesData");
            if (!localCategoryData.equals("")) {
                titleMessage.setText("Search");
                resultsList = new ArrayList<>();
                try {
                    JSONArray categoriesArray = new JSONArray(localCategoryData);
                    String addObject = "";
                    for (int i = 0; i < categoriesArray.length(); i++) {
                        if (categoryEdittext.getText().toString().trim().equals("")) {
                            addObject = categoriesArray.get(i).toString();
                        } else {
                            if (categoriesArray.get(i).toString().toLowerCase().contains(categoryEdittext.getText().toString().toLowerCase())) {
                                addObject = categoriesArray.get(i).toString();
                            }
                        }

                        resultsList.add(utilities.parseStringToSingleJsonObjectKeyValue(addObject).toString());
                    }
                } catch (Exception e2) {
                    //this means the company has no categories, but at least a blank category is given to each company, with code 1, which is not a JSONARRAY, but a JSONOBJECT describing the only blank category
                    resultsList.add(utilities.parseStringToSingleJsonObjectKeyValue(localCategoryData).toString());
                    e2.printStackTrace();
                }

                titleMessage.setText("Listing " + resultsList.size() + " categories");

                try {
                    if (localManageCategoriesActivityListAdapter != null) {
                        localManageCategoriesActivityListAdapter.clear();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                localManageCategoriesActivityListAdapter = new LocalManageCategoriesActivityListAdapter(this, resultsList);
                mainListView.setAdapter(localManageCategoriesActivityListAdapter);
                localManageCategoriesActivityListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageCategoryDetailActivity(JSONObject solutionDetailObject) {
        try {
            if (!solutionDetailObject.getString("description").equals("")) {
                Intent intent = new Intent(this, ManageCategoriesDetailActivity.class);
                intent.putExtra("data", solutionDetailObject.toString());
                startActivityForResult(intent, 1);
            } else {
                openAlertMessage(activity, "error", "Warning", "Unable to manage a blank Category.", "GOT IT", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateList(String actionInput, JSONObject userDetailObject) {
        try {
            String action;
            if (actionInput.toUpperCase().contains("DELETE")) {
                action = "delete";
            } else {
                action = "update";
            }

            resultsList = new ArrayList<>();
            JSONArray usersArray = new JSONArray(sqliteCore.getSetting("usersData"));
            setTitle("Listing " + usersArray.length() + " items");
            int indexToDelete = -1;
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject currentUser = new JSONObject(usersArray.get(i).toString());
                if (action.equals("delete")) {
                    if (currentUser.getInt("id") == userDetailObject.getInt("id")) {
                        indexToDelete = i;
                    } else {
                        resultsList.add(usersArray.get(i).toString());
                    }
                } else {
                    if (currentUser.getInt("id") == userDetailObject.getInt("id")) {
                        Iterator<?> userObjectKeys = userDetailObject.keys();
                        while(userObjectKeys.hasNext() ) {
                            String userObjectKey = (String)userObjectKeys.next();
                            String userObjectValue = userDetailObject.getString(userObjectKey);
                            currentUser.put(userObjectKey, userObjectValue);
                        }

                        usersArray.put(i, userDetailObject.toString());
                        resultsList.add(currentUser.toString());
                    } else {
                        resultsList.add(usersArray.get(i).toString());
                    }
                }
            }

            if (indexToDelete != -1) {
                usersArray.remove(indexToDelete);
            }

            sqliteCore.createSetting("usersData", usersArray.toString());

            try {
                localManageCategoriesActivityListAdapter.clear();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            localManageCategoriesActivityListAdapter = new LocalManageCategoriesActivityListAdapter(activity, resultsList);
            mainListView.setAdapter(localManageCategoriesActivityListAdapter);
            localManageCategoriesActivityListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openAlertMessage(Activity activity, String tag, String title, String message, String button1, String button2) {
        closeAlertMessage();
        AlertDialog.Builder alertSettings = new AlertDialog.Builder(this);
        LayoutInflater inflater = activity.getLayoutInflater();
        localGenericAlert = new LocalGenericAlert (
                activity,
                alertSettings,
                inflater.inflate(R.layout.generic_alert, null),
                tag,
                title,
                message,
                button1,
                button2);
        localGenericAlert.getAlert();
    }

    private void closeAlertMessage() {
        try {
            localGenericAlert.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    String result = data.getStringExtra("result");
                    JSONObject userData = new JSONObject(Objects.requireNonNull(data.getStringExtra("data")));
                    updateList(result, userData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}