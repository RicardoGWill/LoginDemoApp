package com.ricardogwill.logindemoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity {

    // Notes for further improvement: "Swipe Refresh" and "Image Caching".

    private ImageView updateImageView;
    private EditText updateNameEditText, updateEmailEditText, updateAgeEditText;
    private Button saveButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    // Everything down to the bottom of the "onActivityResult" method was copied (and then slightly modified)
    // from "RegistrationActivity.java".
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                // "updateImageView" is used because it is the name of our ImageView in this Activity.
                updateImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
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
        firebaseStorage = FirebaseStorage.getInstance();

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

        // Most of this next major block is copied from "ProfileActivity.java".
        final StorageReference storageReference = firebaseStorage.getReference();
        // Get the image stored on Firebase via "User id/Images/Profile Pic.jpg".
        storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Using "Picasso" (http://square.github.io/picasso/) after adding the dependency in the Gradle.
                // ".fit().centerInside()" fits the entire image into the specified area.
                // Finally, add "READ" and "WRITE" external storage permissions in the Manifest.
                // "updateImageView" is used, as it is the ImageView in this Activity.
                Picasso.get().load(uri).fit().centerInside().into(updateImageView);
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

                if(imagePath != null) {
                    // This next block is copied (and then altered) from within the "sendUserData()" function in "RegistrationActivity.java".
                    StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic"); //User id/Images/Profile Pic.jpg
                    UploadTask uploadTask = imageReference.putFile(imagePath);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(UpdateProfileActivity.this, "Upload Successful.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                finish();
            }
        });

        updateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The following 4 lines of code are copied from an onClickListener in "RegistrationActivity.java".
                Intent profileIntent = new Intent();
                // Type can be "image/*", "application/*", "audio/*"
                profileIntent.setType("image/*");
                profileIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE);
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
