package com.example.zoomagymmanagement.member.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityEditProfileBinding;
import com.example.zoomagymmanagement.databinding.ActivityLoginBinding;
import com.example.zoomagymmanagement.member.MainActivity;
import com.example.zoomagymmanagement.member.MemberMainActivity;
import com.example.zoomagymmanagement.model.HelperClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    String fromScreen = "";
    String name, phone, bmi, age, gender, weight, address, imageUri = "";
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    StorageReference storageReference;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null){
            fromScreen = getIntent().getStringExtra("from");
            if (fromScreen.equals("auth")){
                binding.tvHeading.setText("Complete Profile");
                binding.ivBack.setVisibility(View.GONE);
            }else{
                binding.tvHeading.setText("Update Profile");
                binding.ivBack.setVisibility(View.VISIBLE);
            }
        }

        if (HelperClass.users != null){
            if (!HelperClass.users.getImage().isEmpty()){
                imageUri = HelperClass.users.getImage();
                Glide.with(binding.getRoot()).load(HelperClass.users.getImage()).into(binding.ivProfile);
            }
            if (!HelperClass.users.getName().isEmpty()){
                binding.userNameEt.setText(HelperClass.users.getName());
            }

            if (!HelperClass.users.getPhone().isEmpty()){
                binding.phoneEt.setText(HelperClass.users.getPhone());
            }

            if (!HelperClass.users.getAddress().isEmpty()){
                binding.addressEt.setText(HelperClass.users.getAddress());
            }

            if (!HelperClass.users.getBmi().isEmpty()){
                binding.bmiEt.setText(HelperClass.users.getBmi());
            }

            if (!HelperClass.users.getAge().isEmpty()){
                binding.ageEt.setText(HelperClass.users.getAge());
            }

            if (!HelperClass.users.getGender().isEmpty()){
                binding.genderEt.setText(HelperClass.users.getGender());
            }

            if (!HelperClass.users.getWeight().isEmpty()){
                binding.weightEt.setText(HelperClass.users.getWeight());
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");

        binding.bmiEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBMIDialog();
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
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
                    saveChanges();
                }
            }
        });

    }

    private void saveChanges() {
        progressDialog.show();
        if (imageUri.equals(HelperClass.users.getImage())){
            Map<String, Object> update = new HashMap<String, Object>();
            update.put("name", name);
            update.put("phone", phone);
            update.put("bmi", bmi);
            update.put("age", age);
            update.put("gender", gender);
            update.put("weight", weight);
            dbRefUsers.child(auth.getCurrentUser().getUid()).updateChildren(update).addOnCompleteListener(task -> {
                HelperClass.users.setName(name);
                HelperClass.users.setPhone(phone);
                HelperClass.users.setBmi(bmi);
                HelperClass.users.setAge(age);
                HelperClass.users.setGender(gender);
                HelperClass.users.setWeight(weight);
                progressDialog.dismiss();
                showMessage("Successfully Saved");
                if (fromScreen.equals("auth")){
                    startActivity(new Intent(EditProfileActivity.this, MemberMainActivity.class));
                    finishAffinity();
                }else{
                    finish();
                }
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getMessage());
            });
        }else{
            Uri uriImage = Uri.parse(imageUri);
            StorageReference imageRef = storageReference.child(uriImage.getLastPathSegment());
            imageRef.putFile(uriImage).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUri = uri.toString();
                Map<String, Object> update = new HashMap<>();
                update.put("image", downloadUri);
                update.put("name", name);
                update.put("phone", phone);
                update.put("address", address);
                update.put("bmi", bmi);
                update.put("age", age);
                update.put("gender", gender);
                update.put("weight", weight);
                dbRefUsers.child(auth.getCurrentUser().getUid()).updateChildren(update).addOnCompleteListener(task -> {
                    HelperClass.users.setName(name);
                    HelperClass.users.setPhone(phone);
                    HelperClass.users.setAddress(address);
                    HelperClass.users.setBmi(bmi);
                    HelperClass.users.setAge(age);
                    HelperClass.users.setGender(gender);
                    HelperClass.users.setWeight(weight);
                    HelperClass.users.setImage(downloadUri);
                    progressDialog.dismiss();
                    showMessage("Successfully Saved");
                    if (fromScreen.equals("auth")){
                        startActivity(new Intent(EditProfileActivity.this, MemberMainActivity.class));
                        finishAffinity();
                    }else{
                        finish();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showMessage(e.getMessage());
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            });
        }
    }

    private Boolean isValidated() {
        name = binding.userNameEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();
        address = binding.addressEt.getText().toString().trim();
        bmi = binding.bmiEt.getText().toString().trim();
        age = binding.ageEt.getText().toString().trim();
        gender = binding.genderEt.getText().toString().trim();
        weight = binding.weightEt.getText().toString().trim();

        if (imageUri.isEmpty()) {
            showMessage("Please choose profile picture");
            return false;
        }

        if (name.isEmpty()) {
            showMessage("Please enter userName");
            return false;
        }

        if (phone.isEmpty()) {
            showMessage("Please enter phone");
            return false;
        }

        if (address.isEmpty()) {
            showMessage("Please enter address");
            return false;
        }

        if (bmi.isEmpty()) {
            showMessage("Please calculate BMI");
            return false;
        }

        if (age.isEmpty()) {
            showMessage("Please enter age");
            return false;
        }

        if (gender.isEmpty()) {
            showMessage("Please enter gender");
            return false;
        }

        if (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female") && !gender.equalsIgnoreCase("Others")) {
            showMessage("Invalid gender. Please enter Male, Female, or Others");
            return false;
        }

        if (weight.isEmpty()) {
            showMessage("Please enter weight");
            return false;
        }


        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showBMIDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.item_bmi);

        Button btnCalculate = (Button) dialog.findViewById(R.id.btnCalculate);
        TextInputEditText weightEt = (TextInputEditText) dialog.findViewById(R.id.weightEt);
        TextInputEditText heightEt = (TextInputEditText) dialog.findViewById(R.id.heightEt);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                String weightStr = weightEt.getText().toString();
                String heightStr = heightEt.getText().toString();

                if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
                    try {
                        float weight = Float.parseFloat(weightStr);
                        float height = Float.parseFloat(heightStr);

                        float bmi = weight / (height * height);
                        binding.bmiEt.setText(String.format("%.2f", bmi));
                        dialog.dismiss();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please enter both weight and height.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData().toString();
            binding.ivProfile.setImageURI(data.getData());
        }
    }

}