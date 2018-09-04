package com.ricardogwill.logindemoapp;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView attemptsTextView, registerTextView;
    private int counter = 5;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        loginButton = findViewById(R.id.login_button);
        attemptsTextView = findViewById(R.id.attempts_textView);
        registerTextView = findViewById(R.id.register_textView);
        forgotPasswordTextView = findViewById(R.id.forgot_password_textView);

        attemptsTextView.setText("Attempts Remaining: " + String.valueOf(counter));

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            finish();
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        }

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordActivity.class));
            }
        });


    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser != null) {
//            finish();
//            startActivity(new Intent(MainActivity.this, SecondActivity.class));
//        }
//    }

    public void loginButtonOnClick(View view) {
        validate(emailEditText.getText().toString(), passwordEditText.getText().toString());
    }

    private void validate(String userName, String userPassword) {
        // This "if" statement uses the "validateRegistration" method.  If the EditText fields are NOT blank, it will try to log in.
        if (validateRegistration() == true) {

            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            // This is the method that tries to log a user in.
            firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
//                        Toast.makeText(MainActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(MainActivity.this, SecondActivity.class));
                        checkEmailVerification();
                    } else {
                        Toast.makeText(MainActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                        counter--;
                        attemptsTextView.setText("Attempts Remaining: " + String.valueOf(counter));
                        progressDialog.dismiss();
                        if (counter == 0) {
                            loginButton.setEnabled(false);
                        }
                    }
                }
            });
        }
    }

    // This evaluates to "true" if neither EditText is empty when trying to log in.
    private boolean validateRegistration() {
        boolean result = false;
        String name = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please Enter All Details.", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }
        return result;
    }

    // This checks if the user has verified his email address by checking his email account.
    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag  = firebaseUser.isEmailVerified();

        if (emailFlag) {
            finish();
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        } else {
            Toast.makeText(this, "Please verify your email.", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }



}
