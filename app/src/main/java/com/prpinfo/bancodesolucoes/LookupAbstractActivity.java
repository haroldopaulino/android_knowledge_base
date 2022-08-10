package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class LookupAbstractActivity {

    private final Activity activity;
    private ListView mainListView;
    private Spinner
            categorySpinner,
            approvalSpinner;
    private TextView titleMessage;
    private EditText
            problemEdittext,
            solutionEdittext;
    private LocalLookupActivityListAdapter localLookupActivityListAdapter;
    private final Utilities utilities;
    private JSONArray downloadedCategories;
    private String mode = "lookup";
    private LocalCategoriesListviewListAdapter localCategoriesListAdapter;

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

    class LocalLookupActivityListAdapter extends LookupActivityListAdapter {
        LocalLookupActivityListAdapter(Activity inputContext, ArrayList<Solution> inputList) {
            super(inputContext, inputList);
        }

        @Override
        public void callback(Solution solution) {
            try {
                JSONObject solutionJsonData = new JSONObject()
                        .put("category_id", solution.category_id)
                        .put("category_description", solution.category_description)
                        .put("problem", solution.problem)
                        .put("solution", solution.solution)
                        .put("solution_id", solution.solution_id)
                        .put("approved", solution.approved);
                switch (mode) {
                    case "lookup":
                        viewSolutionDetailDialog(solutionJsonData);
                        break;
                    case "manage_solutions":
                        manageSolutionDetailActivity(solutionJsonData);
                        break;
                }
            } catch (Exception ignored) {

            }
        }
    }

    class LocalLookupSolutionDetailAlert extends LookupSolutionDetailAlert {
        LocalLookupSolutionDetailAlert(
                Activity activityInput,
                AlertDialog.Builder alertInput,
                View viewInput,
                JSONObject dataInput) {
            super(activityInput, alertInput, viewInput, dataInput);
        }

        @Override
        public void callback(JSONObject inputResponse) {
            viewSolutionDetailDialog(inputResponse);
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
                    runLocalSearch();
                    break;
            }
        }
    }

    LookupAbstractActivity(Activity inputActivity, String inputMode) {
        activity = inputActivity;
        mode = inputMode;
        utilities = new Utilities(activity);
        SqliteCore sqliteCore = new SqliteCore(activity);

        try {
            downloadedCategories = new JSONArray(sqliteCore.getSetting("categoriesData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setupViews();
        runLocalSearch();
    }

    private void setupViews() {
        TextView title = activity.findViewById(R.id.title);
        titleMessage = activity.findViewById(R.id.titleMessage);
        titleMessage.setText("");
        switch (mode) {
            case "lookup" :
                title.setText("Search ");
                break;
            case "manage_solutions" :
                title.setText("Manage Solutions");
                break;
        }

        mainListView = activity.findViewById(R.id.mainList);

        ImageView backImage = activity.findViewById(R.id.backImage);
        FrameLayout backImageContainer = activity.findViewById(R.id.backImageContainer);
        backImage.setOnClickListener(new ClickHandler("BACK"));
        backImageContainer.setOnClickListener(new ClickHandler("BACK"));

        categorySpinner = activity.findViewById(R.id.categorySpinner);
        approvalSpinner = activity.findViewById(R.id.approvalSpinner);
        problemEdittext = activity.findViewById(R.id.problemEdittext);
        solutionEdittext = activity.findViewById(R.id.solutionEdittext);

        if (mode.equals("lookup")) {
            (activity.findViewById(R.id.ApprovalContainer)).setVisibility(View.GONE);
        }

        Button searchButton = activity.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new ClickHandler("RUN_SEARCH"));

        loadCategories();
        loadApproved();
    }

    private void runLocalSearch() {
        try {
           new Thread(() -> {
               try {
                   populateList();
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateList() {
        titleMessage.setText("Search");
        new Thread(() -> {
            try {
                ArrayList<Solution> localSolutionsArray = utilities.readSolutionsFromFile();
                if (localSolutionsArray.size() > 0) {
                    ArrayList<Solution> resultsList = new ArrayList<>();
                    activity.runOnUiThread(() -> {
                        try {
                            String
                                    problem = "",
                                    solution = "",
                                    approved;
                            if (!problemEdittext.getText().toString().trim().equals("")) {
                                problem = problemEdittext.getText().toString().trim().toLowerCase();
                            }
                            if (!solutionEdittext.getText().toString().trim().equals("")) {
                                solution = solutionEdittext.getText().toString().trim().toLowerCase();
                            }
                            int category = Integer.parseInt(utilities.getJsonObjectValueFromArrayIndexAndKey(downloadedCategories, categorySpinner.getSelectedItemPosition(), "id"));

                            if (mode.equals("lookup")) {
                                approved = "Y";
                            } else {
                                approved = (approvalSpinner.getSelectedItemPosition() == 0 ? "Y" : "N");
                            }

                            Solution tmpSolution;
                            boolean addRecord;
                            for (int i = 0; i < localSolutionsArray.size(); i++) {
                                addRecord = false;
                                tmpSolution = localSolutionsArray.get(i);
                                if (((problem.equals("") && solution.equals("") && (category == 1)) ||
                                        (!problem.equals("") && tmpSolution.problem.toLowerCase().contains(problem)) ||
                                        (!solution.equals("") && tmpSolution.solution.toLowerCase().contains(solution)) ||
                                        (!(category == 1) && (tmpSolution.category_id == category))) &&
                                        (tmpSolution.approved.equals(approved))) {
                                    addRecord = true;
                                }

                                if (addRecord) {
                                    resultsList.add(tmpSolution);
                                }
                            }
                            titleMessage.setText("Listing " + resultsList.size() + " items");
                            if (localLookupActivityListAdapter != null) {
                                localLookupActivityListAdapter.clear();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        localLookupActivityListAdapter = new LocalLookupActivityListAdapter(activity, resultsList);
                        mainListView.setAdapter(localLookupActivityListAdapter);
                        localLookupActivityListAdapter.notifyDataSetChanged();
                    });
                } else {
                    titleMessage.setText("Empty Database");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void loadCategories() {
        try {
            ArrayList<String> categoriesArray = new ArrayList<>();
            for (int i = 0; i < downloadedCategories.length(); i++) {
                JSONObject currentCategory = new JSONObject(downloadedCategories.get(i).toString());
                if (!mode.equals("manage_solutions")) {
                    if (currentCategory.getString("approved").equals("Y")) {
                        categoriesArray.add(currentCategory.toString());
                    }
                } else {
                    categoriesArray.add(currentCategory.toString());
                }
            }

            localCategoriesListAdapter = new LocalCategoriesListviewListAdapter(activity, categoriesArray);
            categorySpinner.setAdapter(localCategoriesListAdapter);
            localCategoriesListAdapter.setSelectedIndex(0);
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
            approvalSpinner.setAdapter(new LocalApprovalStatusListAdapter(activity, approvedArray));
            approvalSpinner.setSelection(selectedApprovedIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewSolutionDetailDialog(JSONObject solutionDetailObject) {
        try {
            AlertDialog.Builder alertDetail = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            LocalLookupSolutionDetailAlert localMenuAlert= new LocalLookupSolutionDetailAlert(
                    activity,
                    alertDetail,
                    inflater.inflate(R.layout.alert_lookup_solution_detail, null),
                    solutionDetailObject);
            localMenuAlert.getAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent intent = new Intent(activity, LocalLookupSolutionDetailAlert.class);
            intent.putExtra("data", solutionDetailObject.toString());
            activity.startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageSolutionDetailActivity(JSONObject solutionDetailObject) {
        try {
            Intent intent = new Intent(activity, ManageSolutionsDetailActivity.class);
            intent.putExtra("data", solutionDetailObject.toString());
            activity.startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateList() {
        try {
            utilities.dismissKeyboard(activity);
            ArrayList<Solution> solutionsArray = utilities.readSolutionsFromFile();
            titleMessage.setText("Listing " + solutionsArray.size() + " items");

            try {
                localLookupActivityListAdapter.clear();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            localLookupActivityListAdapter = new LocalLookupActivityListAdapter(activity, solutionsArray);
            mainListView.setAdapter(localLookupActivityListAdapter);
            localLookupActivityListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
