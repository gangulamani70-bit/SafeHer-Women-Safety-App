
package com.example.safeher;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SosActivity extends Activity {

    private static final int LOCATION_PERMISSION_REQUEST = 101;

    private TextView txtLocation;
    private Button btnGetLocation;
    private Button btnSendSOS;

    private String currentLocation = "Location unavailable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        // Connect XML views
        txtLocation = findViewById(R.id.txtLocation);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnSendSOS = findViewById(R.id.btnSendSOS);
        Button btnBack = findViewById(R.id.btnBack);

        // Display initial location message
        txtLocation.setText("Location not retrieved yet.");

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Get location button
        btnGetLocation.setOnClickListener(v -> getLocation());

        // Send SOS button
        btnSendSOS.setOnClickListener(v -> confirmSOS());
    }

    // ---------------- GET LOCATION ----------------

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
                    LOCATION_PERMISSION_REQUEST
            );

            return;
        }

        try {
            LocationManager locationManager =
                    (LocationManager) getSystemService(LOCATION_SERVICE);

            Location location = null;

            // Try GPS location first
            if (locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER)) {

                location = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);
            }

            // Try network location if GPS location is unavailable
            if (location == null &&
                    locationManager.isProviderEnabled(
                            LocationManager.NETWORK_PROVIDER)) {

                location = locationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                currentLocation =
                        "Latitude: " + latitude +
                                "\nLongitude: " + longitude;

                txtLocation.setText(currentLocation);

                Toast.makeText(
                        this,
                        "Location retrieved successfully",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                currentLocation = "Location unavailable";

                txtLocation.setText(
                        "Location unavailable. Please enable GPS and try again."
                );

                Toast.makeText(
                        this,
                        "No last-known location found. Try again outdoors.",
                        Toast.LENGTH_LONG
                ).show();
            }

        } catch (SecurityException e) {

            Toast.makeText(
                    this,
                    "Location permission is required.",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to retrieve location.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ---------------- PERMISSION RESULT ----------------

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {

            if (grantResults.length > 0 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED
                            || (grantResults.length > 1 &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED))) {

                getLocation();

            } else {

                Toast.makeText(
                        this,
                        "Location permission denied.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    // ---------------- CONFIRM SOS ----------------

    private void confirmSOS() {

        ArrayList<String> contacts = getTrustedContacts();

        if (contacts.isEmpty()) {

            new AlertDialog.Builder(this)
                    .setTitle("No Trusted Contacts")
                    .setMessage(
                            "Please add at least one trusted contact before sending an SOS alert."
                    )
                    .setPositiveButton("OK", null)
                    .show();

            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm SOS Alert")
                .setMessage(
                        "Do you want to prepare an emergency SMS for your trusted contacts?"
                )
                .setPositiveButton("Continue", (dialog, which) -> {
                    openSmsComposer(contacts);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- GET TRUSTED CONTACTS ----------------

    private ArrayList<String> getTrustedContacts() {

        ArrayList<String> phoneNumbers = new ArrayList<>();

        SharedPreferences preferences =
                getSharedPreferences("SafeHerContacts", MODE_PRIVATE);

        String savedContacts =
                preferences.getString("contacts", "[]");

        try {

            JSONArray contacts = new JSONArray(savedContacts);

            for (int i = 0; i < contacts.length(); i++) {

                JSONObject contact = contacts.getJSONObject(i);

                String phone = contact.optString("phone", "").trim();

                if (!phone.isEmpty()) {
                    phoneNumbers.add(phone);
                }
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to read trusted contacts.",
                    Toast.LENGTH_SHORT
            ).show();
        }

        return phoneNumbers;
    }

    // ---------------- OPEN SMS COMPOSER ----------------

    private void openSmsComposer(ArrayList<String> contacts) {

        String message =
                "EMERGENCY SOS ALERT!\n" +
                        "I may need help. Please try to contact me immediately.\n\n";

        if (!currentLocation.equals("Location unavailable")) {

            String[] coordinates = currentLocation.split("\n");

            if (coordinates.length == 2) {

                String latitude = coordinates[0].replace("Latitude: ", "");
                String longitude = coordinates[1].replace("Longitude: ", "");

                String mapLink =
                        "https://www.google.com/maps/search/?api=1&query="
                                + latitude + "," + longitude;

                message += "My location:\n" + mapLink;

            } else {

                message += currentLocation;
            }

        } else {

            message += "My location is currently unavailable.";
        }

        try {

            // Prepare recipients
            String recipients = android.text.TextUtils.join(";", contacts);

            Uri smsUri = Uri.parse("smsto:" + recipients);

            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, smsUri);

            smsIntent.putExtra("sms_body", message);

            startActivity(smsIntent);

            // Save the confirmed SOS action locally
            saveSOSHistory();

            Toast.makeText(
                    this,
                    "SMS composer opened. Please press Send to send the alert.",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to open an SMS application.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // ---------------- SAVE SOS HISTORY ----------------

    private void saveSOSHistory() {

        try {

            SharedPreferences preferences =
                    getSharedPreferences("SafeHerHistory", MODE_PRIVATE);

            String savedHistory =
                    preferences.getString("history", "[]");

            JSONArray history = new JSONArray(savedHistory);

            String date = new SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
            ).format(new Date());

            String time = new SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
            ).format(new Date());

            JSONObject record = new JSONObject();

            record.put("date", date);
            record.put("time", time);
            record.put("location", currentLocation);

            history.put(record);

            preferences.edit()
                    .putString("history", history.toString())
                    .apply();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to save SOS history.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
