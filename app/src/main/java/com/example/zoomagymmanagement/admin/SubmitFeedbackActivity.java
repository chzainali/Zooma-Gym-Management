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
import com.example.zoomagymmanagement.databinding.ActivitySubmitFeedbackBinding;
import com.example.zoomagymmanagement.model.FeedbackModel;
import com.example.zoomagymmanagement.model.FitnessPlansModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SubmitFeedbackActivity extends AppCompatActivity {
    ActivitySubmitFeedbackBinding binding;
    String subject, details, imageUri = "";
    String formattedDate = "";
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefFeedback;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmitFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        dbRefFeedback = FirebaseDatabase.getInstance().getReference("Feedback");
        storageReference = FirebaseStorage.getInstance().getReference("FeedbackPictures");

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
                startActivityForResult(Intent.createChooser(intent, "Select Feedback Image"), 123);
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
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss", Locale.getDefault());
        formattedDate = dateFormat.format(currentDate);
        progressDialog.show();
        Uri uriImage = Uri.parse(imageUri);
        StorageReference imageRef = storageReference.child(uriImage.getLastPathSegment());
        imageRef.putFile(uriImage).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUri1 = uri.toString();
            String feedbackId = dbRefFeedback.push().getKey();
            FeedbackModel model = new FeedbackModel(feedbackId, auth.getCurrentUser().getUid(), subject, details, formattedDate, downloadUri1, "", "Pending");
            dbRefFeedback.child(feedbackId).setValue(model).addOnSuccessListener(unused -> {
                showMessage("Submitted Successfully");
                progressDialog.dismiss();
                finish();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            });

        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showMessage(e.getLocalizedMessage());
        })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showMessage(e.getLocalizedMessage());
        });

    }

    private Boolean isValidated() {
        subject = binding.subjectEt.getText().toString().trim();
        details = binding.detailsEt.getText().toString().trim();

        if (imageUri.isEmpty()) {
            showMessage("Please choose picture");
            return false;
        }

        if (subject.isEmpty()) {
            showMessage("Please enter subject");
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