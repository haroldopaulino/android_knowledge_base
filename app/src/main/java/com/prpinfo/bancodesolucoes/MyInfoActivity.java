package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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

import org.json.JSONObject;

import java.util.Objects;

public class MyInfoActivity extends AppCompatActivity {
    private Spinner accessTypeSpinner;
    private EditText
            companyEdittext,
            firstNameEdittext,
            lastNameEdittext,
            address1Edittext,
            address2Edittext,
            cityEdittext,
            stateEdittext,
            zipEdittext,
            phone1Edittext,
            phone2Edittext,
            emailEdittext,
            passwordEdittext;
    private LocalAccessTypesListviewListAdapter localAccessTypesListAdapter;
    private SqliteCore sqliteCore;
    private Utilities utilities;
    private Activity activity;
    private JSONObject data;
    private LocalHttpClient httpClient;
    private String mode = "";

    public MyInfoActivity() {
    }

    class LocalHttpClient extends HttpClient {
        LocalHttpClient(Activity inputActivity) {
            super(inputActivity);
        }

        public void callback(JSONObject callbackValues) {
            processCallback(callbackValues);
        }
    }

    static class LocalAccessTypesListviewListAdapter extends BasicListAdapter {
        LocalAccessTypesListviewListAdapter(Activity inputContext, String[] inputList) {
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
            try {
                if (inputResponse.getString("tag").equalsIgnoreCase("success")) {
                    utilities.dismissKeyboard(activity);
                    finish();
                }

                if (inputResponse.getString("tag").equalsIgnoreCase("registration") &&
                    inputResponse.getString("return").equalsIgnoreCase("ok")) {
                    register();
                    return;
                }

                if (inputResponse.getString("tag").equalsIgnoreCase("update") &&
                    inputResponse.getString("return").equalsIgnoreCase("ok")) {
                    update();
                    return;
                }

                if ((mode.equals("my_info") ||
                     mode.equals("registration") ||
                     mode.equals("create_company")) &&
                    (inputResponse.getString("tag").equalsIgnoreCase("created_success_response") ||
                     inputResponse.getString("tag").equalsIgnoreCase("created_company_success_response"))) {

                    JSONObject dataOut = new JSONObject();
                    try {
                        dataOut
                            .put("mode", mode)
                            .put("login_company", companyEdittext.getText().toString())
                            .put("login_email", emailEdittext.getText().toString().trim())
                            .put("login_password", passwordEdittext.getText().toString().trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent intent= new Intent();
                    intent.putExtra("result", "SUCCESS");
                    intent.putExtra("data", dataOut.toString());
                    setResult(RESULT_OK,intent);
                    utilities.dismissKeyboard(activity);
                    finish();
                }

                if (inputResponse.getString("tag").equalsIgnoreCase("create_company") &&
                    inputResponse.getString("return").equalsIgnoreCase("ok")) {
                    createCompany();
                }

                if (inputResponse.getString("tag").equalsIgnoreCase("created_success_response") ||
                    inputResponse.getString("tag").equalsIgnoreCase("update_success_response") ||
                    inputResponse.getString("tag").equalsIgnoreCase("delete_success_response")) {

                    JSONObject dataOut = new JSONObject();
                    try {
                        int accessType = Integer.parseInt(utilities.getLocalJsonKeyByIndex("accessTypesData", "key", accessTypeSpinner.getSelectedItemPosition() + 1));
                        dataOut
                            .put("mode", mode)
                            .put("id", data.getString("id"))
                            .put("firstname", firstNameEdittext.getText().toString().trim())
                            .put("lastname", lastNameEdittext.getText().toString().trim())
                            .put("address1", address1Edittext.getText().toString().trim())
                            .put("address2", address2Edittext.getText().toString().trim())
                            .put("city", cityEdittext.getText().toString().trim())
                            .put("state", stateEdittext.getText().toString().trim())
                            .put("zip", zipEdittext.getText().toString().trim())
                            .put("phone1", phone1Edittext.getText().toString().trim())
                            .put("phone2", phone2Edittext.getText().toString().trim())
                            .put("updated_email", emailEdittext.getText().toString().trim())
                            .put("access_type", accessType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent intent= new Intent();
                    intent.putExtra("result", inputResponse.getString("tag"));
                    intent.putExtra("data", dataOut.toString());
                    setResult(RESULT_OK,intent);
                    utilities.dismissKeyboard(activity);
                    finish();
                }

                if (inputResponse.getString("tag").equalsIgnoreCase("delete") &&
                    inputResponse.getString("return").equalsIgnoreCase("ok")) {
                    delete();
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
                case "BACK" :
                    utilities.dismissKeyboard(activity);
                    activity.finish();
                    break;
                case "UPDATE" :
                    openAlertMessage(activity, "update", "Confirm", "Update?", "OK", "CANCEL");
                    break;
                case "DELETE" :
                    openAlertMessage(activity, "delete", "Confirm", "Proceed with deletion?", "OK", "CANCEL");
                    break;
                case "REGISTER" :
                    openAlertMessage(activity, "registration", "Confirm", "Finish Registration?", "OK", "CANCEL");
                    break;
                case "CREATE_COMPANY" :
                    openAlertMessage(activity, "create_company", "Confirm", "Create Company?", "OK", "CANCEL");
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info);

        activity = this;
        sqliteCore = new SqliteCore(activity);
        utilities = new Utilities(activity);

        try {
            Intent intent = getIntent();
            mode = intent.hasExtra("mode") ? intent.getStringExtra("mode") : "";
            data = new JSONObject(intent.hasExtra("data") ? Objects.requireNonNull(intent.getStringExtra("data")) : "");
        } catch (Exception e) {
            data = new JSONObject();
            e.printStackTrace();
        }

        utilities.dismissKeyboard(this);
        setupViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupViews() {
        ImageView backImage = findViewById(R.id.backImage);
        FrameLayout backImageContainer = findViewById(R.id.backImageContainer);
        TextView title = findViewById(R.id.title);
        TextView titleMessage = findViewById(R.id.titleMessage);
        LinearLayout accessTypeContainer = findViewById(R.id.accessTypeContainer);
        accessTypeSpinner   = findViewById(R.id.accessTypeSpinner);
        LinearLayout companyContainer = findViewById(R.id.companyContainer);
        companyEdittext     = findViewById(R.id.companyEdittext);
        firstNameEdittext   = findViewById(R.id.firstNameEdittext);
        lastNameEdittext    = findViewById(R.id.lastNameEdittext);
        address1Edittext    = findViewById(R.id.address1Edittext);
        address2Edittext    = findViewById(R.id.address2Edittext);
        cityEdittext        = findViewById(R.id.cityEdittext);
        stateEdittext       = findViewById(R.id.stateEdittext);
        zipEdittext         = findViewById(R.id.zipEdittext);
        phone1Edittext      = findViewById(R.id.phone1Edittext);
        phone2Edittext      = findViewById(R.id.phone2Edittext);
        emailEdittext       = findViewById(R.id.emailEdittext);
        LinearLayout passwordContainer = findViewById(R.id.passwordContainer);
        passwordEdittext    = findViewById(R.id.passwordEdittext);
        Button updateButton = findViewById(R.id.updateButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        backImage.setOnClickListener(new ClickHandler("BACK"));
        backImageContainer.setOnClickListener(new ClickHandler("BACK"));

        switch(mode) {
            case "create_company" :
                deleteButton.setText("CANCEL");
                updateButton.setText("CREATE");
                title.setText("My Info");
                title.setText("Creating a Company");
                updateButton.setOnClickListener(new ClickHandler("CREATE_COMPANY"));
                deleteButton.setOnClickListener(new ClickHandler("BACK"));
                loadAccessTypes();
                break;
            case "registration" :
                deleteButton.setText("CANCEL");
                updateButton.setText("SAVE");
                accessTypeContainer.setVisibility(View.GONE);
                companyEdittext.setText(sqliteCore.getSetting("login_company"));
                title.setText("Registration");
                updateButton.setOnClickListener(new ClickHandler("REGISTER"));
                deleteButton.setOnClickListener(new ClickHandler("BACK"));
                break;
            case "user_management" :
                companyContainer.setVisibility(View.GONE);
                passwordContainer.setVisibility(View.GONE);
                title.setText("User Detail");
                updateButton.setOnClickListener(new ClickHandler("UPDATE"));
                deleteButton.setOnClickListener(new ClickHandler("DELETE"));
                loadAccessTypes();
                loadData();
                break;
            case "my_info" :
                accessTypeContainer.setVisibility(View.GONE);
                deleteButton.setText("CANCEL");
                updateButton.setText("UPDATE");
                companyContainer.setVisibility(View.GONE);
                title.setText("My Information");
                updateButton.setOnClickListener(new ClickHandler("UPDATE"));
                deleteButton.setOnClickListener(new ClickHandler("BACK"));
                loadData();
                break;
            case "new_user" :
                deleteButton.setText("CANCEL");
                updateButton.setText("REGISTER");
                companyContainer.setVisibility(View.GONE);
                title.setText("New User");
                updateButton.setOnClickListener(new ClickHandler("REGISTER"));
                deleteButton.setOnClickListener(new ClickHandler("BACK"));
                loadAccessTypes();
                companyEdittext.setText(sqliteCore.getSetting("login_company"));
                break;
            default:
                utilities.dismissKeyboard(this);
                finish();
                break;
        }

        titleMessage.setVisibility(View.GONE);
    }

    private void loadData() {
        try {
            firstNameEdittext.setText(data.getString("firstname"));
            lastNameEdittext.setText(data.getString("lastname"));
            address1Edittext.setText(data.getString("address1"));
            address2Edittext.setText(data.getString("address2"));
            cityEdittext.setText(data.getString("city"));
            stateEdittext.setText(data.getString("state"));
            zipEdittext.setText(data.getString("zip"));
            phone1Edittext.setText(data.getString("phone1"));
            phone2Edittext.setText(data.getString("phone2"));
            emailEdittext.setText(data.getString("email"));
            if (mode.equals("my_info")) {
                passwordEdittext.setText(new String(Base64.decode(data.getString("password"), Base64.DEFAULT)));
            } else {
                passwordEdittext.setText(data.getString("password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAccessTypes() {
        try {
            localAccessTypesListAdapter = new LocalAccessTypesListviewListAdapter(this, utilities.getArrayByLanguage(activity, "access_types"));
            accessTypeSpinner.setAdapter(localAccessTypesListAdapter);
            if (mode.equals("create_company")) {
                accessTypeSpinner.setSelection(0);
                localAccessTypesListAdapter.setSelectedIndex(0);
            } else {
                accessTypeSpinner.setSelection(data.getInt("access_type") - 1);
                localAccessTypesListAdapter.setSelectedIndex(data.getInt("access_type") - 1);
            }
            accessTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    localAccessTypesListAdapter.setSelectedIndex(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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

    public void processCallback(JSONObject httpResponse) {
        try {
            if (httpResponse.getString("result").equalsIgnoreCase("SUCCESS")) {
                if (httpResponse.getString("operation").equalsIgnoreCase("USER_CREATION")) {
                    openAlertMessage(activity, "created_success_response", "Success", "Successful Registration!", "GOT IT", null);
                }
                if (httpResponse.getString("operation").equalsIgnoreCase("USER_UPDATE")) {
                    if (mode.equals("my_info")) {
                        sqliteCore.createSetting("userData", httpResponse.getString("user_data"));//all data associated with the logged user
                    }
                    openAlertMessage(activity, "update_success_response", "Success", "Successful Update!", "GOT IT", null);
                }
                if (httpResponse.getString("operation").equalsIgnoreCase("USER_DELETION")) {
                    openAlertMessage(activity, "delete_success_response", "Success", "Successful Deletion!", "GOT IT", null);
                }
                if (httpResponse.getString("operation").equalsIgnoreCase("CREATE_COMPANY")) {
                    openAlertMessage(activity, "created_company_success_response", "Success", "Successful Company Creation!", "GOT IT", null);
                }
            } else {
                openAlertMessage(activity, "error", "Error", httpResponse.getString("result"), "GOT IT", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createCompany() {
        JSONObject httpClientData = new JSONObject();
        try {
            httpClientData
                    .put("action", "CREATE_COMPANY")
                    .put("new_company_name", companyEdittext.getText().toString().trim())
                    .put("new_company_email", emailEdittext.getText().toString().trim())
                    .put("new_company_password", passwordEdittext.getText().toString().trim())
                    .put("new_company_firstname", firstNameEdittext.getText().toString().trim())
                    .put("new_company_lastname", lastNameEdittext.getText().toString().trim())
                    .put("new_company_address1", address1Edittext.getText().toString().trim())
                    .put("new_company_address2", address2Edittext.getText().toString().trim())
                    .put("new_company_city", cityEdittext.getText().toString().trim())
                    .put("new_company_state", stateEdittext.getText().toString().trim())
                    .put("new_company_zip", zipEdittext.getText().toString().trim())
                    .put("new_company_phone1", phone1Edittext.getText().toString().trim())
                    .put("new_company_phone2", phone2Edittext.getText().toString().trim())
                    .put("access_type", accessTypeSpinner.getSelectedItemPosition() + 1);
            httpClient = new LocalHttpClient(this);
            httpClient.runHttpClient(httpClientData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        JSONObject httpClientData = new JSONObject();
        try {
            httpClientData
                    .put("action", "UPDATE_EXISTING_USER")
                    .put("mode", mode)
                    .put("id", data.getString("id"))
                    .put("firstname", firstNameEdittext.getText().toString().trim())
                    .put("lastname", lastNameEdittext.getText().toString().trim())
                    .put("address1", address1Edittext.getText().toString().trim())
                    .put("address2", address2Edittext.getText().toString().trim())
                    .put("city", cityEdittext.getText().toString().trim())
                    .put("state", stateEdittext.getText().toString().trim())
                    .put("zip", zipEdittext.getText().toString().trim())
                    .put("phone1", phone1Edittext.getText().toString().trim())
                    .put("phone2", phone2Edittext.getText().toString().trim())
                    .put("updated_email", emailEdittext.getText().toString().trim())
                    .put("updated_password", passwordEdittext.getText().toString().trim())
                    .put("access_type", accessTypeSpinner.getSelectedItemPosition() + 1);
            httpClient = new LocalHttpClient(this);
            httpClient.runHttpClient(httpClientData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        JSONObject httpClientData = new JSONObject();
        try {
            httpClientData
                    .put("action", "DELETE_EXISTING_USER")
                    .put("id", data.getString("id"));
            httpClient = new LocalHttpClient(this);
            httpClient.runHttpClient(httpClientData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register() {
        String errorMessage = "";
        errorMessage = companyEdittext.getText().toString().trim().equals("")   ? "The Company Name cannot be blank."                 : errorMessage;
        errorMessage = emailEdittext.getText().toString().trim().equals("")     ? (errorMessage + "\nThe Email cannot be blank.")     : errorMessage;
        errorMessage = passwordEdittext.getText().toString().trim().equals("")  ? (errorMessage + "\nThe Password cannot be blank.")  : errorMessage;
        errorMessage = firstNameEdittext.getText().toString().trim().equals("") ? "\nThe First Name cannot be blank."                 : errorMessage;
        errorMessage = lastNameEdittext.getText().toString().trim().equals("")  ? (errorMessage + "\nThe Last Name cannot be blank.") : errorMessage;

        if (errorMessage.equals("")) {
            JSONObject httpClientData = new JSONObject();

            try {
                httpClientData
                        .put("action", "CREATE_USER")
                        .put("registration_access_type", accessTypeSpinner.getSelectedItemPosition() + 1)
                        .put("registration_mode", mode)
                        .put("registration_company_name", companyEdittext.getText().toString().trim())
                        .put("registration_firstname", firstNameEdittext.getText().toString().trim())
                        .put("registration_lastname", lastNameEdittext.getText().toString().trim())
                        .put("registration_address1", address1Edittext.getText().toString().trim())
                        .put("registration_address2", address2Edittext.getText().toString().trim())
                        .put("registration_city", cityEdittext.getText().toString().trim())
                        .put("registration_state", stateEdittext.getText().toString().trim())
                        .put("registration_zip", zipEdittext.getText().toString().trim())
                        .put("registration_phone1", phone1Edittext.getText().toString().trim())
                        .put("registration_phone2", phone2Edittext.getText().toString().trim())
                        .put("registration_email", emailEdittext.getText().toString().trim())
                        .put("registration_password", passwordEdittext.getText().toString());//this only works in "personal_info" mode, not "user_management"
                httpClient = new LocalHttpClient(this);
                httpClient.runHttpClient(httpClientData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            openAlertMessage(activity, "failure", "Error", errorMessage, "GOT IT", null);
        }
    }
}