package com.example.zoomagymmanagement.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityAddFitnessPlansBinding;
import com.example.zoomagymmanagement.databinding.ActivityAdminMainBinding;
import com.example.zoomagymmanagement.model.FitnessPlansModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddFitnessPlansActivity extends AppCompatActivity {
    ActivityAddFitnessPlansBinding binding;
    String title, details, imageUri = "";
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefFitnessPlans;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFitnessPlansBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        dbRefFitnessPlans = FirebaseDatabase.getInstance().getReference("FitnessPlans");
        storageReference = FirebaseStorage.getInstance().getReference("FitnessPlansPictures");

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        binding.ivPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 123);
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidated()){
                    addFitnessPlans();
                }
            }
        });

    }

    private void addFitnessPlans() {
        // Display a progress dialog to indicate the addition process
        progressDialog.show();

        // Parse the image URI to a Uri object
        Uri uriImage = Uri.parse(imageUri);

        // Create a reference to store the image in Firebase Storage
        StorageReference imageRef = storageReference.child(uriImage.getLastPathSegment());

        // Upload the image file to Firebase Storage
        imageRef.putFile(uriImage).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // If image upload is successful

            // Get the download URL of the uploaded image
            String downloadUri1 = uri.toString();

            // Generate a unique ID for the fitness plan
            String plansId = dbRefFitnessPlans.push().getKey();

            // Create a FitnessPlansModel object with plan details and image URL
            FitnessPlansModel model = new FitnessPlansModel(plansId, title, details, downloadUri1);

            // Set the fitness plan data in the Firebase Realtime Database
            dbRefFitnessPlans.child(plansId).setValue(model).addOnSuccessListener(unused -> {
                // If setting data in database is successful

                // Show a success message
                showMessage("Added Successfully");

                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Finish the current activity
                finish();
            }).addOnFailureListener(e -> {
                // If setting data in database fails

                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Show an error message indicating the failure reason
                showMessage(e.getLocalizedMessage());
            });

        }).addOnFailureListener(e -> {
            // If getting download URL fails

            // Dismiss the progress dialog
            progressDialog.dismiss();

            // Show an error message indicating the failure reason
            showMessage(e.getLocalizedMessage());
        })).addOnFailureListener(e -> {
            // If uploading image file fails

            // Dismiss the progress dialog
            progressDialog.dismiss();

            // Show an error message indicating the failure reason
            showMessage(e.getLocalizedMessage());
        });
    }

    private Boolean isValidated() {
        title = binding.titleEt.getText().toString().trim();
        details = binding.detailsEt.getText().toString().trim();

        if (imageUri.isEmpty()) {
            showMessage("Please choose plan picture");
            return false;
        }

        if (title.isEmpty()) {
            showMessage("Please enter title");
            return false;
        }

        if (details.isEmpty()) {
            showMessage("Please enter details");
            return false;
        }


        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData().toString();
            binding.ivPlan.setImageURI(data.getData());
        }
    }

}