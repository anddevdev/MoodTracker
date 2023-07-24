
package com.example.megakursach;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentsActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button btnAddAppointment;
    private Button btnViewAppointments;
    private long loggedInUserId;
    private EditText etAppointmentTitle;
    private EditText etAppointmentDescription;
    private int selectedHour;
    private int selectedMinute;
    private String loggedInUserEmail;
    private ListView listView;

    private static final String COLUMN_APPOINTMENT_ID = "appointment_id";
    private static final String COLUMN_APPOINTMENT_TITLE = "title";
    private static final String COLUMN_APPOINTMENT_DESCRIPTION = "description";
    private static final String COLUMN_APPOINTMENT_DATE = "date";
    private static final String COLUMN_APPOINTMENT_TIME = "time";
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String COLUMN_USER_ID = "user_id";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_calendar);



        loggedInUserEmail = getIntent().getStringExtra("email");
        loggedInUserId = getUserIdFromDatabase(loggedInUserEmail);
        Log.d("AppointmentsActivity", "Received user_id: " + loggedInUserId);

        calendarView = findViewById(R.id.calendarView);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);
        btnViewAppointments = findViewById(R.id.btnViewAppointments);
        etAppointmentTitle = findViewById(R.id.etAppointmentTitle);
        etAppointmentDescription = findViewById(R.id.etAppointmentDescription);
        listView = findViewById(R.id.listViewAppointments);

        // Set click listener for calendar date selection
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Get the selected date in milliseconds
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                long selectedTimestamp = selectedDate.getTimeInMillis();

                // Convert the timestamp to a formatted date string
                String formattedDate = getFormattedDate(selectedTimestamp);

                // Call the method to show the time picker
                showTimePickerDialog();

                // Save the formatted date for later use
                saveFormattedDate(formattedDate);
            }
        });

        // Set click listener for "Add Appointment" button
        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etAppointmentTitle.getText().toString().trim();
                String description = etAppointmentDescription.getText().toString().trim();
                String date = getFormattedDate(calendarView.getDate());

                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AppointmentsActivity.this, "Title and Description cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform input validation
                    if (loggedInUserId == -1 || date.isEmpty()) {
                        Toast.makeText(AppointmentsActivity.this, "Invalid user ID or empty date", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get a reference to the database
                    DatabaseHelper databaseHelper = new DatabaseHelper(AppointmentsActivity.this);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();

                    // Create a ContentValues object to hold the appointment details
                    ContentValues values = new ContentValues();
                    values.put("user_id", loggedInUserId);
                    values.put("title", title);
                    values.put("description", description);
                    values.put("date", date);
                    values.put("time", getTimeString(selectedHour, selectedMinute));

                    // Insert the values into the database
                    long rowId = db.insert("appointments", null, values);

                    if (rowId != -1) {
                        Toast.makeText(AppointmentsActivity.this, "Appointment added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AppointmentsActivity.this, "Failed to add appointment", Toast.LENGTH_SHORT).show();
                    }

                    // Close the database
                    db.close();
                }
            }
        });

        // Set click listener for "View Appointments" button
        btnViewAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAppointments();
            }
        });
    }

    private String getFormattedDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private String getTimeString(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    private void saveFormattedDate(String formattedDate) {
        // TODO: Save the formatted date for later use
        // You can store it in a class variable or any other desired location.
    }

    public void showTimePickerDialog() {
        // Get the current time from the device
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        // Create a TimePickerDialog to pick the time for the appointment
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMinute = minute;

                // TODO: Use the selected time for the appointment
            }
        }, currentHour, currentMinute, true);

        // Show the TimePickerDialog
        timePickerDialog.show();
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

    private void viewAppointments() {
        // Retrieve appointments for the logged-in user
        List<Appointment> appointments = getAppointmentsForUser(loggedInUserId);

        // Create an instance of AppointmentsAdapter and set it to the ListView
        AppointmentsAdapter appointmentsAdapter = new AppointmentsAdapter(this, appointments);
        listView.setAdapter(appointmentsAdapter);
    }

    private List<Appointment> getAppointmentsForUser(long userId) {
        List<Appointment> appointments = new ArrayList<>();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] projection = {
                COLUMN_APPOINTMENT_ID,
                COLUMN_APPOINTMENT_TITLE,
                COLUMN_APPOINTMENT_DESCRIPTION,
                COLUMN_APPOINTMENT_DATE,
                COLUMN_APPOINTMENT_TIME
        };

        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(TABLE_APPOINTMENTS, projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long appointmentId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_TIME));

                // Create an Appointment object and add it to the list
                Appointment appointment = new Appointment(appointmentId, title, description, date, time);
                appointments.add(appointment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return appointments;
    }
}
