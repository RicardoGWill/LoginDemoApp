package com.ricardogwill.logindemoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private EditText enterNameEditText, enterEmailEditText, createPasswordEditText, ageEditText;
    private Button registerButton;
    private TextView goToLoginTextView;
    private FirebaseAuth firebaseAuth;
    private ImageView profileImageView;
    String name, email, password, age;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        enterNameEditText = findViewById(R.id.update_name_editText);
        enterEmailEditText = findViewById(R.id.update_email_editText);
        createPasswordEditText = findViewById(R.id.create_password_editText);
        registerButton = findViewById(R.id.save_button);
        goToLoginTextView = findViewById(R.id.go_to_login_textView);
        ageEditText = findViewById(R.id.update_age_editText);
        profileImageView = findViewById(R.id.update_imageView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent();
                // Type can be "image/*", "application/*", "audio/*"
                profileIntent.setType("image/*");
                profileIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE);
            }
        });

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
//                        sendEmailVerification();
                        // See "sendUserData()" function later in this Activity.
                        sendUserData();
                        firebaseAuth.signOut();
                        Toast.makeText(RegistrationActivity.this, "Successfully registered. Upload complete.", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(RegistrationActivity.this, "Successfully registered. Verification email has been sent.", Toast.LENGTH_SHORT).show();
//                        firebaseAuth.signOut();
                        finish();
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
        name = enterNameEditText.getText().toString();
        email = enterEmailEditText.getText().toString();
        password = createPasswordEditText.getText().toString();
        age = ageEditText.getText().toString();


        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty() || imagePath == null) {
            Toast.makeText(this, "Please Enter All Details.", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }
        return result;
    }

    // Sends an email to verify the correct email address is input during registration.
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // See "sendUserData()" function later in this Activity.
                        sendUserData();
                        Toast.makeText(RegistrationActivity.this, "Successfully registered. Verification email has been sent.", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Verification email has not been sent.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // Get "User UID" from Firebase > Authentification > Users.
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic"); //User id/Images/Profile Pic.jpg
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegistrationActivity.this, "Upload Successful.", Toast.LENGTH_SHORT).show();
            }
        });
        UserProfile userProfile = new UserProfile(name, email, age);
        databaseReference.setValue(userProfile);
    }

}
