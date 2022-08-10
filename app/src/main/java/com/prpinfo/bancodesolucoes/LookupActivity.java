package com.prpinfo.bancodesolucoes;

import android.app.Activity;
import android.os.Bundle;

public class LookupActivity extends Activity {

    class LocalLookupAbstractActivity extends LookupAbstractActivity {
        LocalLookupAbstractActivity(Activity inputActivity, String inputMode) {
            super(inputActivity, inputMode);
        }

        public void callback(Solution solution) {
            populateList();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution_lookup);

        LocalLookupAbstractActivity localLookupAbstractActivity = new LocalLookupAbstractActivity(this, "lookup");
    }
}