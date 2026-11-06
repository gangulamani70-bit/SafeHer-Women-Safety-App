
package com.example.safeher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

public class SosHistoryActivity extends Activity {

    private LinearLayout historyContainer;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_history);

        historyContainer = findViewById(R.id.historyContainer);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnClearHistory = findViewById(R.id.btnClearHistory);

        preferences = getSharedPreferences("SafeHerHistory", MODE_PRIVATE);

        btnBack.setOnClickListener(v -> finish());

        btnClearHistory.setOnClickListener(v -> confirmClearHistory());

        displayHistory();
    }

    private void displayHistory() {
        historyContainer.removeAllViews();

        String savedData = preferences.getString("history", "[]");

        try {
            JSONArray history = new JSONArray(savedData);

            if (history.length() == 0) {
                addText("No SOS alerts recorded yet.");
                return;
            }

            for (int i = history.length() - 1; i >= 0; i--) {
                JSONObject item = history.getJSONObject(i);

                String date = item.optString("date", "Unknown date");
                String time = item.optString("time", "Unknown time");
                String location = item.optString("location", "Location unavailable");

                addText(
                        "SOS Alert\n\nDate: " + date +
                                "\nTime: " + time +
                                "\nLocation: " + location
                );
            }

        } catch (Exception e) {
            addText("Unable to load SOS history.");
        }
    }

    private void addText(String message) {
        TextView card = new TextView(this);
        card.setText(message);
        card.setTextSize(16);
        card.setTextColor(0xFF333333);
        card.setPadding(24, 24, 24, 24);
        card.setBackgroundColor(0xFFFFFFFF);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 12, 0, 12);
        card.setLayoutParams(params);

        historyContainer.addView(card);
    }

    private void confirmClearHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Clear SOS History")
                .setMessage("Are you sure you want to delete all saved SOS records?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    preferences.edit().putString("history", "[]").apply();
                    displayHistory();
                    Toast.makeText(this, "History cleared",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
