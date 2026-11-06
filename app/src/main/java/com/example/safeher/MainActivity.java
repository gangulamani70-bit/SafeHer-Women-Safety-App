
package com.example.safeher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get Started button
        Button btnGetStarted = findViewById(R.id.btnGetStarted);

        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    DashboardActivity.class
            );
            startActivity(intent);
        });

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Connect to Firebase
        testFirebaseConnection();
    }

    private void testFirebaseConnection() {

        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseDatabase database =
                                FirebaseDatabase.getInstance(
                                        "https://safeher-e9abc-default-rtdb.asia-southeast1.firebasedatabase.app"
                                );

                        database.getReference("connectionTest")
                                .setValue("SafeHer Firebase Connected")
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Firebase Connected Successfully!",
                                            Toast.LENGTH_LONG
                                    ).show();

                                })
                                .addOnFailureListener(error -> {

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Database Error: "
                                                    + error.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                });

                    } else {

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error";

                        Toast.makeText(
                                MainActivity.this,
                                "Firebase Authentication Failed: " + error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
