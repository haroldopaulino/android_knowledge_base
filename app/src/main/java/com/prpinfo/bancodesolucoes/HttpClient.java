package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public abstract class HttpClient {
	public Activity activity;
	public String webServiceURL, action;
	public JSONObject data;
	private int resultHTTPClient = 0;
	public String httpResponse = "";
	public String connectionType = "No Connection Available";
	private final SqliteCore sqliteCore;
	private final Utilities utilities;

	public HttpClient(Activity inputActivity) {
		activity = inputActivity;
		webServiceURL = "https://haroldopaulino.com/web/knowledge_base/gateway.php";
		sqliteCore = new SqliteCore(activity);
		utilities = new Utilities(activity);
	}

	public void callback(JSONObject callbackJSONObject) { }

	public void runHttpClient(JSONObject inputData) {
		data = inputData;
		try {
			data
				.put("company_id", sqliteCore.getSetting("login_company_id"))
				.put("company", sqliteCore.getSetting("login_company"))
				.put("email", sqliteCore.getSetting("login_email"))
				.put("password", sqliteCore.getSetting("login_password"))
				.put("client_datetime", utilities.generateTimestamp(null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		encodeJSONObjectValues();
		connectionType = utilities.isNetworkAvailable(activity);
		ServiceTask task = new ServiceTask();
		task.execute(webServiceURL);
	}

	public void encodeJSONObjectValues() {
		try {
			Iterator<String> keys = data.keys();
			while (keys.hasNext()) {
				String
					aKey = (String) keys.next(),
					aValue = data.get(aKey).toString();
				if (aKey.equalsIgnoreCase("action")) {
					action = aValue;
				}
				data.put(aKey, Base64.encodeToString(aValue.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ServiceTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			for (String url : urls) {
				URL object;
				try {
					resultHTTPClient = 0;
					String params = "params=" + URLEncoder.encode(Base64.encodeToString(data.toString().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT), "UTF-8");
					object = new URL(url);
					HttpURLConnection con = (HttpURLConnection) object.openConnection();
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setUseCaches(false);
					con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					con.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
					con.setRequestProperty("Content-Language", "en-US");
					con.setRequestMethod("POST");

					DataOutputStream wr = new DataOutputStream(con.getOutputStream ());
					wr.writeBytes(params);
					wr.flush();
					wr.close();
					Log.e("HTTP URL", url.toString());
					Log.e("HTTP SENDING", params);

					int HttpResult = con.getResponseCode();
					if(HttpResult == HttpURLConnection.HTTP_OK){
						BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
						String line;
						while ((line = br.readLine()) != null) {
							httpResponse += line;
						}
						resultHTTPClient = 1;
						br.close();

					}else{
						resultHTTPClient = 0;
						httpResponse = "Error connecting to server (Negative Response)";
					}
				} catch (MalformedURLException e) {
					resultHTTPClient = 0;
					httpResponse = "Error connecting to server (MalformedURLException)";
				} catch (ProtocolException e) {
					resultHTTPClient = 0;
					httpResponse = "Error connecting to server (ProtocolException)";
				} catch (UnsupportedEncodingException e) {
					resultHTTPClient = 0;
					httpResponse = "Error connecting to server (UnsupportedEncodingException)";
				} catch (IOException e) {
					resultHTTPClient = 0;
					httpResponse = "Error connecting to server (IOException)";
				}
			}
			return httpResponse;
		}

		@Override
		protected void onPreExecute() { }

		@Override
		protected void onPostExecute(String result) {
			parseHTTPResponseDownloadInfo(result);
		}
	}

	public void parseHTTPResponseDownloadInfo(String httpInput) {
		if (resultHTTPClient == 1) {
			try {
				if (!httpInput.trim().equals("")) {
					if (action.equalsIgnoreCase("GET_SOLUTIONS_LOOKUP_LIST") ||
						action.equalsIgnoreCase("CREATE_SOLUTION") ||
						action.equalsIgnoreCase("UPDATE_SOLUTION") ||
						action.equalsIgnoreCase("DELETE_SOLUTION")) {
						utilities.writeSolutionsToFile(httpInput.replaceAll(",", "\n"));

						if (action.equalsIgnoreCase("GET_SOLUTIONS_LOOKUP_LIST")) {
							callback(new JSONObject().put("result", "OK"));
						}

						if (action.equalsIgnoreCase("CREATE_SOLUTION")) {
							callback(new JSONObject().put("result", "CREATED"));
						}

						if (action.equalsIgnoreCase("UPDATE_SOLUTION")) {
							callback(new JSONObject().put("result", "UPDATED"));
						}

						if (action.equalsIgnoreCase("DELETE_SOLUTION")) {
							callback(new JSONObject().put("result", "DELETED"));
						}
					} else {
						JSONObject receivedData = new JSONObject(httpInput);

						String value = "";
						Iterator<String> keys = receivedData.keys();
						while (keys.hasNext()) {
							String key = keys.next();
							if (key.equalsIgnoreCase("user_data") ||
								key.equalsIgnoreCase("languages") ||
								key.equalsIgnoreCase("access_types")) {
								try {
									JSONObject arrayJsonObject = new JSONObject(receivedData.getString(key));
									Iterator<?> arrayObjectKeys = arrayJsonObject.keys();
									while (arrayObjectKeys.hasNext()) {
										String categoryObjectKey = (String) arrayObjectKeys.next();
										String categoryObjectValue = arrayJsonObject.getString(categoryObjectKey);
										arrayJsonObject.put(categoryObjectKey, new String(Base64.decode(categoryObjectValue, Base64.DEFAULT)));
									}
									receivedData.put(key, arrayJsonObject);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (key.equalsIgnoreCase("categories_data") ||
									   key.equalsIgnoreCase("users_data")) {
								JSONArray categoriesArray = new JSONArray(receivedData.getString(key));
								for (int i = 0; i < categoriesArray.length(); i++) {
									JSONObject categoryObject = categoriesArray.getJSONObject(i);

									Iterator<?> categoryObjectKeys = categoryObject.keys();
									while (categoryObjectKeys.hasNext()) {
										String categoryObjectKey = (String) categoryObjectKeys.next();
										String categoryObjectValue = categoryObject.getString(categoryObjectKey);
										categoriesArray.getJSONObject(i).put(categoryObjectKey, new String(Base64.decode(categoryObjectValue, Base64.DEFAULT)));
									}
								}
								receivedData.put(key, categoriesArray);
								if (key.equalsIgnoreCase("categories_data")) {
									sqliteCore.createSetting("categoriesData", categoriesArray.toString());
								}
							} else {
								value = new String(Base64.decode(receivedData.getString(key), Base64.DEFAULT));
								receivedData.put(key, value);
							}
						}
						callback(receivedData);
					}
				} else {
					callback(null);
				}
			} catch(Exception e2) {
				callback(null);
			}
		} else {
			callback(null);
		}
	}
}