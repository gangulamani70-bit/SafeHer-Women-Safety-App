
package com.example.safeher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactsActivity extends Activity {

    private LinearLayout contactsContainer;
    private ArrayList<String[]> contacts = new ArrayList<>();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsContainer = findViewById(R.id.contactsContainer);

        Button btnAddContact = findViewById(R.id.btnAddContact);
        Button btnBack = findViewById(R.id.btnBack);

        preferences = getSharedPreferences(
                "SafeHerContacts",
                MODE_PRIVATE
        );

        loadContacts();
        displayContacts();

        btnAddContact.setOnClickListener(v -> showContactDialog(null));

        btnBack.setOnClickListener(v -> finish());
    }

    private void showContactDialog(String[] contactToEdit) {

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(40, 10, 40, 10);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Contact name");
        form.addView(nameInput);

        EditText phoneInput = new EditText(this);
        phoneInput.setHint("Phone number");
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
        form.addView(phoneInput);

        if (contactToEdit != null) {
            nameInput.setText(contactToEdit[0]);
            phoneInput.setText(contactToEdit[1]);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(contactToEdit == null
                        ? "Add Trusted Contact"
                        : "Edit Trusted Contact")
                .setView(form)
                .setPositiveButton("Save", null) // We override this below to prevent auto-dismiss
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = nameInput.getText().toString().trim();
                String phone = phoneInput.getText().toString().trim();

                if (name.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(
                            this,
                            "Enter name and phone number",
                            Toast.LENGTH_SHORT
                    ).show();
                    return; // Dialog remains open
                }

                if (!phone.matches("[+0-9() -]{7,20}")) {
                    Toast.makeText(
                            this,
                            "Enter a valid phone number",
                            Toast.LENGTH_SHORT
                    ).show();
                    return; // Dialog remains open
                }

                String[] newContact = {name, phone};

                if (contactToEdit == null) {
                    contacts.add(newContact);
                } else {
                    int currentIndex = contacts.indexOf(contactToEdit);
                    if (currentIndex != -1) {
                        contacts.set(currentIndex, newContact);
                    } else {
                        Toast.makeText(
                                this,
                                "Contact no longer exists",
                                Toast.LENGTH_SHORT
                        ).show();
                        dialog.dismiss();
                        return;
                    }
                }

                saveContacts();
                displayContacts();

                Toast.makeText(
                        this,
                        "Contact saved successfully",
                        Toast.LENGTH_SHORT
                ).show();
                
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void displayContacts() {

        contactsContainer.removeAllViews();

        if (contacts.isEmpty()) {

            TextView empty = new TextView(this);
            empty.setText("No trusted contacts added yet.");
            empty.setTextSize(16);
            empty.setPadding(10, 30, 10, 30);

            contactsContainer.addView(empty);
            return;
        }

        for (int i = 0; i < contacts.size(); i++) {

            final String[] currentContact = contacts.get(i);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(20, 15, 20, 15);
            card.setBackgroundColor(0xFFFFEEEE);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(0, 0, 0, 20);
            card.setLayoutParams(cardParams);

            TextView name = new TextView(this);
            name.setText("Name: " + currentContact[0]);
            name.setTextSize(18);
            name.setTextColor(0xFF222222);
            name.setPadding(0, 0, 0, 5);

            card.addView(name);

            TextView phone = new TextView(this);
            phone.setText("Phone: " + currentContact[1]);
            phone.setTextSize(15);
            phone.setTextColor(0xFF444444);

            card.addView(phone);

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);

            Button edit = new Button(this);
            edit.setText("Edit");

            Button delete = new Button(this);
            delete.setText("Delete");

            actions.addView(edit);
            actions.addView(delete);

            card.addView(actions);
            contactsContainer.addView(card);

            edit.setOnClickListener(v -> {
                if (contacts.contains(currentContact)) {
                    showContactDialog(currentContact);
                }
            });

            delete.setOnClickListener(v -> {
                if (!contacts.contains(currentContact)) {
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle("Delete Contact")
                        .setMessage("Are you sure you want to delete " + currentContact[0] + "?")
                        .setPositiveButton("Delete", (d, w) -> {
                            int index = contacts.indexOf(currentContact);
                            if (index != -1) {
                                contacts.remove(index);
                                saveContacts();
                                displayContacts();
                                Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }
    }

    private void saveContacts() {

        JSONArray array = new JSONArray();

        try {
            for (String[] contact : contacts) {

                JSONObject object = new JSONObject();

                object.put("name", contact[0]);
                object.put("phone", contact[1]);

                array.put(object);
            }

            preferences.edit()
                    .putString("contacts", array.toString())
                    .apply();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Could not save contacts",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void loadContacts() {

        contacts.clear();

        String saved = preferences.getString("contacts", "[]");

        try {

            JSONArray array = new JSONArray(saved);

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                contacts.add(new String[]{
                        object.getString("name"),
                        object.getString("phone")
                });
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Could not load contacts",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
