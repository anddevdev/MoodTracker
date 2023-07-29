package com.example.megakursach;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GoalSettingActivity extends AppCompatActivity {

    private EditText etGoalTitle;
    private EditText etGoalDescription;
    private EditText etTargetValue;
    private EditText etTargetDate;
    private Button btnSaveGoal;
    private Button btnCancel;

    private String loggedInUserEmail;

    private long loggedInUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_setting);

        loggedInUserEmail = getIntent().getStringExtra("email");
        loggedInUserId = getUserIdFromDatabase(loggedInUserEmail);
        Log.d("AppointmentsActivity", "Received user_id: " + loggedInUserId);

        etGoalTitle = findViewById(R.id.etGoalTitle);
        etGoalDescription = findViewById(R.id.etGoalDescription);
        etTargetValue = findViewById(R.id.etTargetValue);
        etTargetDate = findViewById(R.id.etTargetDate);
        btnSaveGoal = findViewById(R.id.btnSaveGoal);
        btnCancel = findViewById(R.id.btnCancel);

        btnSaveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoal();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity without saving the goal
                finish();
            }
        });
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

    private void saveGoal() {
        String title = etGoalTitle.getText().toString().trim();
        String description = etGoalDescription.getText().toString().trim();
        String targetValueStr = etTargetValue.getText().toString().trim();
        String targetDate = etTargetDate.getText().toString().trim();

        // Perform input validation
        if (title.isEmpty() || description.isEmpty() || targetValueStr.isEmpty() || targetDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int targetValue = Integer.parseInt(targetValueStr);

        // Get a reference to the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Create a ContentValues object to hold the goal details
        ContentValues values = new ContentValues();
        values.put("user_id", loggedInUserId);
        values.put("title", title);
        values.put("description", description);
        values.put("target_value", targetValue);
        values.put("target_date", targetDate);

        // Insert the values into the database
        long rowId = db.insert("goals", null, values);

        if (rowId != -1) {
            Toast.makeText(this, "Goal saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save goal", Toast.LENGTH_SHORT).show();
        }

        // Close the database
        db.close();

        // Close the activity after saving the goal
        finish();
    }
}
