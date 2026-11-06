
package com.example.safeher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class DashboardActivity extends Activity {

    Button btnSOS, btnContacts, btnLocation, btnSafePlaces, btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnSOS = findViewById(R.id.btnSOS);
        btnContacts = findViewById(R.id.btnContacts);
        btnLocation = findViewById(R.id.btnLocation);
        btnSafePlaces = findViewById(R.id.btnSafePlaces);
        btnHistory = findViewById(R.id.btnHistory);

        if (btnSOS == null || btnContacts == null ||
                btnLocation == null || btnSafePlaces == null ||
                btnHistory == null) {
            Toast.makeText(this,
                    "Dashboard buttons not found. Check XML IDs.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        btnSOS.setOnClickListener(v -> openScreen(SosActivity.class));

        btnContacts.setOnClickListener(v -> openScreen(ContactsActivity.class));

        btnLocation.setOnClickListener(v -> openScreen(ShareLocationActivity.class));

        btnSafePlaces.setOnClickListener(v -> openScreen(SafePlacesActivity.class));

        btnHistory.setOnClickListener(v -> openScreen(SosHistoryActivity.class));
    }

    private void openScreen(Class<?> activityClass) {
        Intent intent = new Intent(DashboardActivity.this, activityClass);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit SafeHer?")
                .setMessage("Do you want to exit the application?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}
