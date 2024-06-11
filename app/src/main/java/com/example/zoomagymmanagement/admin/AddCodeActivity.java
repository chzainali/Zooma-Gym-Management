package com.example.zoomagymmanagement.admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityAddCodeBinding;
import com.example.zoomagymmanagement.databinding.ActivityQrCodesBinding;
import com.example.zoomagymmanagement.model.CodesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddCodeActivity extends AppCompatActivity {
    ActivityAddCodeBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefCodes;
    String strCode;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefCodes = FirebaseDatabase.getInstance().getReference("QRCodes");

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getAllCodes();

        binding.btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the code entered by the user
                strCode = binding.codeEt.getText().toString();

                // Check if the code is empty
                if (strCode.isEmpty()){
                    // If the code is empty, show a toast message prompting the user to enter a code
                    Toast.makeText(AddCodeActivity.this, "Please enter code", Toast.LENGTH_SHORT).show();
                } else {
                    // If the code is not empty

                    // Check if the list already contains the entered code
                    if (list.contains(strCode)){
                        // If the code already exists in the list, show a toast message indicating that the code should be unique
                        Toast.makeText(AddCodeActivity.this, "Please enter unique code", Toast.LENGTH_SHORT).show();
                    } else {
                        // If the code is unique

                        // Display a progress dialog to indicate the code creation process
                        progressDialog.show();

                        // Generate a unique ID for the code
                        String pushId = dbRefCodes.push().getKey();

                        // Get the current date and time
                        Date currentDate = Calendar.getInstance().getTime();

                        // Format the current date and time
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss", Locale.getDefault());
                        String formattedDate = dateFormat.format(currentDate);

                        // Create a CodesModel object with code details
                        CodesModel model = new CodesModel(pushId, strCode, formattedDate, "", "", "Available");

                        // Set the code data in the Firebase Realtime Database
                        dbRefCodes.child(pushId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // When setting data in the database is complete

                                if (task.isSuccessful()){
                                    // If setting data is successful, show a toast message indicating success and finish the activity
                                    Toast.makeText(AddCodeActivity.this, "Successfully Created and Saved", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    // If setting data fails, dismiss the progress dialog and show a toast message with the failure reason
                                    progressDialog.dismiss();
                                    Toast.makeText(AddCodeActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // If an error occurs while setting data in the database, dismiss the progress dialog and show a toast message with the failure reason
                                progressDialog.dismiss();
                                Toast.makeText(AddCodeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

    }

    private void getAllCodes() {
        progressDialog.show();
        dbRefCodes.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            CodesModel model = ds.getValue(CodesModel.class);
                            list.add(model.getCode());
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(AddCodeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}