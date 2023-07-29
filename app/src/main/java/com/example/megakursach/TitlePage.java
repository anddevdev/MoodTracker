package com.example.megakursach;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TitlePage extends AppCompatActivity {

    private TextView tvProfileName;
    private ImageButton btnAppointments;
    private ImageButton btnMoodTracking;
    private ImageButton btnGuidedMeditation;
    private ImageButton btnSupport;
    private ImageButton btnWellnessContent;
    private ImageButton btnGoalSetting;
    private RecyclerView goalsRecyclerView;
    private TextView emptyView; // Added reference to the emptyView
    private String loggedInUserEmail;
    private long loggedInUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_page);

        loggedInUserEmail = getIntent().getStringExtra("email");
        loggedInUserId = getUserIdFromDatabase(loggedInUserEmail);
        Log.d("TitlePage", "Received user_id: " + loggedInUserId);

        tvProfileName = findViewById(R.id.profileName);
        btnAppointments = findViewById(R.id.btnAppointments);
        btnMoodTracking = findViewById(R.id.btnMoodTracking);
        btnGuidedMeditation = findViewById(R.id.btnGuidedMeditation);
        btnSupport = findViewById(R.id.btnSupportCommunity);
        btnWellnessContent = findViewById(R.id.btnWellnessContent);
        btnGoalSetting = findViewById(R.id.btnGoalSetting);
        goalsRecyclerView = findViewById(R.id.goalsRecyclerView);
        emptyView = findViewById(R.id.emptyView);

        // Get the logged-in user's email from the intent
        loggedInUserEmail = getIntent().getStringExtra("email");

        // Fetch and display the user's name from the database
        String name = fetchUserNameFromDatabase(loggedInUserEmail);
        tvProfileName.setText(name);

        List<Goal> goals = fetchUserGoalsFromDatabase(loggedInUserId);

        // Update the UI to display the user's goals
        if (goals.isEmpty()) {
            goalsRecyclerView.setVisibility(View.GONE); // Hide the RecyclerView
            emptyView.setVisibility(View.VISIBLE); // Show the emptyView
        } else {
            goalsRecyclerView.setVisibility(View.VISIBLE); // Show the RecyclerView
            emptyView.setVisibility(View.GONE); // Hide the emptyView
            GoalsAdapter goalsAdapter = new GoalsAdapter(this, goals);
            goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            goalsRecyclerView.setAdapter(goalsAdapter);
        }

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
                Intent intent = new Intent(TitlePage.this, AppointmentsActivity.class);
                intent.putExtra("email", loggedInUserEmail);
                startActivity(intent);
            }
        });

        btnGoalSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TitlePage.this, GoalSettingActivity.class);
                intent.putExtra("email", loggedInUserEmail);
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

    private List<Goal> fetchUserGoalsFromDatabase(long userId) {
        // Get a reference to the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Define the table and columns to query
        String tableName = "goals";
        String[] projection = {"goal_id", "title", "description", "target_value", "target_date"};

        // Set up the selection criteria
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        // Query the database
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);

        // Create a list to hold the user's goals
        List<Goal> goals = new ArrayList<>();

        // Set up the RecyclerView with the GoalsAdapter
        RecyclerView goalsRecyclerView = findViewById(R.id.goalsRecyclerView);
        GoalsAdapter goalsAdapter = new GoalsAdapter(this, goals);
        goalsRecyclerView.setAdapter(goalsAdapter);

        // Set a LinearLayoutManager to display the goals in a vertical list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        goalsRecyclerView.setLayoutManager(layoutManager);

        // Iterate through the cursor and add goals to the list
        while (cursor.moveToNext()) {
            int goalId = cursor.getInt(cursor.getColumnIndexOrThrow("goal_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            int targetValue = cursor.getInt(cursor.getColumnIndexOrThrow("target_value"));
            String targetDate = cursor.getString(cursor.getColumnIndexOrThrow("target_date"));

            // Create a Goal object and add it to the list
            Goal goal = new Goal(goalId, title, description, targetValue, targetDate);
            goals.add(goal);
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        return goals;
    }

    private long getUserIdFromDatabase(String email) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String tableName = "users";
        String[] projection = {"user_id"};

        String selection = "email = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);

        long userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
        }

        cursor.close();
        db.close();

        return userId;
    }

}
