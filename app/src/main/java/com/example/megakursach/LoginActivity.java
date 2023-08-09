package com.example.megakursach;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Perform input validation
        if (!isValidEmail(email)) {
            etEmail.setError("Invalid email address");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password cannot be empty");
            return;
        }

        // Perform authentication logic
        if (authenticateUser(email, password)) {
            // Authentication successful
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, TitlePage.class);
            intent.putExtra("email", email);
            startActivity(intent);

            finish(); // Optional: Close the login activity
        } else {
            // Authentication failed
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }


    private boolean authenticateUser(String email, String password) {
        // Get a reference to the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Define the table and columns to query
        String tableName = "users";
        String[] projection = {"email", "password"};

        // Set up the selection criteria
        String selection = "email = ?";
        String[] selectionArgs = {email};

        // Query the database
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);

        // Check if a matching user is found
        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            if (storedPassword.equals(password)) {
                // Authentication successful
                cursor.close();
                db.close();
                return true;
            }
        }

        // Authentication failed
        cursor.close();
        db.close();
        return false;
    }
}
