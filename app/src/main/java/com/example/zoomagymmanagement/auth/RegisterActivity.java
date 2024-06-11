package com.example.zoomagymmanagement.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityRegisterBinding;
import com.example.zoomagymmanagement.member.ui.profile.EditProfileActivity;
import com.example.zoomagymmanagement.model.HelperClass;
import com.example.zoomagymmanagement.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    Animation rotate;
    String name, email, phone, password;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        binding.ivLogo.setAnimation(rotate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        binding.llBottom.setOnClickListener(view ->
                finish());

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidated()){
                    registerUser();
                }
            }
        });

    }

    private void registerUser() {
        // Display a progress dialog to indicate registration process
        progressDialog.show();

        // Create a new user account with email and password
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            // On successful creation of user account

            // Create a new UserModel object with user information
            UserModel model = new UserModel(auth.getCurrentUser().getUid(), name, email, phone, password, "", "","","","","", "Monthly");

            // Set the user information in the Firebase Realtime Database
            dbRefUsers.child(auth.getCurrentUser().getUid()).setValue(model).addOnCompleteListener(task -> {
                // On successful completion of setting user data in database

                // Store the user model in a helper class for future reference
                HelperClass.users = model;

                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Show a message indicating successful registration
                showMessage("Registered Successfully");

                // Create an intent to navigate to the EditProfileActivity
                Intent intent = new Intent(RegisterActivity.this, EditProfileActivity.class);

                // Pass additional information through intent
                intent.putExtra("from", "auth");

                // Start the EditProfileActivity
                startActivity(intent);

                // Finish the current activity and all parent activities
                finishAffinity();
            }).addOnFailureListener(e -> {
                // If setting user data in database fails

                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Show an error message indicating the failure reason
                showMessage(e.getLocalizedMessage());
            });
        }).addOnFailureListener(e -> {
            // If creating user account fails

            // Dismiss the progress dialog
            progressDialog.dismiss();

            // Show an error message indicating the failure reason
            showMessage(e.getLocalizedMessage());
        });
    }


    private Boolean isValidated() {
        name = binding.userNameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();
        password = binding.passET.getText().toString().trim();

        if (name.isEmpty()) {
            showMessage("Please enter userName");
            return false;
        }
        if (email.isEmpty()) {
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }
        if (phone.isEmpty()) {
            showMessage("Please enter phone");
            return false;
        }
        if (password.isEmpty()) {
            showMessage("Please enter password");
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}