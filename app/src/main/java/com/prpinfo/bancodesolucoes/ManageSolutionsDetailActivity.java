package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ManageSolutionsDetailActivity extends Activity {
	private Activity activity;
	private Utilities utilities;
	private SqliteCore sqliteCore;
	private EditText
			problemEdittext,
			solutionEdittext;
	private Spinner
			approvedSpinner,
			categorySpinner;
	private JSONObject data;
	private LocalCategoriesListviewListAdapter localCategoriesListAdapter;
	class LocalHttpClient extends HttpClient {
		LocalHttpClient(Activity inputActivity) {
			super(inputActivity);
		}

		public void callback(JSONObject callbackValues) {
			processCallback(callbackValues);
		}
	}

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
				if (inputResponse.getString("tag").equalsIgnoreCase("update") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					update();
				}

				if (inputResponse.getString("tag").equalsIgnoreCase("create") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					create();
				}

				if (inputResponse.getString("tag").equalsIgnoreCase("delete") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					delete();
				}

				if (inputResponse.getString("tag").equalsIgnoreCase("ok_create") ||
					inputResponse.getString("tag").equalsIgnoreCase("ok_delete") ||
					inputResponse.getString("tag").equalsIgnoreCase("ok_update")) {
					data
						.put("problem", problemEdittext.getText().toString())
						.put("solution", solutionEdittext.getText().toString())
						.put("category_id", utilities.getLocalJsonKeyByIndex("accessTypesData", "key", categorySpinner.getSelectedItemPosition()))
						.put("approved", (approvedSpinner.getSelectedItemPosition() == 0 ? "Y" : "N"));

					Intent intent= new Intent();
					intent.putExtra("result", inputResponse.getString("tag"));
					intent.putExtra("data", data.toString());
					setResult(RESULT_OK,intent);
					finish();
				}

				if (inputResponse.getString("tag").equalsIgnoreCase("not_ok_create")) {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class ClickHandler implements View.OnClickListener {

		String action;

		ClickHandler(String actionInput) {
			action = actionInput;
		}

		@Override
		public void onClick(View view) {
			utilities.dismissKeyboard(activity);
			switch(action) {
				case "BACK" :
					activity.finish();
					break;
				case "CREATE" :
					openAlertMessage(activity, "create", "Create Solution?", "Proceed with the creation?", "OK", "CANCEL");
					break;
				case "UPDATE" :
					openAlertMessage(activity, "update", "Update Solution?", "Proceed with the update?", "OK", "CANCEL");
					break;
				case "DELETE" :
					openAlertMessage(activity, "delete", "Delete Solution?", "Proceed with the deletion?", "OK", "CANCEL");
					break;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_solutions_detail_activity);

		activity = this;
		utilities = new Utilities(activity);
		sqliteCore = new SqliteCore(activity);
		try {
			Intent intent = getIntent();
			data = new JSONObject(Objects.requireNonNull(intent.getStringExtra("data")));
		} catch (Exception e) {
			try {
				data = (new JSONObject())
						.put("category_id", "")
						.put("category_description", "")
						.put("problem", "")
						.put("solution", "")
						.put("solution_id", "")
						.put("approved", "");
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}

		setupViews();
		loadApproved();
		loadCategories();
	}

	private void setupViews() {
		TextView title = findViewById(R.id.title);
		TextView titleMessage = findViewById(R.id.titleMessage);
		titleMessage.setVisibility(View.GONE);

		title.setText("Manage Solution");

		ImageView backImage = activity.findViewById(R.id.backImage);
		FrameLayout backImageContainer = activity.findViewById(R.id.backImageContainer);
		backImage.setOnClickListener(new ClickHandler("BACK"));
		backImageContainer.setOnClickListener(new ClickHandler("BACK"));

		categorySpinner   = findViewById(R.id.categorySpinner);
		problemEdittext   = findViewById(R.id.problemEdittext);
		solutionEdittext  = findViewById(R.id.solutionEdittext);
		approvedSpinner   = findViewById(R.id.approvedSpinner);
		Button updateButton = findViewById(R.id.updateButton);
		Button deleteButton = findViewById(R.id.deleteButton);
		LinearLayout approvedContainer = findViewById(R.id.approvedContainer);

		try {
			if (data.getString("solution_id").trim().equals("")) {
				deleteButton.setVisibility(View.GONE);
				updateButton.setText("CREATE");
				updateButton.setOnClickListener(new ClickHandler("CREATE"));

				/*
                    1 = Disabled
                    2 = Active Administrator
                    3 = Actrive Employee
				 */
				if (!sqliteCore.getSetting("login_access_type").equals("2")) {
                    approvedContainer.setVisibility(View.GONE);
                }
			} else {
				updateButton.setOnClickListener(new ClickHandler("UPDATE"));
			}

			if (data.has("problem") &&
				!data.getString("problem").trim().equals("")) {
				problemEdittext.setText(data.getString("problem"));
				solutionEdittext.setText(data.getString("solution"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		deleteButton.setOnClickListener(new ClickHandler("DELETE"));

		utilities.dismissKeyboard(activity);
	}

	public void loadApproved() {
		try {
			int selectedApprovedIndex = 0;
			ArrayList<String> approvedArray = new ArrayList<>();
			JSONObject approvedData = utilities.getApprovedItems();
			Iterator<?> approvedObjectKeys = approvedData.keys();
			int index = 0;
			while(approvedObjectKeys.hasNext() ) {
				String approvedId = (String)approvedObjectKeys.next();
				String approvedDescription = approvedData.getString(approvedId);
				approvedArray.add(((new JSONObject())
						.put("approved", approvedId)
						.put("text", approvedDescription)).toString());

				if (data.getString("approved").equals(approvedId)) {
					selectedApprovedIndex = index;
				}
				index++;
			}
			approvedSpinner.setAdapter(new LocalApprovalStatusListAdapter(activity, approvedArray));
			approvedSpinner.setSelection(selectedApprovedIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadCategories() {
		try {
			if (data != null &&
				data.has("category_id")) {
				int selectedCategoryIndex = 1;

				ArrayList<String> categoriesArray = new ArrayList<>();
				//categoriesArray.add("<<<-1>>>");
				JSONArray categoriesObject = new JSONArray(sqliteCore.getSetting("categoriesData"));
				for (int i = 0; i < categoriesObject.length(); i++) {
					JSONObject currentCategory = new JSONObject(categoriesObject.get(i).toString());
					String categoryId = currentCategory.getString("id");
					categoriesArray.add(categoriesObject.get(i).toString());

					if (data.getString("category_id").equalsIgnoreCase(categoryId)) {
						selectedCategoryIndex = i;
					}
				}
				localCategoriesListAdapter = new LocalCategoriesListviewListAdapter(activity, categoriesArray);
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
			}
		} catch (Exception e) {
			categorySpinner.setAdapter(null);
			e.printStackTrace();
		}
	}

	public void openAlertMessage(Activity activity, String tag, String title, String message, String button1, String button2) {
		AlertDialog.Builder alertSettings = new AlertDialog.Builder(this);
		LayoutInflater inflater = activity.getLayoutInflater();
		LocalGenericAlert localMenuAlert= new LocalGenericAlert(
				activity,
				alertSettings,
				inflater.inflate(R.layout.generic_alert, null),
				tag,
				title,
				message,
				button1,
				button2);
		localMenuAlert.getAlert();
	}
	
	public void processCallback(JSONObject callbackValues) {
		try {
			if (callbackValues.getString("result").toUpperCase().contains("THE SOLUTION ALREADY EXISTS")) {
				openAlertMessage(activity, "not_ok_create", "Success", "The solution already exists.", "GOT IT", null);
			}

			if (callbackValues.getString("result").toUpperCase().contains("CREATED")) {
				openAlertMessage(activity, "ok_create", "Success", "Successful creation.", "GOT IT", null);
			}

			if (callbackValues.getString("result").toUpperCase().contains("UPDATED")) {
				openAlertMessage(activity, "ok_update", "Success", "Successful update.", "GOT IT", null);
			}

			if (callbackValues.getString("result").toUpperCase().contains("DELETED")) {
				openAlertMessage(activity, "ok_delete", "Success", "Successful deletion.", "GOT IT", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearFocus() {
		solutionEdittext.requestFocus();
		categorySpinner.clearFocus();
		problemEdittext.clearFocus();
		solutionEdittext.clearFocus();
		approvedSpinner.clearFocus();

		utilities.dismissKeyboard(activity);
	}

	private void delete() {
		clearFocus();
		try {
			JSONObject httpClientData = new JSONObject();
			httpClientData
					.put("action", "DELETE_SOLUTION")
					.put("solution_id", data.getString("solution_id"));
			LocalHttpClient httpClient = new LocalHttpClient(activity);
			httpClient.runHttpClient(httpClientData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void update() {
		clearFocus();
		try {
			JSONObject httpClientData = new JSONObject();
			httpClientData
					.put("action", "UPDATE_SOLUTION")
					.put("problem", problemEdittext.getText().toString())
					.put("solution", solutionEdittext.getText().toString())
					.put("solution_id", data.getString("solution_id"))
					.put("category_id", utilities.getLocalJsonKeyByIndex("categoriesData", "key", categorySpinner.getSelectedItemPosition()))
					.put("approved", (approvedSpinner.getSelectedItemPosition() == 0 ? "Y" : "N"));
			LocalHttpClient httpClient = new LocalHttpClient(activity);
			httpClient.runHttpClient(httpClientData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void create() {
		clearFocus();
		try {
			JSONObject httpClientData = new JSONObject();
			httpClientData
					.put("action", "CREATE_SOLUTION")
					.put("problem", problemEdittext.getText().toString())
					.put("solution", solutionEdittext.getText().toString())
					.put("category_id", utilities.getLocalJsonKeyByIndex("categoriesData", "key", categorySpinner.getSelectedItemPosition()));
            if (data.getString("solution_id").trim().equals("")) {
			    /*
                    1 = Disabled
                    2 = Active Administrator
                    3 = Actrive Employee
				 */
                if (!sqliteCore.getSetting("login_access_type").equals("2")) {
                    httpClientData.put("approved", "N");
                } else {
                    httpClientData.put("approved", (approvedSpinner.getSelectedItemPosition() == 0 ? "Y" : "N"));
                }
            }
			LocalHttpClient httpClient = new LocalHttpClient(activity);
			httpClient.runHttpClient(httpClientData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}