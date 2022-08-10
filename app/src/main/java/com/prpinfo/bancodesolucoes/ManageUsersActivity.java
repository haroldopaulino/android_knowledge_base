package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ManageUsersActivity extends Activity {
	private Activity activity;
	private SqliteCore sqliteCore;
	ArrayList<String> resultsList;
	private ListView mainListView;
	private Spinner accessTypeSpinner;
	private TextView titleMessage;
	private EditText
			firstNameEdittext,
			lastNameEdittext,
			emailEdittext;
	private LocalManageUsersActivityListAdapter localManageUsersActivityListAdapter;
	private Utilities utilities;
	private LocalAccessTypesAdapterListview localAccessTypesAdapter;
	private JSONObject downloadedAccessType;

	class LocalHttpClient extends HttpClient {
		LocalHttpClient(Activity inputActivity) {
			super(inputActivity);
		}

		public void callback(JSONObject callbackValues) {
			processCallback(callbackValues);
		}
	}

	static class LocalAccessTypesAdapterListview extends CategoriesListviewListAdapter {
		LocalAccessTypesAdapterListview(Activity inputContext, ArrayList<String> inputList) {
			super(inputContext, inputList);
		}
	}

	class LocalManageUsersActivityListAdapter extends ManageUsersActivityListAdapter {
		LocalManageUsersActivityListAdapter(Activity inputContext, ArrayList<String> inputList) {
			super(inputContext, inputList);
		}

		@Override
		public void callback(JSONObject callbackObject) {
			manageUserDetailActivity(callbackObject);
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
					runSearch();
					break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_users);

		activity = this;
		utilities = new Utilities(activity);
		sqliteCore = new SqliteCore(activity);

		utilities.dismissKeyboard(this);
		setupViews();
		runSearch();
	}

	private void setupViews() {
		TextView title = findViewById(R.id.title);
		titleMessage = findViewById(R.id.titleMessage);
		title.setText(utilities.getStringByLanguage(this, "manage_users", ""));

		mainListView = findViewById(R.id.mainList);

		ImageView backImage = findViewById(R.id.backImage);
		FrameLayout backImageContainer = findViewById(R.id.backImageContainer);
		backImage.setOnClickListener(new ClickHandler("BACK"));
		backImageContainer.setOnClickListener(new ClickHandler("BACK"));

		accessTypeSpinner = findViewById(R.id.accessTypeSpinner);
		firstNameEdittext = findViewById(R.id.firstNameEdittext);
		lastNameEdittext = findViewById(R.id.lastNameEdittext);
		emailEdittext = findViewById(R.id.emailEdittext);

		Button searchButton = findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new ClickHandler("RUN_SEARCH"));

		populateList();
		loadAccessTypes();
	}

	public void loadAccessTypes() {
		try {
			ArrayList<String> accessTypesArray = new ArrayList<>();
			accessTypesArray.add("<<<-1>>>");
			downloadedAccessType = new JSONObject(sqliteCore.getSetting("accessTypesData"));
			Iterator<?> categoryObjectKeys = downloadedAccessType.keys();
			while(categoryObjectKeys.hasNext() ) {
				String categoryId = (String)categoryObjectKeys.next();
				String categoryDescription = downloadedAccessType.getString(categoryId);
				accessTypesArray.add(new JSONObject().put("id", categoryId).put("description", categoryDescription).toString());
			}
			localAccessTypesAdapter = new LocalAccessTypesAdapterListview(this, accessTypesArray);
			accessTypeSpinner.setAdapter(localAccessTypesAdapter);
			accessTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					localAccessTypesAdapter.setSelectedIndex(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runSearch() {
		try {
			titleMessage.setText("Loading ...");
			JSONObject httpClientData = new JSONObject();
			if (!firstNameEdittext.getText().toString().trim().equals("")) {
				httpClientData.put("first_name", firstNameEdittext.getText().toString().trim());
			}
			if (!lastNameEdittext.getText().toString().trim().equals("")) {
				httpClientData.put("last_name", lastNameEdittext.getText().toString().trim());
			}
			if (!emailEdittext.getText().toString().trim().equals("")) {
				httpClientData.put("email_search", emailEdittext.getText().toString().trim());
			}

			int selectedCategory = accessTypeSpinner.getSelectedItemPosition();
			String selectedAccessTypeKey = "-1";
			if (selectedCategory != 0) {
				selectedAccessTypeKey = utilities.getKeyByIndex(downloadedAccessType, accessTypeSpinner.getSelectedItemPosition() - 1);
			}

			httpClientData
					.put("action", "GET_USERS_LOOKUP_LIST")
					.put("first_name", firstNameEdittext.getText().toString().trim())
					.put("last_name", lastNameEdittext.getText().toString().trim())
					.put("email_search", emailEdittext.getText().toString().trim())
					.put("access_type", selectedAccessTypeKey);
			LocalHttpClient httpClient = new LocalHttpClient(this);
			httpClient.runHttpClient(httpClientData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateList() {
		try {
			String localUserData = sqliteCore.getSetting("usersData");
			if (!localUserData.equals("")) {
				titleMessage.setText("Search");
				resultsList = new ArrayList<>();
				JSONArray usersArray = new JSONArray(localUserData);
				titleMessage.setText("Listing " + usersArray.length() + " users");
				for (int i = 0; i < usersArray.length(); i++) {
					resultsList.add(usersArray.get(i).toString());
				}

				try {
					if (localManageUsersActivityListAdapter != null) {
						localManageUsersActivityListAdapter.clear();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				localManageUsersActivityListAdapter = new LocalManageUsersActivityListAdapter(this, resultsList);
				mainListView.setAdapter(localManageUsersActivityListAdapter);
				localManageUsersActivityListAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processCallback(JSONObject callbackValues) {
		try {
			titleMessage.setText("Search");
			if (callbackValues.has("users_data")) {
				sqliteCore.createSetting("usersData", callbackValues.getString("users_data"));
				resultsList = new ArrayList<>();
				JSONArray solutionsArray = new JSONArray(callbackValues.getString("users_data"));
				titleMessage.setText("Listing " + solutionsArray.length() + " users");
				for (int i = 0; i < solutionsArray.length(); i++) {
					resultsList.add(solutionsArray.get(i).toString());
				}

				try {
					if (localManageUsersActivityListAdapter != null) {
						localManageUsersActivityListAdapter.clear();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				localManageUsersActivityListAdapter = new LocalManageUsersActivityListAdapter(this, resultsList);
				mainListView.setAdapter(localManageUsersActivityListAdapter);
				localManageUsersActivityListAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void manageUserDetailActivity(JSONObject solutionDetailObject) {
		try {
			Intent intent = new Intent(this, MyInfoActivity.class);
			intent.putExtra("data", solutionDetailObject.toString());
			intent.putExtra("mode", "user_management");
			startActivityForResult(intent, 1);
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
				localManageUsersActivityListAdapter.clear();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			localManageUsersActivityListAdapter = new LocalManageUsersActivityListAdapter(activity, resultsList);
			mainListView.setAdapter(localManageUsersActivityListAdapter);
			localManageUsersActivityListAdapter.notifyDataSetChanged();
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