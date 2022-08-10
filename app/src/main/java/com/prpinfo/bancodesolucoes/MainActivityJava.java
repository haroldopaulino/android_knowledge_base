package com.prpinfo.bancodesolucoes;

import static android.content.Intent.EXTRA_MIME_TYPES;
import static com.prpinfo.bancodesolucoes.Utilities.IMPORT_CSV_FILE;
import static com.prpinfo.bancodesolucoes.Utilities.REQUEST_WRITE_STORAGE_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivityJava extends Activity {
	private Activity activity;
	private final JSONArray
			mainListObjects = new JSONArray();
	private SqliteCore sqliteCore;
	private Utilities utilities;
	private String
			importCategoryId = "",
			importApproved = "";
	private StringBuilder importedData = new StringBuilder();
	private LocalGenericAlert localGenericAlert;

	class LocalHttpClient extends HttpClient {
		String mode = "";

		public void setMode(String inputMode) {
			mode = inputMode;
		}

		LocalHttpClient(Activity inputActivity) {
			super(inputActivity);
		}

		public void callback(JSONObject callbackValues) {
			processHttpData(callbackValues);
		}
	}

	class LocalMainActivityListAdapter extends MainActivityListAdapter {
		LocalMainActivityListAdapter(Activity inputContext, ArrayList<String> inputList) {
			super(inputContext, inputList);
		}

		@Override
		public void callback(JSONObject callbackObject) {
			int intentResultCode = 1;
			try {
				Intent intent = null;
				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_search_solution", ""))) {
					intent = new Intent(activity, LookupActivity.class);
				}

				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_new_solution", ""))) {
					intent = new Intent(activity, ManageSolutionsDetailActivity.class);
				}

				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_manage_solutions", ""))) {
					intent = new Intent(activity, ManageSolutionsActivity.class);
				}

				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_new_user", ""))) {
					intent = new Intent(activity, MyInfoActivity.class);
					intent.putExtra("mode", "new_user");
				}

				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_manage_users", ""))) {
					intent = new Intent(activity, ManageUsersActivity.class);
					intent.putExtra("data", sqliteCore.getSetting("userData"));
					intent.putExtra("mode", "user_management");
				}

				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_new_category", ""))) {
					intent = new Intent(activity, ManageCategoriesDetailActivity.class);
					intent.putExtra("data", "");
					intent.putExtra("mode", "new_category");
				}

				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_manage_categories", ""))) {
					intent = new Intent(activity, ManageCategoriesActivity.class);
				}

				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_my_info", ""))) {
					intent = new Intent(activity, MyInfoActivity.class);
					intent.putExtra("data", sqliteCore.getSetting("userData"));
					intent.putExtra("mode", "my_info");
				}

				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_import_solutions", ""))) {
					openAlertMessage(activity, "open_csv_options", "Import Solutions?", "Start the Solution Import process?", "OK", "CANCEL");
				}

				if (callbackObject.getString("action").equalsIgnoreCase(utilities.getStringByLanguage(activity, "main_menu_export_solutions", ""))) {
					openAlertMessage(activity, "post_exported_data_to_file", "Export Solutions?", "Start the Solution Export process?", "OK", "CANCEL");
				}
				startActivityForResult(intent, intentResultCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			try {
				if (inputResponse.getString("tag").equalsIgnoreCase("open_csv_options") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					openImportOptionsAlert();
				}

				if (inputResponse.getString("tag").equalsIgnoreCase("show_import_picker") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					openCsvPicker();
				}

				if (inputResponse.getString("tag").equalsIgnoreCase("post_imported_data_to_server") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					postImportedDataToServer();
				}

				if (inputResponse.getString("tag").equalsIgnoreCase("post_exported_data_to_file") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					exportSolutionsToFile();
				}

				if (inputResponse.getString("tag").equalsIgnoreCase("error_exporting_file") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					requestFilePermissionForExportingFile();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class LocalImportOptionsAlert extends ImportOptionsAlert {
		LocalImportOptionsAlert(
				Activity activityInput,
				AlertDialog.Builder alertInput,
				View viewInput,
				String titleInput) {
			super(activityInput, alertInput, viewInput, titleInput);
		}

		@Override
		public void callback(JSONObject inputResponse) {
			try {
				if (inputResponse.getString("return").equalsIgnoreCase("IMPORT")) {
					importCategoryId = inputResponse.getString("category_id");
					importApproved = inputResponse.getString("approved");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			openAlertMessage(activity, "show_import_picker", "Choose a file", "Proceed to choose a CSV file to import data from.", "OK", "CANCEL");
		}
	}

	private class ClickHandler implements View.OnClickListener {

		String action;

		ClickHandler(String actionInput) {
			action = actionInput;
		}

		@Override
		public void onClick(View view) {
			if ("BACK".equals(action)) {
				finish();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		activity = this;
		sqliteCore = new SqliteCore(activity);
		utilities = new Utilities(activity);

		utilities.dismissKeyboard(activity);

		TextView title = findViewById(R.id.title);
		TextView titleMessage = findViewById(R.id.titleMessage);

		title.setText(sqliteCore.getSetting("login_company"));
		titleMessage.setVisibility(View.GONE);

		TextView loggedInTitle = findViewById(R.id.loggedInTitle);
		loggedInTitle.setText(sqliteCore.getSetting("login_email"));

		ImageView backImage = findViewById(R.id.backImage);
		FrameLayout backImageContainer = findViewById(R.id.backImageContainer);
		backImage.setOnClickListener(new ClickHandler("BACK"));
		backImageContainer.setOnClickListener(new ClickHandler("BACK"));

		buildMainObjectsList();
		loadList();
	}

	@Override
	public void onResume() {
		super.onResume();
		utilities.dismissKeyboard(activity);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void buildMainObjectsList() {
		try {
			mainListObjects
					.put(utilities.getStringByLanguage(this, "main_menu_search_solution", ""))
					.put(utilities.getStringByLanguage(this, "main_menu_new_solution", ""))
					.put(utilities.getStringByLanguage(this, "main_menu_manage_solutions", ""))
					.put(utilities.getStringByLanguage(this, "main_menu_new_user", ""))
					.put(utilities.getStringByLanguage(this, "main_menu_manage_users", ""))
					.put(utilities.getStringByLanguage(this, "main_menu_new_category", ""))
					.put(utilities.getStringByLanguage(this, "main_menu_manage_categories", ""))
					.put(utilities.getStringByLanguage(this, "main_menu_my_info", ""))
					.put(utilities.getStringByLanguage(this, "main_menu_import_solutions", ""))
					.put(utilities.getStringByLanguage(this, "main_menu_export_solutions", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadList() {
		ListView mainListView = findViewById(R.id.mainListView);

		ArrayList<String> resultsList = new ArrayList<>();
		try {
			for (int i = 0; i < mainListObjects.length(); i++) {
				resultsList.add(mainListObjects.get(i).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		LocalMainActivityListAdapter adapter = new LocalMainActivityListAdapter(this, resultsList);
		mainListView.setAdapter(adapter);
	}

	private void openAlertMessage(Activity activity, String tag, String title, String message, String button1, String button2) {
		closeAlertMessage();
		AlertDialog.Builder alertSettings = new AlertDialog.Builder(this);
		LayoutInflater inflater = activity.getLayoutInflater();
		localGenericAlert = new LocalGenericAlert(
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

	private void openCsvPicker() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		intent.setType("*/*");
		String[] mimetypes = {"text/comma-separated-values", "text/csv"};
		intent.putExtra(EXTRA_MIME_TYPES, mimetypes);
		startActivityForResult(intent, IMPORT_CSV_FILE);
	}

	private void openImportOptionsAlert() {
		try {
			AlertDialog.Builder alertDetail = new AlertDialog.Builder(activity);
			LayoutInflater inflater = activity.getLayoutInflater();
			LocalImportOptionsAlert localMenuAlert= new LocalImportOptionsAlert(
					activity,
					alertDetail,
					inflater.inflate(R.layout.alert_import_options, null),
					"Import Options"
			);
			localMenuAlert.getAlert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void exportSolutionsToFile() {
		new Thread(() -> activity.runOnUiThread(() -> openAlertMessage(activity, "loading", "Exporting File", "Exporting ...", null, null))).start();

		new Handler().postDelayed(this::executeFileExport, 2000);
	}

	private void executeFileExport() {
		try {
			requestFilePermissionForExportingFile();
			Utilities utilities = new Utilities(this);
			ArrayList<Solution> localSolutionsData = utilities.readSolutionsFromFile();
			if (localSolutionsData.size() > 0) {
				StringBuilder outputData = new StringBuilder();
				for (int i = 0; i < localSolutionsData.size(); i++) {
					Solution tmpObject = localSolutionsData.get(i);
					outputData
							.append("\"")
							.append(tmpObject.problem.replace("\"", "'"))
							.append("\"")
							.append(",")
							.append("\"")
							.append(tmpObject.solution.replace("\"", "'"))
							.append("\"")
							.append("\n");
				}

				File exportFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Knowledge_Base.csv");
				if(!exportFile.exists()){
					try {
						exportFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				FileOutputStream stream = new FileOutputStream(exportFile);
				stream.write(outputData.toString().getBytes());
				stream.close();
				openAlertMessage(activity, "ok", "Export Complete", "Successful export!\n\nLocation: Downloads folder\n\nFile: Knowledge_Base.csv", "OK", null);
			} else {
				openAlertMessage(activity, "error", utilities.getStringByLanguage(activity, "error", ""), "Could not export data to file because there's no data in your device.\nPlease enter create some solutions and try again.", "OK", null);
			}

		} catch (Exception e) {
			openAlertMessage(activity, "error_exporting_file", utilities.getStringByLanguage(activity, "error", ""), "Could not export data to file.\nPlease allow the app to create the file.", "OK", null);
			e.printStackTrace();
		}
	}

	private void requestFilePermissionForExportingFile() {
		if (ContextCompat.checkSelfPermission(this,	Manifest.permission.WRITE_EXTERNAL_STORAGE)	!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{
							Manifest.permission.READ_EXTERNAL_STORAGE,
							Manifest.permission.WRITE_EXTERNAL_STORAGE
					}, REQUEST_WRITE_STORAGE_REQUEST_CODE);
		}
	}

	private void postImportedDataToServer() {
		try {
			JSONObject httpClientData = new JSONObject();
			httpClientData
					.put("action", "IMPORT_DATA")
					.put("approved", importApproved)
					.put("category_id", importCategoryId)
					.put("data", importedData.toString());
			LocalHttpClient httpClient = new LocalHttpClient(this);
			httpClient.setMode("import_data");
			openAlertMessage(activity, "loading", "Importing Data", "Syncing data with server ...", null, null);
			httpClient.runHttpClient(httpClientData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processHttpData(JSONObject callbackValues) {
		try {
			closeAlertMessage();
			if (callbackValues.getString("result").equalsIgnoreCase("IMPORTED")) {
				openAlertMessage(activity, "server_post_result",
					"Success",
					"Imported: " + callbackValues.getString("imported_count") +
							"\nRepeated: " + callbackValues.getString("repeat_repetition_count") +
							"\nRejected: " + callbackValues.getString("repeat_rejection_count"),
					"OK",
					null);
			} else {
				openAlertMessage(activity, "error", utilities.getStringByLanguage(activity, "error", ""), "Error importing data.\nPlease try again.", "OK", null);
			}
		} catch (Exception e) {
			openAlertMessage(activity, "error", utilities.getStringByLanguage(activity, "error", ""), "Error importing data.\nPlease try again.", "OK", null);
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			if (resultCode == RESULT_OK) {
				if (requestCode == IMPORT_CSV_FILE) {
					Uri importUri = data.getData();
					assert importUri != null;
					InputStream inputStream = getContentResolver().openInputStream(importUri);
					try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
						importedData = new StringBuilder();
						int charRead = 0;
						while ((charRead = reader.read()) != -1) {
							importedData.append((char) charRead);
						}
					}
					if (importedData.length() > 0) {
						openAlertMessage(activity, "post_imported_data_to_server", "Save to Server", "Proceed to save the imported data to server?", "OK", "CANCEL");
					} else {
						openAlertMessage(activity, "error", utilities.getStringByLanguage(activity, "error", ""), "Could not import data from file.\nPlease try again.", "OK", null);
					}
				}
			}
		} catch (Exception e) {
			openAlertMessage(activity, "error", utilities.getStringByLanguage(activity, "error", ""), "Error importing data because the file seems to be too large.\nPlease try again with a smaller file.", "OK", null);
			e.printStackTrace();
		}
	}
}