package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Objects;

abstract class LookupSolutionDetailAlert {
    private final Activity activity;
    private final View view;
    private final AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private final JSONObject data;

    LookupSolutionDetailAlert(
            Activity activityInput,
            AlertDialog.Builder alertInput,
            View viewInput,
            JSONObject dataInput) {
        activity = activityInput;
        alert = alertInput;
        view = viewInput;
        data = dataInput;
        buildLayout();
    }

    private void buildLayout() {
        TextView alertTitle = view.findViewById(R.id.title);
        alertTitle.setText("Solution");

        view.findViewById(R.id.titleMessage).setVisibility(View.GONE);

        view.findViewById(R.id.backImageContainer).setOnClickListener(arg0 -> OptionDialog.dismiss());

        view.findViewById(R.id.backImage).setOnClickListener(arg0 -> OptionDialog.dismiss());

        TextView category = view.findViewById(R.id.category);
        TextView problem = view.findViewById(R.id.problem);
        TextView solution = view.findViewById(R.id.solution);

        try {
            category.setText(data.getString("category_description"));
            problem.setText(data.getString("problem"));
            solution.setText(data.getString("solution"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        alert.setView(view);
        OptionDialog = alert.create();
        OptionDialog.setOnDismissListener(dialogInterface -> {

        });
        //OptionDialog.setCancelable(false);//ALLOW THE DEVICE'S BACK BUTTON TO CLOSE THE ALERT OR NOT

        Objects.requireNonNull(OptionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ffffff")));
        OptionDialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(OptionDialog.getWindow().getAttributes());

        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        int dialogWindowHeight = (int) (displayHeight * 0.9f);

        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        OptionDialog.getWindow().setAttributes(layoutParams);
    }

    AlertDialog.Builder getAlert() {
        return alert;
    }

    public void callback(JSONObject inputResponse) { }
}
