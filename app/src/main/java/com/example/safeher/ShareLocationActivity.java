
package com.example.safeher;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShareLocationActivity extends Activity {

    private static final int LOCATION_REQUEST = 200;

    private TextView txtLocation;
    private Button btnGetLocation;
    private Button btnShareLocation;

    private LocationManager locationManager;
    private String locationLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        txtLocation = findViewById(R.id.txtLocation);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnShareLocation = findViewById(R.id.btnShareLocation);
        Button btnBack = findViewById(R.id.btnBack);

        locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);

        btnBack.setOnClickListener(v -> finish());

        btnGetLocation.setOnClickListener(v -> getLocation());

        btnShareLocation.setOnClickListener(v -> shareLocation());

        btnShareLocation.setEnabled(false);
    }

    private void getLocation() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_REQUEST
            );
            return;
        }

        try {
            Location location = null;

            if (locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER)) {

                location = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);
            }

            if (location == null &&
                    locationManager.isProviderEnabled(
                            LocationManager.NETWORK_PROVIDER)) {

                location = locationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER);
            }

            if (location == null) {
                locationLink = "";
                btnShareLocation.setEnabled(false);

                txtLocation.setText(
                        "Location unavailable.\n"
                                + "Enable GPS and try again."
                );

                Toast.makeText(this,
                        "No last-known location found",
                        Toast.LENGTH_LONG).show();
                return;
            }

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            locationLink = "https://www.google.com/maps/search/?api=1&query="
                    + latitude + "," + longitude;

            txtLocation.setText(
                    "Location found successfully!\n\n"
                            + "Latitude: " + latitude + "\n"
                            + "Longitude: " + longitude + "\n\n"
                            + "Google Maps link:\n" + locationLink
            );

            btnShareLocation.setEnabled(true);

        } catch (SecurityException e) {
            Toast.makeText(this,
                    "Location permission is required",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void shareLocation() {

        if (locationLink.isEmpty()) {
            Toast.makeText(this,
                    "Please get your location first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String message = "My current available location:\n"
                + locationLink;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(
                shareIntent,
                "Share my location"
        ));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLocation();

            } else {
                txtLocation.setText(
                        "Location permission denied. "
                                + "Allow location access to use this feature."
                );
            }
        }
    }
}
