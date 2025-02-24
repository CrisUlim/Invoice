package com.example.invoice_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.invoice_2.data.AppDatabase;
import com.example.invoice_2.data.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if already logged in
        if (checkLoginStatus()) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> login());
        registerButton.setOnClickListener(v -> showRegisterDialog());
    }

    private boolean checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    private void login() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            User user = db.userDao().findByUsername(username);

            runOnUiThread(() -> {
                if (user != null && user.getPassword().equals(password)) {
                    // Save login status
                    SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("isLoggedIn", true).apply();

                    startMainActivity();
                } else {
                    Toast.makeText(this, "Invalid username or password", 
                        Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register New User");

        // Inflate and set the layout for the dialog
        final EditText usernameInput = new EditText(this);
        final EditText passwordInput = new EditText(this);
        
        usernameInput.setHint("Username");
        passwordInput.setHint("Password");
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | 
            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 0, 20, 0);
        layout.addView(usernameInput);
        layout.addView(passwordInput);

        builder.setView(layout);

        builder.setPositiveButton("Register", (dialog, which) -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                
                // Check if username already exists
                if (db.userDao().exists(username)) {
                    runOnUiThread(() -> Toast.makeText(this, 
                        "Username already exists", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Create new user
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                
                db.userDao().insert(newUser);
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    this.usernameInput.setText(username);
                    this.passwordInput.setText("");
                });
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
} 