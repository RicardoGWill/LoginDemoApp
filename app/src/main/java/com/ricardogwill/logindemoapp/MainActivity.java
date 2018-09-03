package com.ricardogwill.logindemoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText, passwordEditText;
    private Button loginButton;
    private TextView attemptsTextView, registerTextView;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = findViewById(R.id.name_editText);
        passwordEditText = findViewById(R.id.password_editText);
        loginButton = findViewById(R.id.login_button);
        attemptsTextView = findViewById(R.id.attempts_textView);
        registerTextView = findViewById(R.id.register_textView);

        attemptsTextView.setText("Attempts Remaining: " + String.valueOf(counter));

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });
    }

    public void loginButtonOnClick(View view) {
        validate(nameEditText.getText().toString(), passwordEditText.getText().toString());
    }

    private void validate(String userName, String userPassword) {
        if (userName.equals("Admin") && userPassword.equals("1234")) {
            Intent secondActivityIntent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(secondActivityIntent);
        } else {
            counter--;
            // Revise attemptsTextView to new number.
            attemptsTextView.setText("Attempts Remaining: " + String.valueOf(counter));
            // Tell person that the login failed.
            Toast.makeText(this, "Login Failed. Attempts Remaining: " + counter, Toast.LENGTH_SHORT).show();
            // Disable loginButton when the counter == 0.
            if (counter == 0) {
                loginButton.setEnabled(false);
            }
        }
    }



}
