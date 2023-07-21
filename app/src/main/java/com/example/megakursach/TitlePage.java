package com.example.megakursach;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TitlePage extends AppCompatActivity {

    private TextView tvProfileName;
    private ImageButton btnAppointments;
    private ImageButton btnMoodTracking;
    private ImageButton btnGuidedMeditation;
    private ImageButton btnSupport;
    private ImageButton btnWellnessContent;
    private ImageButton btnGoalSetting;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_page);

        tvProfileName = findViewById(R.id.profileName);
        btnAppointments = findViewById(R.id.btnAppointments);
        btnMoodTracking = findViewById(R.id.btnMoodTracking);
        btnGuidedMeditation = findViewById(R.id.btnGuidedMeditation);
        btnSupport = findViewById(R.id.btnSupportCommunity);
        btnWellnessContent = findViewById(R.id.btnWellnessContent);
        btnGoalSetting = findViewById(R.id.btnGoalSetting);

        // Get the logged-in user's email from the intent
        loggedInUserEmail = getIntent().getStringExtra("email");

        // Fetch and display the user's name from the database
        String name = fetchUserNameFromDatabase(loggedInUserEmail);
        tvProfileName.setText(name);

        btnMoodTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the logged-in user's email to the MoodTrackerActivity
                Intent intent = new Intent(TitlePage.this, MoodTrackerActivity.class);
                intent.putExtra("email", loggedInUserEmail);
                startActivity(intent);
            }
        });

        btnAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the logged-in user's email to the MoodTrackerActivity
                Intent intent = new Intent(TitlePage.this, AppointmentsActivity.class);
                startActivity(intent);
            }
        });

        // Set click listeners for other buttons (Appointments, Guided Meditation, etc.)
        // ...
    }

    private String fetchUserNameFromDatabase(String email) {
        // Get a reference to the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Define the table and columns to query
        String tableName = "users";
        String[] projection = {"full_name"};

        // Set up the selection criteria
        String selection = "email = ?";
        String[] selectionArgs = {email};

        // Query the database
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);

        // Check if a matching user is found and retrieve the name
        String name = "";
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        return name;
    }
}
