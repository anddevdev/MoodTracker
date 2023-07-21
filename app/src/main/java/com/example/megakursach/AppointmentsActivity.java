package com.example.megakursach;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_calendar);

        loggedInUserId = getIntent().getLongExtra("user_id", -1);

        calendarView = findViewById(R.id.calendarView);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);
        btnViewAppointments = findViewById(R.id.btnViewAppointments);
        etAppointmentTitle = findViewById(R.id.etAppointmentTitle);
        etAppointmentDescription = findViewById(R.id.etAppointmentDescription);

        // Set click listener for calendar date selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // TODO: Implement actions on date selection, if needed
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
                    // Get a reference to the database
                    DatabaseHelper databaseHelper = new DatabaseHelper(AppointmentsActivity.this);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();

                    // Perform input validation
                    if (loggedInUserId == -1 || date.isEmpty()) {
                        Toast.makeText(AppointmentsActivity.this, "Invalid user ID or empty date", Toast.LENGTH_SHORT).show();
                        return;
                    }

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
                // TODO: Implement logic to view existing appointments
                // You can open a new activity to display the list of appointments, or any other desired action.
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

    public void showTimePickerDialog(View v) {
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
            }
        }, currentHour, currentMinute, true);

        // Show the TimePickerDialog
        timePickerDialog.show();
    }
}
