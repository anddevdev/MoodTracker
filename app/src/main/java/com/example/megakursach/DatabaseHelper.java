package com.example.megakursach;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_database";
    private static final int DATABASE_VERSION =8;
    private static final String TABLE_USERS = "users";
    private static final String TABLE_MOODS = "moods";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_MOOD_ID = "mood_id";
    private static final String COLUMN_MOOD_DATE = "date";
    private static final String COLUMN_MOOD_RATING = "rating";
    private static final String COLUMN_MOOD_COMMENT = "comment";

    // New table and column names for Appointment table
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String COLUMN_APPOINTMENT_ID = "appointment_id";
    private static final String COLUMN_APPOINTMENT_TITLE = "title";
    private static final String COLUMN_APPOINTMENT_DESCRIPTION = "description";
    private static final String COLUMN_APPOINTMENT_DATE = "date";
    private static final String COLUMN_APPOINTMENT_TIME = "time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT"
                + ")";
        db.execSQL(createUserTableQuery);

        String createMoodTableQuery = "CREATE TABLE " + TABLE_MOODS + " ("
                + COLUMN_MOOD_ID + " INTEGER,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_MOOD_DATE + " TEXT,"
                + COLUMN_MOOD_RATING + " INTEGER,"
                + COLUMN_MOOD_COMMENT + " TEXT"
                + ")";
        db.execSQL(createMoodTableQuery);

        String createAppointmentsTableQuery = "CREATE TABLE " + TABLE_APPOINTMENTS + " ("
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_APPOINTMENT_TITLE + " TEXT,"
                + COLUMN_APPOINTMENT_DESCRIPTION + " TEXT,"
                + COLUMN_APPOINTMENT_DATE + " TEXT,"
                + COLUMN_APPOINTMENT_TIME + " TEXT"
                + ")";
        db.execSQL(createAppointmentsTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    public long addUser(String fullName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

}
