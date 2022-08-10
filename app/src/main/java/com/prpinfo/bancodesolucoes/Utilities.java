package com.prpinfo.bancodesolucoes;

import static android.Manifest.permission.READ_CONTACTS;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

public class Utilities {
    private final Context context;
    private final SqliteCore sqliteCore;
    private final Languages languages;
    static final int REQUEST_READ_CONTACTS = 444;
    static final int IMPORT_CSV_FILE = 123;
    static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 789;

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
    }

    Utilities(Context inputContext) {
        context = inputContext;
        sqliteCore = new SqliteCore(context);
        languages = new Languages();
    }

    JSONObject getApprovedItems() {
        try {
            return (new JSONObject())
                    .put("Y", "YES")
                    .put("N", "NO");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    String getLocalJsonKeyByIndex(String localJsonObject, String valueType, int inputIndex) {
        try {
            JSONObject categoryObject = new JSONObject(sqliteCore.getSetting(localJsonObject));
            Iterator<?> categoryObjectKeys = categoryObject.keys();
            int index = 1;
            while(categoryObjectKeys.hasNext() ) {
                String categoryId = (String)categoryObjectKeys.next();
                String categoryDescription = categoryObject.getString(categoryId);

                if (index == inputIndex) {
                    if (valueType.equalsIgnoreCase("key")) {
                        return categoryId;
                    } else {
                        return categoryDescription;
                    }
                }
                index++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    String getStringByLanguage(Context contextInput, String targetString, String caseInput) {
        String output = targetString;
        SqliteCore sqliteCore;
        try {
            sqliteCore = new SqliteCore(contextInput);
            output = (String) contextInput.getResources().getText(contextInput.getResources().getIdentifier(targetString + "_" + sqliteCore.getSetting("login_language"), "string", contextInput.getPackageName()));
            if (caseInput.equalsIgnoreCase("lowercase")) {
                return output.toLowerCase(Locale.ROOT);
            }
            if (caseInput.equalsIgnoreCase("uppercase")) {
                return output.toUpperCase(Locale.ROOT);
            }
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    String[] getArrayByLanguage(Context contextInput, String targetString) {
        String[] output = null;
        SqliteCore sqliteCore;
        try {
            sqliteCore = new SqliteCore(contextInput);
            output = contextInput.getResources().getStringArray(contextInput.getResources().getIdentifier(targetString + "_" + sqliteCore.getSetting("login_language"), "array", contextInput.getPackageName()));

            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public JSONObject parseStringToSingleJsonObjectKeyValue(String inputString) {
        JSONObject output = new JSONObject();
        try {
            output = new JSONObject(inputString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    String generateTimestamp(String customPattern) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat outputFormat = new SimpleDateFormat(pattern);
        try {
            if (customPattern != null &&
                !customPattern.equals("")) {
                pattern = customPattern;
                outputFormat = new SimpleDateFormat(pattern);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputFormat.format(new Date());
    }

    String formatMysqlTimestamp(String inputMysqlDate) {
        DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = inputFormat.parse(inputMysqlDate);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(new Date());
    }

    /*public void vibrateDevice() {
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }
    }*/

    /*public void playSound() {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.chime);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @SuppressWarnings("unused")
    public String getKeyFromValue(JSONObject inputObject, String inputValue) {
        try {
            Iterator<?> objectKeys = inputObject.keys();
            while (objectKeys.hasNext()) {
                String objectKey = (String) objectKeys.next();
                String objectValue = inputObject.getString(objectKey);
                if (objectValue.equals(inputValue)) {
                    return objectKey;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    String getKeyByIndex(JSONObject inputObject, int inputIndex) {
        try {
            Iterator<?> objectKeys = inputObject.keys();
            int currentIndex = 0;
            while (objectKeys.hasNext()) {
                String objectKey = (String) objectKeys.next();
                if (currentIndex == inputIndex) {
                    return objectKey;
                }
                currentIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unused")
    public String getValueByIndex(JSONObject inputObject, int inputIndex) {
        try {
            Iterator<?> objectKeys = inputObject.keys();
            int currentIndex = 0;
            while (objectKeys.hasNext()) {
                String objectKey = (String) objectKeys.next();
                String objectValue = inputObject.getString(objectKey);
                if (currentIndex == inputIndex) {
                    return objectValue;
                }
                currentIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    String getJsonObjectValueFromArrayIndexAndKey(JSONArray inputObject, int inputIndex, String inputKey) {
        String output = "";
        try {
            for (int i = 0; i < inputObject.length(); i++) {
                if (i == inputIndex) {
                    JSONObject currentObject = new JSONObject(inputObject.get(i).toString());
                    output = currentObject.getString(inputKey);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @SuppressWarnings("unused")
    String getStringJsonObjectValueFromArrayIndexAndKey(String inputObject, int inputIndex, String inputKey) {
        String output = "";
        try {
            JSONArray parsedArray = new JSONArray(inputObject);
            output = getJsonObjectValueFromArrayIndexAndKey(parsedArray, inputIndex, inputKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @SuppressWarnings("unused")
    public boolean checkIfValueExists(JSONObject inputObject, String inputValue) {
        try {
            Iterator<?> objectKeys = inputObject.keys();
            while (objectKeys.hasNext()) {
                String objectKey = (String) objectKeys.next();
                String objectValue = inputObject.getString(objectKey);
                if (objectValue.equals(inputValue)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void dismissKeyboard(Activity activity) {
        try {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void executeNotificationAction() {
        if (sqliteCore.getSetting("NOTIFICATION_ALERT_SETTING").equalsIgnoreCase("0")) {
            //playSound();
        } else {
            //vibrateDevice();
        }
    }

    @SuppressWarnings("unused")
    private void generateNotification(JSONObject inputJsonObject) {
        if (sqliteCore.getSetting("NOTIFICATION_SETTING").equalsIgnoreCase("ON")) {
            executeNotificationAction();
        }

        try {
            String inputTitle = inputJsonObject.getString("TITLE");
            String inputMessage = inputJsonObject.getString("MESSAGE");
            NotificationManager notificationManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                String id = "id_product";
                // The user-visible name of the channel.
                CharSequence name = "Product";
                // The user-visible description of the channel.
                String description = "Quote of the day";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                // Configure the notification channel.
                mChannel.setDescription(description);
                mChannel.enableLights(true);
                // Sets the notification light color for notifications posted to this
                // channel, if the device supports this feature.
                mChannel.setLightColor(Color.RED);
                notificationManager.createNotificationChannel(mChannel);

                Intent intent1 = new Intent(context.getApplicationContext(), MainActivityJava.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 123, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext(),"id_product")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setChannelId(id)
                        .setContentTitle(inputTitle)
                        .setAutoCancel(true).setContentIntent(pendingIntent)
                        .setNumber(1)
                        .setColor(255)
                        .setContentText(inputMessage)
                        .setWhen(System.currentTimeMillis());
                notificationManager.notify(1, notificationBuilder.build());
            } else {
                notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                // Create an Intent for the activity you want to start
                Intent notificationIntent = new Intent(context, MainActivityJava.class);
                notificationIntent.putExtra("notification_title", inputTitle);
                notificationIntent.putExtra("notification_message", inputMessage);
                // Create the TaskStackBuilder and add the intent, which inflates the back stack
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(notificationIntent);
                // Get the PendingIntent containing the entire back stack
                PendingIntent pIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification;
                notification = new Notification.Builder(context)
                        .setContentTitle(inputTitle)
                        .setContentText(inputMessage)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(0, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String isNetworkAvailable(Context context) {
        String output = "No Connection Available";
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            assert wifiInfo != null;
            if (wifiInfo.isConnected()) {
                output = "Wi-Fi";
            }

            assert mobileInfo != null;
            if (mobileInfo.isConnected()) {
                if (!output.equals("Wi-Fi")) {
                    output = "Mobile";
                } else {
                    output = "Wi-Fi and Mobile";
                }
            }
        } catch (Exception e) {
            output = "No Connection Available";
            e.printStackTrace();
        }
        return output;
    }

    @SuppressWarnings("unused")
    private JSONObject getDeviceInfo() {
        JSONObject output = new JSONObject();
        try {
            output.put("manufacturer", Build.MANUFACTURER);
            output.put("model", Build.MODEL);
            output.put("release", Build.VERSION.RELEASE);
            output.put("sdk", Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName());
            output.put("base_os", Build.VERSION.BASE_OS);
            output.put("codename", Build.VERSION.CODENAME);
            output.put("sdk_int", Build.VERSION.SDK_INT);
            output.put("security_patch", Build.VERSION.SECURITY_PATCH);
            output.put("device", Build.DEVICE);
            output.put("id", Build.ID);
            output.put("user", Build.USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    @SuppressWarnings("unused")
    public JSONObject appendDeviceInfo(JSONObject inputJsonObject) {
        try {
            inputJsonObject.put("manufacturer", Base64.encodeToString(Build.MANUFACTURER.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("model", Base64.encodeToString(Build.MODEL.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("release", Base64.encodeToString(Build.VERSION.RELEASE.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("sdk", Base64.encodeToString(Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName().getBytes(), Base64.DEFAULT));
            inputJsonObject.put("base_os", Base64.encodeToString(Build.VERSION.BASE_OS.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("codename", Base64.encodeToString(Build.VERSION.CODENAME.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("sdk_int", Base64.encodeToString(Integer.toString(Build.VERSION.SDK_INT).getBytes(), Base64.DEFAULT));
            inputJsonObject.put("security_patch", Base64.encodeToString(Build.VERSION.SECURITY_PATCH.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("device", Base64.encodeToString(Build.DEVICE.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("id", Base64.encodeToString(Build.ID.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("user", Base64.encodeToString(Build.USER.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("unique_id", Base64.encodeToString(UUID.randomUUID().toString().getBytes(), Base64.DEFAULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inputJsonObject;
    }

    @SuppressWarnings("unused")
    public String getWebServiceUrl() {
        return "https://haroldopaulino.com/web_services/papi_quotes/gateway.php";
    }

    @SuppressWarnings("unused")
    public boolean requestToReadContacts(Activity inputActivity) {
        if (context.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (inputActivity.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            inputActivity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            inputActivity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @SuppressWarnings("unused")
    public void sendSmsMessage(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        if (message.length() > 160) {//THIS IS THE LIMIT FOR ANY SINGLE TEXT MESSAGE SIZE, OTHERWISE IT HAS TO BE A MULTIPART MESSAGE
            ArrayList<String> parts =sms.divideMessage(message);
            int numParts = parts.size();
            for (String str : parts)
                Log.e("str", str);

            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();
            Intent sentIntent = new Intent();
            Intent deliveryIntent = new Intent();

            for (int i = 0; i < numParts; i++) {
                sentIntents.add(PendingIntent.getBroadcast(context, 0, sentIntent, 0));
                deliveryIntents.add(PendingIntent.getBroadcast(context, 0, deliveryIntent, 0));
            }

            sms.sendMultipartTextMessage(phoneNumber,null, parts, sentIntents, deliveryIntents);
        } else {
            PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, MainActivityJava.class), 0);
            sms.sendTextMessage(phoneNumber, null, message, pi, null);
        }
    }

    @SuppressWarnings("unused")
    public String getLanguageText(String inputElement) {
        return languages.getText(sqliteCore.getSetting("language"), inputElement);
    }

    @SuppressWarnings("unused")
    public String getFileContentFromAssets(String name) {
        try {
            InputStream inputStream = context.getAssets().open(name);
            int streamSize = inputStream.available();
            byte[] buffer = new byte[streamSize];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public String readFile(Context context, String filename) {
        String output = "";
        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                output = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            //Log.e("FileNotFoundException", "File not found: " + e.toString());
        } catch (IOException e) {
            //Log.e("FileNotFoundException", "Can not read file: " + e.toString());
        }

        return output;
    }

    @SuppressWarnings("unused")
    public void writeToFile(Context context, String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            //Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @SuppressWarnings("unused")
    public JSONArray readCsvFile(Uri inputUri, boolean skipFirstLine) {
        JSONArray output = new JSONArray();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(inputUri);
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int charRead = 0;
                while ((charRead = reader.read()) != -1) {
                    textBuilder.append((char) charRead);
                }
            }

            CSVReader reader = new CSVReader(new StringReader(textBuilder.toString()));
            //THIS BLOCK HAPPENS WHEN THE CSV FILE HAS A LINE BREAK TO SEPARATE EACH CSV LINE RECORD
            String[] nextLine;
            long lineNumber = 0;
            boolean skipLine = false;
            while ((nextLine = reader.readNext()) != null) {
                lineNumber++;
                if (lineNumber == 1 && skipFirstLine) {
                    skipLine = true;
                }

                if (!skipLine) {
                    output.put((new JSONObject())
                            .put("problem", nextLine[0])
                            .put("solution", nextLine[1]));
                } else {
                    skipLine = false;
                }
            }
            /*
            //THIS HAPPENS WHEN THE CSV FILE IS ONE CONTINUOS STRING, WITH NO LINE BREAKS
            String[] lines = reader.readNext();
            long lineNumber = 0;
            String problem = "";
            for (String aValue : lines) {
                lineNumber++;
                if (skipFirstLine && lineNumber == 1 || lineNumber == 2) {

                } else {
                    if (lineNumber % 2 == 0) {
                        output.put((new JSONObject())
                                .put("problem", problem)
                                .put("solution", Base64.encodeToString(aValue.getBytes("UTF-8"), Base64.DEFAULT)));
                    } else {
                        problem = Base64.encodeToString(aValue.getBytes("UTF-8"), Base64.DEFAULT);
                    }
                }
            }
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @SuppressWarnings("unused")
    public String getFilePathFromUri(Uri inputUri) {
        String output = null;
        output = getRealPathFromURI_API19(context, inputUri);
        return output;
    }

    private String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        assert cursor != null;
        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    @SuppressWarnings("unused")
    private String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    @SuppressWarnings("unused")
    private String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void writeSolutionsToFile(String contents) {
        File directory = new File(String.valueOf(context.getFilesDir()));
        File solutionsFile = new File(directory, "solutions.dat");

        if(!solutionsFile.exists()){
            try {
                if (solutionsFile.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try  {
            FileOutputStream fileOutputStream = new FileOutputStream(solutionsFile);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutputStream);
            outputWriter.write(contents);
            outputWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Solution> readSolutionsFromFile() {
        File directory = new File(String.valueOf(context.getFilesDir()));
        File solutionsFile = new File(directory, "solutions.dat");
        ArrayList<Solution> output = new ArrayList<>();

        if(solutionsFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(solutionsFile.getAbsoluteFile()))) {

                String line;
                JSONObject tmpObject;
                while ((line = br.readLine()) != null) {
                    try {
                        tmpObject = new JSONObject(new String(Base64.decode(line, Base64.DEFAULT)));
                        Solution tmpSolution = new Solution();
                        tmpSolution.category_id          = tmpObject.getInt("category_id");
                        tmpSolution.category_description = tmpObject.getString("category_description");
                        tmpSolution.problem              = tmpObject.getString("problem");
                        tmpSolution.solution             = tmpObject.getString("solution");
                        tmpSolution.solution_id          = tmpObject.getInt("solution_id");
                        tmpSolution.approved             = tmpObject.getString("approved");

                        output.add(tmpSolution);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    @SuppressWarnings("unused")
    public void appendSolutionToFile(Solution solution) {
        File directory = new File(String.valueOf(context.getFilesDir()));
        File solutionsFile = new File(directory, "solutions.dat");

        if(solutionsFile.exists()) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(solutionsFile.getAbsolutePath(), true));
                out.write("\n" +
                        (new JSONObject())
                            .put("category_id", solution.category_id)
                            .put("category_description", solution.category_description)
                            .put("problem", solution.problem)
                            .put("solution", solution.solution)
                            .put("solution_id", solution.solution_id)
                            .put("approved", solution.approved)
                            .toString()
                );
                out.close();
            } catch (Exception e) {
                System.out.println("exception occoured" + e);
            }
        }
    }

    @SuppressWarnings("unused")
    public ArrayList<Solution> readSolutionsFromJsonFile() {
        File directory = new File(String.valueOf(context.getFilesDir()));
        File solutionsFile = new File(directory, "solutions.dat");
        ArrayList<Solution> output = new ArrayList<>();

        if(solutionsFile.exists()){
            try {
                InputStream inputStream = Files.newInputStream(solutionsFile.toPath());
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
                reader.beginArray();
                while (reader.hasNext()) {
                    output.add(new Gson().fromJson(String.valueOf(reader), Solution.class));
                }
                reader.endArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    @SuppressWarnings("unused")
    public void writeEncryptedContentsToFile(String contents) {
        File directory = new File(String.valueOf(context.getFilesDir()));
        File solutionsFile = new File(directory, "encrypted_content.dat");

        if(!solutionsFile.exists()){
            try {
                solutionsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try  {
            FileOutputStream fileOutputStream = new FileOutputStream(solutionsFile);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutputStream);
            outputWriter.write(AESUtils.encrypt(contents));
            outputWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public String readEncryptedContentsFromFile() {
        File directory = new File(String.valueOf(context.getFilesDir()));
        File solutionswFile = new File(directory, "encrypted_content.dat");
        String output = "";

        if(solutionswFile.exists()){
            try {
                int length = (int) solutionswFile.length();

                byte[] bytes = new byte[length];

                try (FileInputStream in = new FileInputStream(solutionswFile)) {
                    in.read(bytes);
                }

                output = AESUtils.decrypt((new String(bytes)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }
}
