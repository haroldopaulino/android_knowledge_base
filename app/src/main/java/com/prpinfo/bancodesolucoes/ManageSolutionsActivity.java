package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

public class ManageSolutionsActivity extends Activity {

    LocalLookupAbstractActivity localLookupAbstractActivity;

    class LocalLookupAbstractActivity extends LookupAbstractActivity {
        LocalLookupAbstractActivity(Activity inputActivity, String inputMode) {
            super(inputActivity, inputMode);
        }

        public void callback() {
            populateList();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution_lookup);

        localLookupAbstractActivity = new LocalLookupAbstractActivity(this, "manage_solutions");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    localLookupAbstractActivity.updateList();
                } catch (Exception e) {
                    e.printStackTrace();
                };
            }
        }
    }
}