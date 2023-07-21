package com.example.megakursach;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodTrackerActivity extends AppCompatActivity {

    private Button btnSubmit;
    private Button btnViewHistory;
    private int selectedMood;
    private EditText etComment;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracker);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        etComment = findViewById(R.id.etComment);

        loggedInUserEmail = getIntent().getStringExtra("email");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoodSelectionDialog();
            }
        });

        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long loggedInUserId = getUserIdFromDatabase(loggedInUserEmail);
                Intent intent = new Intent(MoodTrackerActivity.this, MoodHistoryActivity.class);
                intent.putExtra("user_id", loggedInUserId);
                startActivity(intent);
            }
        });
    }

    private void showMoodSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Mood");
        builder.setMessage("How do you feel today?");

        builder.setPositiveButton("Happy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedMood = 1;
                saveMoodToDatabase();
            }
        });

        builder.setNegativeButton("Sad", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedMood = 2;
                saveMoodToDatabase();
            }
        });

        builder.setNeutralButton("Neutral", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedMood = 3;
                saveMoodToDatabase();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveMoodToDatabase() {
        String comment = etComment.getText().toString().trim();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        long loggedInUserId = getUserIdFromDatabase(loggedInUserEmail);

        if (loggedInUserId == -1 || comment.isEmpty()) {
            Toast.makeText(this, "Invalid user ID or empty comment", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("user_id", loggedInUserId);
        values.put("date", getCurrentDateWithoutTime());
        values.put("mood_id", selectedMood);
        values.put("comment", comment);

        long rowId = db.insert("moods", null, values);

        if (rowId != -1) {
            Toast.makeText(this, "Mood saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save mood", Toast.LENGTH_SHORT).show();
        }

        db.close();
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

    private String getCurrentDateWithoutTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
