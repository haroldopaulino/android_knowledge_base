package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

abstract class ImportOptionsAlert {
    private final Activity activity;
    private final SqliteCore sqliteCore;
    private final Utilities utilities;
    private final View view;
    private final AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private final String
            title;
    private Spinner
            categorySpinner,
            approvedSpinner;

    static class LocalCategoriesListviewListAdapter extends CategoriesListviewListAdapter {
        LocalCategoriesListviewListAdapter(Activity inputContext, ArrayList<String> inputList) {
            super(inputContext, inputList);
        }
    }

    static class LocalApprovalStatusListAdapter extends ApprovalStatusListAdapter {
        LocalApprovalStatusListAdapter(Activity inputContext, ArrayList<String> inputList) {
            super(inputContext, inputList);
        }
    }

    ImportOptionsAlert(
            Activity activityInput,
            AlertDialog.Builder alertInput,
            View viewInput,
            String titleInput) {
        activity = activityInput;
        alert = alertInput;
        view = viewInput;
        title = titleInput;
        sqliteCore = new SqliteCore(activity);
        utilities = new Utilities(activity);
        buildLayout();
    }

    private void buildLayout() {
        TextView alertTitle = view.findViewById(R.id.title);
        alertTitle.setText(title);

        view.findViewById(R.id.titleMessage).setVisibility(View.GONE);

        view.findViewById(R.id.backImageContainer).setOnClickListener(arg0 -> {
            OptionDialog.dismiss();
            try {
                callback((new JSONObject())
                        .put("return", "BACK"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.backImage).setOnClickListener(arg0 -> {
            OptionDialog.dismiss();
            try {
                callback((new JSONObject())
                        .put("return", "BACK"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button importButton = view.findViewById(R.id.importButton);
        importButton.setOnClickListener(arg0 -> returnImportOptions());

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(arg0 -> {
            OptionDialog.dismiss();
            try {
                callback((new JSONObject())
                        .put("return", "CANCEL"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        categorySpinner = view.findViewById(R.id.categorySpinner);
        approvedSpinner = view.findViewById(R.id.approvedSpinner);
        loadApproved();
        loadCategories();

        alert.setView(view);
        OptionDialog = alert.create();
        OptionDialog.setOnDismissListener(dialogInterface -> sqliteCore.createSetting("ERROR_DIALOG_CURRENTLY_SHOWING", "NO"));
        //OptionDialog.setCancelable(false);//ALLOW THE DEVICE'S BACK BUTTON TO CLOSE THE ALERT OR NOT

        Objects.requireNonNull(OptionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ffffff")));
        OptionDialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(OptionDialog.getWindow().getAttributes());

        OptionDialog.getWindow().setAttributes(layoutParams);
    }

    public AlertDialog.Builder getAlert() {
        return alert;
    }

    public void close() {
        try {
            OptionDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadApproved() {
        try {
            int selectedApprovedIndex = 0;
            ArrayList<String> approvedArray = new ArrayList<>();
            JSONObject approvedData = utilities.getApprovedItems();
            Iterator<?> approvedObjectKeys = approvedData.keys();
            while(approvedObjectKeys.hasNext() ) {
                String approvedId = (String)approvedObjectKeys.next();
                String approvedDescription = approvedData.getString(approvedId);
                approvedArray.add(((new JSONObject())
                        .put("approved", approvedId)
                        .put("text", approvedDescription)).toString());
            }
            approvedSpinner.setAdapter(new LocalApprovalStatusListAdapter(activity, approvedArray));
            approvedSpinner.setSelection(selectedApprovedIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCategories() {
        try {
            int selectedCategoryIndex = 1;

            ArrayList<String> categoriesArray = new ArrayList<>();
            JSONArray categoriesObject = new JSONArray(sqliteCore.getSetting("categoriesData"));
            for (int i = 0; i < categoriesObject.length(); i++) {
                categoriesArray.add(categoriesObject.get(i).toString());
            }
            final LocalCategoriesListviewListAdapter localCategoriesListAdapter = new LocalCategoriesListviewListAdapter(activity, categoriesArray);
            categorySpinner.setAdapter(localCategoriesListAdapter);
            categorySpinner.setSelection(selectedCategoryIndex);
            localCategoriesListAdapter.setSelectedIndex(selectedCategoryIndex);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    localCategoriesListAdapter.setSelectedIndex(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void returnImportOptions() {
        OptionDialog.dismiss();
        try {
            callback((new JSONObject())
                    .put("return", "IMPORT")
                    .put("category_id", getSelectedCategoryId())
                    .put("approved", getSelectedApprovedCode()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSelectedCategoryId() {
        return utilities.getLocalJsonKeyByIndex("categoriesData", "key", categorySpinner.getSelectedItemPosition());
    }

    private String getSelectedApprovedCode() {
        return (approvedSpinner.getSelectedItemPosition() == 0 ? "Y" : "N");
    }

    public void callback(JSONObject inputResponse) { }
}