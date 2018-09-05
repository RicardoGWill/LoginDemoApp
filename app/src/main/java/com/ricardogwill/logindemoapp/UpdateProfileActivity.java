package com.ricardogwill.logindemoapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity {

    private ImageView updateImageView;
    private EditText updateNameEditText, updateEmailEditText, updateAgeEditText;
    private Button saveButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        updateImageView = findViewById(R.id.update_imageView);
        updateNameEditText = findViewById(R.id.update_name_editText);
        updateEmailEditText = findViewById(R.id.update_email_editText);
        updateAgeEditText = findViewById(R.id.update_age_editText);
        saveButton = findViewById(R.id.save_button);

        // This adds the action bar to the top of the Activity.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // From here until the "});" part after the Toast has been copied from ProfileActivity.java,
        // but the "setText" parts and the Toast have been changed.
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                updateNameEditText.setText(userProfile.getUserName());
                updateEmailEditText.setText(userProfile.getUserEmail());
                updateAgeEditText.setText(userProfile.getUserAge());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = updateNameEditText.getText().toString();
                String email = updateEmailEditText.getText().toString();
                String age = updateAgeEditText.getText().toString();

                UserProfile userProfile = new UserProfile(name, email, age);

                databaseReference.setValue(userProfile);

                finish();


            }
        });

    }

    // This deals with the "up" action in the ActionBar above: getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
