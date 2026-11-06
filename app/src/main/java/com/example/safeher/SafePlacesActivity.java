
package com.example.safeher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.Toast;

public class SafePlacesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_places);

        Button btnPolice = findViewById(R.id.btnPolice);
        Button btnHospital = findViewById(R.id.btnHospital);
        Button btnPharmacy = findViewById(R.id.btnPharmacy);
        Button btnBack = findViewById(R.id.btnBack);

        btnPolice.setOnClickListener(v ->
                searchNearby("police station"));

        btnHospital.setOnClickListener(v ->
                searchNearby("hospital"));

        btnPharmacy.setOnClickListener(v ->
                searchNearby("pharmacy"));

        btnBack.setOnClickListener(v -> finish());
    }

    private void searchNearby(String place) {
        Uri uri = Uri.parse(
                "https://www.google.com/maps/search/?api=1&query="
                        + Uri.encode(place + " near me")
        );

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Unable to open Maps",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
