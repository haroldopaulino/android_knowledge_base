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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ManageCategoriesDetailActivity extends Activity {
	private Activity activity;
	private Utilities utilities;
	private SqliteCore sqliteCore;
	private EditText
			categoryEdittext;
	private Spinner
			approvedSpinner;
	private JSONObject data;
	class LocalHttpClient extends HttpClient {
		LocalHttpClient(Activity inputActivity) {
			super(inputActivity);
		}

		public void callback(JSONObject callbackValues) {
			processCallback(callbackValues);
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
						.put("problem", categoryEdittext.getText().toString())
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
					openAlertMessage(activity, "create", "Create Category?", "Proceed with the creation?", "OK", "CANCEL");
					break;
				case "UPDATE" :
					openAlertMessage(activity, "update", "Update Category?", "Proceed with the update?", "OK", "CANCEL");
					break;
				case "DELETE" :
					openAlertMessage(activity, "delete", "Delete Category?", "Proceed with the deletion?", "OK", "CANCEL");
					break;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_categories_detail_activity);

		activity = this;
		utilities = new Utilities(activity);
		sqliteCore = new SqliteCore(activity);
		try {
			Intent intent = getIntent();
			if (!Objects.equals(intent.getStringExtra("data"), "")) {
				data = new JSONObject(Objects.requireNonNull(intent.getStringExtra("data")));
			} else {
				data = (new JSONObject())
						.put("key", "")
						.put("value", "");
			}
		} catch (Exception e) {
			try {
				data = (new JSONObject())
						.put("key", "")
						.put("value", "");
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}

		setupViews();
		loadApproved();
	}

	private void setupViews() {
		TextView title = findViewById(R.id.title);
		TextView titleMessage = findViewById(R.id.titleMessage);
		titleMessage.setVisibility(View.GONE);

		title.setText("Manage Category");

		ImageView backImage = activity.findViewById(R.id.backImage);
		FrameLayout backImageContainer = activity.findViewById(R.id.backImageContainer);
		backImage.setOnClickListener(new ClickHandler("BACK"));
		backImageContainer.setOnClickListener(new ClickHandler("BACK"));

		categoryEdittext  = findViewById(R.id.categoryEdittext);
		approvedSpinner   = findViewById(R.id.approvedSpinner);
		Button updateButton = findViewById(R.id.updateButton);
		Button deleteButton = findViewById(R.id.deleteButton);
		LinearLayout approvedContainer = findViewById(R.id.approvedContainer);

		try {
			if (!data.has("description") || data.getString("description").trim().equals("")) {
				title.setText("Create Category");
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
				updateButton.setText("UPDATE");
				updateButton.setOnClickListener(new ClickHandler("UPDATE"));
			}

			if (data.has("description") &&
				!data.getString("description").trim().equals("")) {
				categoryEdittext.setText(data.getString("description"));
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
			if (callbackValues.getString("result").toUpperCase().contains("THE CATEGORY ALREADY EXISTS")) {
				openAlertMessage(activity, "not_ok_create", "Success", "The category already exists.", "GOT IT", null);
			}

			if (callbackValues.getString("result").toUpperCase().contains("CREATED")) {
				loadNewCategoriesData(callbackValues.getString("categories_data"));
				openAlertMessage(activity, "ok_create", "Success", "Successful creation.", "GOT IT", null);
			}

			if (callbackValues.getString("result").toUpperCase().contains("UPDATED")) {
				loadNewCategoriesData(callbackValues.getString("categories_data"));
				openAlertMessage(activity, "ok_update", "Success", "Successful update.", "GOT IT", null);
			}

			if (callbackValues.getString("result").toUpperCase().contains("DELETED")) {
				loadNewCategoriesData(callbackValues.getString("categories_data"));
				openAlertMessage(activity, "ok_delete", "Success", "Successful deletion.", "GOT IT", null);
			}
		} catch (Exception e) {
			openAlertMessage(activity, "ok_delete", "Communication Error", "Could not communicate with server.", "GOT IT", null);
			e.printStackTrace();
		}
	}

	private void loadNewCategoriesData(String input) {
		sqliteCore.createSetting("categoriesData", input);
		sqliteCore.createSetting("reload_categories", "YES");
	}

	private void delete() {
		try {
			JSONObject httpClientData = new JSONObject();
			httpClientData
					.put("action", "DELETE_CATEGORY")
					.put("category_id", data.getString("id"));
			LocalHttpClient httpClient = new LocalHttpClient(activity);
			httpClient.runHttpClient(httpClientData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void update() {
		try {
			JSONObject httpClientData = new JSONObject();
			httpClientData
					.put("action", "UPDATE_CATEGORY")
					.put("category_id", data.getString("id"))
					.put("category_description", categoryEdittext.getText().toString())
					.put("approved", (approvedSpinner.getSelectedItemPosition() == 0 ? "Y" : "N"));
			LocalHttpClient httpClient = new LocalHttpClient(activity);
			httpClient.runHttpClient(httpClientData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void create() {
		try {
			JSONObject httpClientData = new JSONObject();
			httpClientData
					.put("action", "CREATE_CATEGORY")
					.put("category_description", categoryEdittext.getText().toString());
            /*
                    1 = Disabled
                    2 = Active Administrator
                    3 = Active Employee
				 */
			if (!sqliteCore.getSetting("login_access_type").equals("2")) {
				httpClientData.put("approved", "N");
			} else {
				httpClientData.put("approved", (approvedSpinner.getSelectedItemPosition() == 0 ? "Y" : "N"));
			}
			LocalHttpClient httpClient = new LocalHttpClient(activity);
			httpClient.runHttpClient(httpClientData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}