
package com.example.megakursach;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class TitlePage extends AppCompatActivity {

    private TextView tvProfileName;
    private ImageButton btnAppointments;
    private ImageButton btnMoodTracking;
    private ImageButton btnGoalSetting;
    private RecyclerView goalsRecyclerView;
    private TextView emptyView; // Added reference to the emptyView
    private String loggedInUserEmail;
    private long loggedInUserId;
    private DatabaseHelper databaseHelper; // Added instance of DatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_page);

        loggedInUserEmail = getIntent().getStringExtra("email");
        loggedInUserId = getUserIdFromDatabase(loggedInUserEmail);
        Log.d("TitlePage", "Received user_id: " + loggedInUserId);

        // Initialize the databaseHelper instance
        databaseHelper = new DatabaseHelper(this);

        tvProfileName = findViewById(R.id.profileName);
        btnAppointments = findViewById(R.id.btnAppointments);
        btnMoodTracking = findViewById(R.id.btnMoodTracking);
        btnGoalSetting = findViewById(R.id.btnGoalSetting);
        goalsRecyclerView = findViewById(R.id.goalsRecyclerView);
        emptyView = findViewById(R.id.emptyView);

        // Get the logged-in user's email from the intent
        loggedInUserEmail = getIntent().getStringExtra("email");

        // Fetch and display the user's name from the database
        String name = fetchUserNameFromDatabase(loggedInUserEmail);
        tvProfileName.setText(name);

        // Update the UI to display the user's goals
        refreshGoals();

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
                startActivityForResult(intent, GOAL_SETTING_REQUEST_CODE);
            }
        });

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

    // Method to refresh goals after adding a new goal
    public void refreshGoals() {
        Log.d("TitlePage", "Refreshing goals.");
        List<Goal> goals = fetchUserGoalsFromDatabase(loggedInUserId);
        // Update the UI to display the user's goals
        if (goals.isEmpty()) {
            Log.d("TitlePage", "Goals list is empty. Hiding RecyclerView, showing emptyView.");
            goalsRecyclerView.setVisibility(View.GONE); // Hide the RecyclerView
            emptyView.setVisibility(View.VISIBLE); // Show the emptyView
        } else {
            Log.d("TitlePage", "Goals list is not empty. Showing RecyclerView, hiding emptyView.");
            goalsRecyclerView.setVisibility(View.VISIBLE); // Show the RecyclerView
            emptyView.setVisibility(View.GONE); // Hide the emptyView
            GoalsAdapter goalsAdapter = new GoalsAdapter(this, goals, databaseHelper); // Pass the databaseHelper instance here
            goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            goalsRecyclerView.setAdapter(goalsAdapter);
        }
        // Set up the mood chart
        setupMoodPieChart(loggedInUserId);
    }

    // Define a constant for the goal setting request code
    private static final int GOAL_SETTING_REQUEST_CODE = 1;

    // Override onActivityResult to handle the result of the GoalSettingActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TitlePage", "onActivityResult triggered. requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (requestCode == GOAL_SETTING_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("TitlePage", "New goal added. Refreshing goals.");
            // A new goal was added, refresh the goals
            refreshGoals();
        }
    }

    private void setupMoodPieChart(long userId) {
        // Retrieve mood data from the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<MoodEntry> moodEntries = databaseHelper.getAllMoodEntries(userId);

        // Initialize the PieChart
        PieChart moodChart = findViewById(R.id.moodPieChart);
        moodChart.setUsePercentValues(true);
        moodChart.getDescription().setEnabled(false);
        moodChart.setExtraOffsets(5, 10, 5, 5);
        moodChart.setDragDecelerationFrictionCoef(0.95f);
        moodChart.setDrawHoleEnabled(true);
        moodChart.setHoleColor(Color.WHITE);
        moodChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> entries = new ArrayList<>();

        int totalEntries = moodEntries.size();
        int happyCount = 0;
        int sadCount = 0;
        int neutralCount = 0;

        // Count mood occurrences
        for (MoodEntry entry : moodEntries) {
            if (entry.getMoodValue() == 1) {
                happyCount++;
            } else if (entry.getMoodValue() == 2) {
                sadCount++;
            } else if (entry.getMoodValue() == 3) {
                neutralCount++;
            }
        }

        // Add mood entries to the PieChart
        if (totalEntries > 0) {
            entries.add(new PieEntry((float) happyCount / totalEntries, "Happy"));
            entries.add(new PieEntry((float) sadCount / totalEntries, "Sad"));
            entries.add(new PieEntry((float) neutralCount / totalEntries, "Neutral"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Mood Distribution");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(Color.rgb(34, 139, 34), Color.rgb(255, 69, 0), Color.rgb(0, 0, 205));

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        moodChart.setData(data);

        // Set up the legend
        Legend legend = moodChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);

        Description description = new Description();
        description.setText("Mood Distribution");
        moodChart.setDescription(description);

        // Refresh the chart
        moodChart.invalidate();
    }
}


