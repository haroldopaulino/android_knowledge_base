package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

public class LoginActivity extends Activity {
	private ImageView imageTitle;
	private EditText
			companyEdittext,
			emailEdittext,
			passwordEdittext;
	public Spinner languagesSpinner;
	private TextView
			passwordTextview,
			companyTextview,
			registerTextview,
			newCompanyTextview;
	private CheckBox rememberMeCheckbox;
	public Activity activity;
	private SqliteCore sqliteCore;
	private Utilities utilities;
	private LocalHttpClient httpClient;
	private LocalGenericAlert localGenericAlert;
	private boolean skipSpinnerEvent = false;
	
	class LocalHttpClient extends HttpClient {
		String mode = "";

		public void setMode(String inputMode) {
			mode = inputMode;
		}

		LocalHttpClient(Activity inputActivity) {
			super(inputActivity);
		}

		public void callback(JSONObject callbackValues) {
			processHttpLoginData(mode, callbackValues);
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
				if (inputResponse.getString("tag").equalsIgnoreCase("register") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					register();
				}

				if (inputResponse.getString("tag").equalsIgnoreCase("new_company") &&
					inputResponse.getString("return").equalsIgnoreCase("ok")) {
					newCompany();
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
			switch(action) {
				case "LOGIN" :
					login();
					break;
				case "REGISTER" :
					openAlertMessage(activity, "register", utilities.getStringByLanguage(activity, "registration", ""), utilities.getStringByLanguage(activity, "registration_confirmation", ""), "OK", "CANCEL");
					break;
				case "CREATE_COMPANY" :
					openAlertMessage(activity, "new_company", utilities.getStringByLanguage(activity, "new_company", ""), utilities.getStringByLanguage(activity, "new_company_confirmation", ""), "OK", "CANCEL");
					break;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		sqliteCore = new SqliteCore(this);
		activity = this;
		utilities = new Utilities(activity);

		utilities.dismissKeyboard(activity);
		setupViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		utilities.dismissKeyboard(activity);
		updateViewsAndDatabase();
	}

	private void setupViews() {
		skipSpinnerEvent = true;

		imageTitle         = findViewById(R.id.imageTitle);
		companyEdittext    = findViewById(R.id.companyEdittext);
		emailEdittext      = findViewById(R.id.emailEdittext);
		passwordEdittext   = findViewById(R.id.passwordEdittext);
		languagesSpinner   = findViewById(R.id.spinnerLanguages);
		Button loginButton = findViewById(R.id.loginButton);
		registerTextview   = findViewById(R.id.registerTextview);
		newCompanyTextview = findViewById(R.id.newCompanyTextview);
		passwordTextview   = findViewById(R.id.passwordTextview);
		companyTextview    = findViewById(R.id.companyTextview);
		rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);

		rememberMeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> sqliteCore.createSetting("login_remember_password", isChecked ? "true" : "false"));
		if (sqliteCore.getSetting("login_remember_password").equals("true")) {
			companyEdittext.setText(sqliteCore.getSetting("login_company"));
			emailEdittext.setText(sqliteCore.getSetting("login_email"));
			passwordEdittext.setText(sqliteCore.getSetting("login_password"));
			rememberMeCheckbox.setChecked(true);
			login();
		}
		setLanguagesSpinnerEvent();
		updateViewsAndDatabase();
		companyEdittext.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				login();
			}
			return false;
		});
		loginButton.setOnClickListener(new ClickHandler("LOGIN"));
		registerTextview.setOnClickListener(new ClickHandler("REGISTER"));
		newCompanyTextview.setOnClickListener(new ClickHandler("CREATE_COMPANY"));
	}

	private void setLanguagesSpinnerEvent() {
		languagesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> spinner, View container, int position, long id) {
				if (skipSpinnerEvent) {
					skipSpinnerEvent = false;
					return;
				}
				switch (position) {
					case 0:
						sqliteCore.createSetting("login_language", "english");
						break;
					case 1:
						sqliteCore.createSetting("login_language", "spanish");
						break;
					case 2:
						sqliteCore.createSetting("login_language", "french");
						break;
					case 3:
						sqliteCore.createSetting("login_language", "portuguese");
						break;
				}

				updateViewsAndDatabase();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		languagesSpinner.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				skipSpinnerEvent = false;
				return false;
			}
		});
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
	
	public void updateViewsAndDatabase() {
		String languageValue = sqliteCore.getSetting("login_language");
		if (!languageValue.trim().equals("")) {
			skipSpinnerEvent = true;
			switch (languageValue.substring(0, 3)) {
				case "eng":
					languageValue = "english";
					languagesSpinner.setSelection(0);
					imageTitle.setImageResource(R.drawable.title_english_1);
					break;
				case "spa":
					languageValue = "spanish";
					languagesSpinner.setSelection(1);
					imageTitle.setImageResource(R.drawable.title_spanish_1);
					break;
				case "fra":
				case "fre":
					languageValue = "french";
					languagesSpinner.setSelection(2);
					imageTitle.setImageResource(R.drawable.title_french_1);
					break;
				case "por":
					languageValue = "portuguese";
					languagesSpinner.setSelection(3);
					imageTitle.setImageResource(R.drawable.title_portuguese_1);
					break;
			}
			imageTitle.refreshDrawableState();
		} else {
			languageValue = "english";
		}

		sqliteCore.createSetting("login_language", languageValue);
		sqliteCore.createSetting("login_company", companyEdittext.getText().toString());
		sqliteCore.createSetting("login_email", emailEdittext.getText().toString());
		sqliteCore.createSetting("login_password", passwordEdittext.getText().toString());

		registerTextview.setText(utilities.getStringByLanguage(this, "register", "lowercase"));
		newCompanyTextview.setText(utilities.getStringByLanguage(this, "new_company", "lowercase"));
		passwordTextview.setText(utilities.getStringByLanguage(this, "password", "lowercase"));
		companyTextview.setText(utilities.getStringByLanguage(this, "company", "lowercase"));
		rememberMeCheckbox.setText(utilities.getStringByLanguage(this, "remember_me", "lowercase"));
	}
	
	private void login() {
        if (!companyEdittext.getText().toString().equals("") &&
			!emailEdittext.getText().toString().equals("") &&
			!passwordEdittext.getText().toString().equals("")) {

			sqliteCore.createSetting("login_company", companyEdittext.getText().toString());
			sqliteCore.createSetting("login_email", emailEdittext.getText().toString());
			sqliteCore.createSetting("login_password", passwordEdittext.getText().toString());

		    JSONObject httpClientData = new JSONObject();
		    try {
                httpClientData
                        .put("action", "LOGIN")
						.put("login_attempt", "1");
                httpClient = new LocalHttpClient(this);
				httpClient.setMode("get_login_data");
				openAlertMessage(activity, "loading", utilities.getStringByLanguage(activity, "registration", "lowercase"), utilities.getStringByLanguage(activity, "logging_in", ""), null, null);
                httpClient.runHttpClient(httpClientData);
            } catch (Exception e) {
		        e.printStackTrace();
            }
		} else {
			openAlertMessage(activity, "invalid_login", utilities.getStringByLanguage(activity, "error", ""), utilities.getStringByLanguage(activity, "login_invalid_message", ""), "ok", null);
		}
	}

	private void register() {
		try {
			Intent intent = new Intent(this, MyInfoActivity.class);
			intent.putExtra("mode", "registration");
			startActivityForResult(intent, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void newCompany() {
		try {
			Intent intent = new Intent(this, MyInfoActivity.class);
			intent.putExtra("mode", "create_company");
			startActivityForResult(intent, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String parseLanguage(String inputLanguage) {
		String output = "english";
		if (inputLanguage.substring(0,3).equalsIgnoreCase("eng")) {
    		output = "english";
    	}
    	if (inputLanguage.substring(0,3).equalsIgnoreCase("esp")) {
    		output = "spanish";
    	}
    	if (inputLanguage.substring(0,3).equalsIgnoreCase("fra")) {
    		output = "french";
    	}
    	if (inputLanguage.substring(0,3).equalsIgnoreCase("por")) {
    		output = "portuguese";
    	}
    	return output.toLowerCase();
	}
	
	public void processHttpLoginData(String inputMode, JSONObject httpResponse) {
		switch (inputMode) {
			case "get_login_data" :
				String parsedLanguage = parseLanguage(languagesSpinner.getSelectedItem().toString().trim());
				try {
					if (httpResponse.has("access_type") && //the actual access type associated with the user
						httpResponse.has("access_types") &&//the list of access types with code and description
						httpResponse.has("firstname") &&
						httpResponse.has("lastname") &&
						httpResponse.has("categories_data")) {

						if (!httpResponse.getJSONObject("access_types").getString(httpResponse.getString("access_type")).equalsIgnoreCase("disabled")) {
							if (sqliteCore.getSetting("login_language").trim().equals("")) {
								sqliteCore.createSetting("login_language", parsedLanguage);
							}
							sqliteCore.createSetting("login_company_id", httpResponse.getString("company_id"));
							sqliteCore.createSetting("userData", httpResponse.getString("user_data"));//all data associated with the logged user
							sqliteCore.createSetting("accessTypesData", httpResponse.getString("access_types"));
							sqliteCore.createSetting("login_company_description", httpResponse.getString("company_description"));
							sqliteCore.createSetting("login_access_type", httpResponse.getString("access_type"));
							sqliteCore.createSetting("login_firstname", httpResponse.getString("firstname"));
							sqliteCore.createSetting("login_lastname", httpResponse.getString("lastname"));

							if (!sqliteCore.getSetting("solutions_modified_date").equalsIgnoreCase(httpResponse.getString("solutions_modified_date"))) {
								sqliteCore.createSetting("solutions_modified_date", httpResponse.getString("solutions_modified_date"));
								getSolutionsData();
							} else {
								closeAlertMessage();
								startMainActivity();
							}
						} else {
							openAlertMessage(activity, "invalid_login", utilities.getStringByLanguage(activity, "error", ""), utilities.getStringByLanguage(activity, "disabled_user_message", ""), "ok", null);
						}
					} else {
						openAlertMessage(activity, "invalid_login", utilities.getStringByLanguage(activity, "error", ""), utilities.getStringByLanguage(activity, "login_invalid_message", ""), "ok", null);
					}
				} catch (Exception e) {
					openAlertMessage(activity, "invalid_login", utilities.getStringByLanguage(activity, "error", ""), utilities.getStringByLanguage(activity, "login_invalid_message", ""), "ok", null);
					e.printStackTrace();
				}
				break;
			case "get_solutions_data" :
					try {
						if (httpResponse.has("result") &&
							httpResponse.getString("result").equalsIgnoreCase("OK")) {
							closeAlertMessage();
							startMainActivity();
						}
					} catch (Exception e) {
						openAlertMessage(activity, "invalid_login", utilities.getStringByLanguage(activity, "error", ""), utilities.getStringByLanguage(activity, "login_invalid_message", ""), "ok", null);
						e.printStackTrace();
					}
					break;
		}
	}

	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivityJava.class);
		startActivity(intent);
	}

	private void getSolutionsData() {
		try {
			JSONObject httpClientData = new JSONObject();
			httpClientData
					.put("action", "GET_SOLUTIONS_LOOKUP_LIST")
					.put("category_id", "")
					.put("problem", "")
					.put("solution", "")
					.put("approved", "");
			httpClient = new LocalHttpClient(this);
			httpClient.setMode("get_solutions_data");
			httpClient.runHttpClient(httpClientData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeAlertMessage() {
		try {
			localGenericAlert.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){

		}
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		super.onActivityResult(requestCode, resultCode, data);

		try {
			if (resultCode == RESULT_OK) {
				if (data.hasExtra("result") &&
				    data.hasExtra("data")) {
					String resultString = data.getStringExtra("result");
					JSONObject resultData = new JSONObject(data.getStringExtra("data"));
					if (resultData.has("mode")) {
						if (resultData.getString("mode").equals("registration") ||
							resultData.getString("mode").equals("create_company")) {
							if (resultString.equals("SUCCESS")) {
								sqliteCore.createSetting("login_company", resultData.getString("login_company"));
								sqliteCore.createSetting("login_email", resultData.getString("login_email"));
								sqliteCore.createSetting("login_password", resultData.getString("login_password"));

								if (sqliteCore.getSetting("login_remember_password").equals("true")) {
									companyEdittext.setText(sqliteCore.getSetting("login_company"));
									emailEdittext.setText(sqliteCore.getSetting("login_email"));
									passwordEdittext.setText(sqliteCore.getSetting("login_password"));
									login();
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
