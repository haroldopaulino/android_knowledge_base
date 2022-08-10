package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

abstract class GenericAlert {
    private final Activity activity;
    private final SqliteCore sqliteCore;
    private final View view;
    private final AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private final String
            tag;
    private final String title;
    private final String message;
    private final String button1Text;
    private final String button2Text;

    GenericAlert(
            Activity activityInput,
            AlertDialog.Builder alertInput,
            View viewInput,
            String tagInput,
            String titleInput,
            String messageInput,
            String button1Input,
            String button2Input) {
        activity = activityInput;
        alert = alertInput;
        view = viewInput;
        tag = tagInput;
        title = titleInput;
        message = messageInput;
        button1Text = button1Input;
        button2Text = button2Input;
        sqliteCore = new SqliteCore(activity);
        buildLayout();
    }

    private void buildLayout() {
        TextView alertTitle = view.findViewById(R.id.title);
        alertTitle.setText(title);

        view.findViewById(R.id.titleMessage).setVisibility(View.GONE);
        WebView loadingWebView = view.findViewById(R.id.loadingWebView);

        if (tag.equalsIgnoreCase("loading")) {
            view.findViewById(R.id.backImageContainer).setVisibility(View.GONE);
            loadingWebView.setBackgroundColor(0x00000000);
            loadingWebView.loadUrl("file:///android_asset/loading.html");
        } else {
            loadingWebView.setVisibility(View.GONE);
            view.findViewById(R.id.backImageContainer).setOnClickListener(arg0 -> {
                OptionDialog.dismiss();
                try {
                    callback((new JSONObject())
                            .put("tag", tag)
                            .put("return", "BACK"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            view.findViewById(R.id.backImage).setOnClickListener(arg0 -> {
                OptionDialog.dismiss();
                try {
                    callback((new JSONObject())
                            .put("tag", tag)
                            .put("return", "BACK"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        Button button1View = view.findViewById(R.id.button1);
        if (button1Text != null &&
            !button1Text.trim().equals("")) {
            button1View.setText(button1Text);
            button1View.setOnClickListener(arg0 -> {
                OptionDialog.dismiss();
                try {
                    callback((new JSONObject())
                            .put("tag", tag)
                            .put("return", button1Text));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            button1View.setVisibility(View.GONE);
        }

        Button button2View = view.findViewById(R.id.button2);
        if (button2Text != null &&
            !button2Text.trim().equals("")) {
            button2View.setText(button2Text);
            button2View.setOnClickListener(arg0 -> {
                OptionDialog.dismiss();
                try {
                    callback((new JSONObject())
                            .put("tag", tag)
                            .put("return", button2Text));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            button2View.setVisibility(View.GONE);
        }

        TextView messageTextView = view.findViewById(R.id.message);
        messageTextView.setText(message);

        alert.setView(view);
        OptionDialog = alert.create();
        OptionDialog.setOnDismissListener(dialogInterface -> sqliteCore.createSetting("ERROR_DIALOG_CURRENTLY_SHOWING", "NO"));

        if (tag.equalsIgnoreCase("loading")) {
            OptionDialog.setCancelable(false);//ALLOW THE DEVICE'S BACK BUTTON TO CLOSE THE ALERT OR NOT
        }

        OptionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ffffff")));
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

    public void callback(JSONObject inputResponse) { }
}