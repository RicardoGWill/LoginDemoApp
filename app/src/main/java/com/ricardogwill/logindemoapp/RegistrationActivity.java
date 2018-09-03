package com.ricardogwill.logindemoapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText enterNameEditText, enterEmailEditText, createPasswordEditText;
    private Button registerButton;
    private TextView goToLoginTextView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();

        enterNameEditText = findViewById(R.id.enter_email_editText);
        enterEmailEditText = findViewById(R.id.enter_email_editText);
        createPasswordEditText = findViewById(R.id.create_password_editText);
        registerButton = findViewById(R.id.register_button);
        goToLoginTextView = findViewById(R.id.go_to_login_textView);

        goToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });
    }

    public void registerButtonOnClick(View view) {
        if (validateRegistration() == true) {
            // Upload registration data to the database.
            String userEmail = enterEmailEditText.getText().toString().trim();
            String userPassword = createPasswordEditText.getText().toString().trim();

            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration Failed.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private boolean validateRegistration() {
        boolean result = false;
        String name = enterNameEditText.getText().toString();
        String password = createPasswordEditText.getText().toString();
        String email = enterEmailEditText.getText().toString();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please Enter All Details.", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }
        return result;
    }

}
