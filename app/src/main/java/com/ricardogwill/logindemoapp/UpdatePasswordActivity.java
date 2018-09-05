package com.ricardogwill.logindemoapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText;
    private Button updateNewPasswordButton;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        newPasswordEditText = findViewById(R.id.new_password_editText);
        updateNewPasswordButton = findViewById(R.id.update_new_password_button);

        // This adds the action bar to the top of the Activity.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                String userPasswordNew = newPasswordEditText.getText().toString();
                // Updating the password via these means DOES affect the login,
                // whereas the UpdateProfileActivity does NOT affect the login.
                firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdatePasswordActivity.this, "Your password has been updated!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UpdatePasswordActivity.this, "Password update failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

    // This deals with the "up" action in the ActionBar above: getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
