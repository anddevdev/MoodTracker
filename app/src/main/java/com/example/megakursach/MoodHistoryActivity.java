
package com.example.megakursach;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodHistoryActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tvMoodInfo;

    private long loggedInUserId;
    private long selectedDateTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_history);

        tvMoodInfo = findViewById(R.id.tvMoodRating);

        loggedInUserId = getIntent().getLongExtra("user_id", -1);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateTimestamp = getTimestampFromDate(year, month, dayOfMonth);
            Log.d("MoodHistoryActivity", "Timestamp: " + selectedDateTimestamp);

            if (loggedInUserId != -1) {
                String moodInfo = fetchMoodInfoFromDatabase(loggedInUserId, selectedDateTimestamp);
                Log.d("MoodHistoryActivity", "Mood Info: " + moodInfo);
                tvMoodInfo.setText(moodInfo);
            }
        });
    }

    private long getTimestampFromDate(int year, int month, int dayOfMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
        Date selectedDate;
        try {
            selectedDate = sdf.parse(selectedDateString);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return selectedDate.getTime();
    }

    private String fetchMoodInfoFromDatabase(long userId, long timestamp) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String tableName = "moods";
        String[] projection = {"mood_id", "comment"};

        // Format the timestamp to remove milliseconds
        String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(timestamp));

        String selection = "user_id = ? AND date(date) = ?";
        String[] selectionArgs = {String.valueOf(userId), formattedTimestamp};

        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);
        Log.d("MoodHistoryActivity", "Timestamp: " + timestamp);
        Log.d("MoodHistoryActivity", "SelectionArgs: [" + userId + ", " + formattedTimestamp + "]");
        int rowsCount = cursor.getCount();
        Log.d("MoodHistoryActivity", "Rows Count: " + rowsCount);

        StringBuilder moodInfo = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                int moodId = cursor.getInt(cursor.getColumnIndexOrThrow("mood_id"));
                String moodString = getMoodString(moodId);

                String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                moodInfo.append(moodString).append(": ").append(comment).append("\n");
            } while (cursor.moveToNext());
        } else {
            moodInfo.append("No mood information found for the selected date.");
        }

        cursor.close();
        db.close();

        return moodInfo.toString();
    }


    private String getMoodString(int moodId) {
        switch (moodId) {
            case 1:
                return "Happy";
            case 2:
                return "Sad";
            case 3:
                return "Neutral";
            default:
                return "Unknown Mood";
        }
    }
}
