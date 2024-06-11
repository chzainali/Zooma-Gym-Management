package com.example.zoomagymmanagement.member.ui.home;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.admin.AddCodeActivity;
import com.example.zoomagymmanagement.admin.FeedbackDetailsActivity;
import com.example.zoomagymmanagement.databinding.ActivityAddCodeBinding;
import com.example.zoomagymmanagement.databinding.ActivityTakeAttendanceBinding;
import com.example.zoomagymmanagement.model.AttendanceModel;
import com.example.zoomagymmanagement.model.CapActivity;
import com.example.zoomagymmanagement.model.CodesModel;
import com.example.zoomagymmanagement.model.GymCapacityModel;
import com.example.zoomagymmanagement.model.UserModel;
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
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TakeAttendanceActivity extends AppCompatActivity {
    ActivityTakeAttendanceBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefCodes;
    DatabaseReference dbRefAttendance;
    DatabaseReference dbRefGymCapacitor;
    List<CodesModel> codesList = new ArrayList<>();
    String getCurrentDate = "";
    CodesModel codeMainModel;
    Boolean isAlreadyAttend = false;
    int gymCapacityCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakeAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefCodes = FirebaseDatabase.getInstance().getReference("QRCodes");
        dbRefAttendance = FirebaseDatabase.getInstance().getReference("Attendance");
        dbRefGymCapacitor = FirebaseDatabase.getInstance().getReference("GymCapacitor");

        getCurrentDate();

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getGymCapacity();

    }

    // Method to fetch gym capacity from the database
    private void getGymCapacity() {
        // Display a progress dialog
        progressDialog.show();

        // Add a ValueEventListener to fetch gym capacity from the database
        dbRefGymCapacitor.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // When data is changed in the database

                if (snapshot.exists()) {
                    // If gym capacity data exists in the database

                    // Retrieve GymCapacityModel object from the snapshot
                    GymCapacityModel model = snapshot.getValue(GymCapacityModel.class);

                    // Retrieve gym capacity count
                    gymCapacityCount = Integer.parseInt(model.getCapacity());

                    // Proceed to take attendance
                    takeAttendance();
                } else {
                    // If gym capacity data doesn't exist in the database

                    // Proceed to take attendance directly
                    takeAttendance();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // If onCancelled event occurs (e.g., due to network error)

                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Show a toast with the error message
                Toast.makeText(TakeAttendanceActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to take attendance
    private void takeAttendance() {
        // Add a ValueEventListener to fetch attendance data from the database
        dbRefAttendance.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // When data is changed in the database

                if (snapshot.exists()) {
                    // If attendance data exists in the database

                    // Iterate through each snapshot of the database
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            // Retrieve AttendanceModel object from the snapshot
                            AttendanceModel model = ds.getValue(AttendanceModel.class);

                            // Check if the current date and user ID match any existing attendance record
                            if (model.getCurrentDate().equals(getCurrentDate)  && model.getUserId().equals(auth.getCurrentUser().getUid())){
                                isAlreadyAttend = true;
                                break;
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    // Check if the user has already attended for today
                    if (!isAlreadyAttend){
                        // If user hasn't attended for today, proceed to fetch all codes for attendance
                        getAllCodes();
                    } else {
                        // If user has already attended for today, show a toast message and finish the activity
                        progressDialog.dismiss();
                        Toast.makeText(TakeAttendanceActivity.this, "You have already taken the attendance for today", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    // If attendance data doesn't exist in the database, proceed to fetch all codes for attendance directly
                    getAllCodes();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // If onCancelled event occurs (e.g., due to network error)

                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Show a toast with the error message
                Toast.makeText(TakeAttendanceActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch all codes for attendance
    private void getAllCodes() {
        // Add a ValueEventListener to fetch all codes from the database
        dbRefCodes.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // When data is changed in the database

                if (snapshot.exists()) {
                    // If codes data exists in the database

                    // Clear the codesList to avoid duplicate entries
                    codesList.clear();

                    // Iterate through each snapshot of the database
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            // Retrieve CodesModel object from the snapshot
                            CodesModel model = ds.getValue(CodesModel.class);

                            // Add CodesModel object to the codesList
                            codesList.add(model);
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    // Proceed to scan QR code
                    scanCode();
                } else {
                    // If codes data doesn't exist in the database

                    // Dismiss the progress dialog
                    progressDialog.dismiss();

                    // Show a toast indicating no QR Code found for attendance and finish the activity
                    Toast.makeText(TakeAttendanceActivity.this, "No QR Code found for attendance", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // If onCancelled event occurs (e.g., due to network error)

                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Show a toast with the error message
                Toast.makeText(TakeAttendanceActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to initiate the QR code scanning process
    private void scanCode() {
        // Configure the options for QR code scanning
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CapActivity.class);

        // Launch the QR code scanner activity
        launcher.launch(options);
    }

    // Activity result launcher for QR code scanning
    ActivityResultLauncher<ScanOptions> launcher = registerForActivityResult(new ScanContract(), result -> {
        // When QR code scanning result is received

        if (result.getContents() != null) {
            // If QR code content is not null

            // Get the scanned QR code
            String getCode = result.getContents();

            // Search for the scanned QR code in the codesList
            for(int i = 0; i< codesList.size(); i++){
                if (codesList.get(i).getCode().equals(getCode)){
                    codeMainModel = codesList.get(i);
                    break;
                }
            }

            // Check if the scanned QR code exists
            if (codeMainModel != null) {
                // If scanned QR code exists

                // Check the status of the code
                if (codeMainModel.getStatus().contains("Available")){
                    // If the code is available, proceed to save attendance data
                    progressDialog.show();
                    saveData("new");
                } else {
                    // If the code is not available

                    // Check if the code is assigned to the current user
                    if (codeMainModel.getUserId().equals(auth.getCurrentUser().getUid())){
                        // If the code is assigned to the current user, proceed to save attendance data
                        saveData("old");
                    } else {
                        // If the code is assigned to another member, show a toast message and finish the activity
                        Toast.makeText(this, "Invalid Code, Already assigned to another member", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            } else {
                // If scanned QR code doesn't exist, show a toast message and finish the activity
                Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // If QR code content is null, finish the activity
            finish();
        }
    });

    // Method to save attendance data
    private void saveData(String from){

        if (from.equals("new")){
            // If attendance data is new

            // Get the current date and format it
            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat;
            dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss", Locale.getDefault());
            String formattedDate = dateFormat.format(currentDate);

            // Update the status of the scanned QR code to "Booked" and set the booked date and user ID
            Map<String, Object> update = new HashMap<>();
            update.put("bookedDate", formattedDate);
            update.put("userId", auth.getCurrentUser().getUid());
            update.put("status", "Booked");
            dbRefCodes.child(codeMainModel.getCodeId()).updateChildren(update).addOnCompleteListener(task -> {
                // When updating data is complete

                // Increment the gym capacity count
                gymCapacityCount  = gymCapacityCount+1;
                GymCapacityModel model = new GymCapacityModel(String.valueOf(gymCapacityCount));

                // Update the gym capacity in the database
                dbRefGymCapacitor.setValue(model).addOnCompleteListener(task1 -> {
                    // When updating gym capacity is complete, save attendance into history
                    saveIntoHistory();
                }).addOnFailureListener(e -> {
                    // If updating gym capacity fails, dismiss the progress dialog and show a toast with the error message
                    progressDialog.dismiss();
                    Toast.makeText(TakeAttendanceActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                // If updating data fails, dismiss the progress dialog and show a toast with the error message
                progressDialog.dismiss();
                Toast.makeText(TakeAttendanceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // If attendance data is old, save attendance into history directly
            saveIntoHistory();
        }
    }

    // Method to save attendance data into history
    private void saveIntoHistory(){
        // Generate a unique ID for the attendance record
        String pushId = dbRefAttendance.push().getKey();

        // Get the current date and format it
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Create an AttendanceModel object with attendance details
        AttendanceModel model = new AttendanceModel(pushId, auth.getCurrentUser().getUid(), getCurrentDate, formattedDate);

        // Set the attendance data in the database
        dbRefAttendance.child(pushId).setValue(model).addOnCompleteListener(task -> {
            // When setting data is complete

            if (task.isSuccessful()){
                // If setting data is successful, dismiss the progress dialog, show a toast indicating success, and finish the activity
                progressDialog.dismiss();
                Toast.makeText(TakeAttendanceActivity.this, "Successfully Taken for today", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // If setting data fails, dismiss the progress dialog and show a toast with the error message
                progressDialog.dismiss();
                Toast.makeText(TakeAttendanceActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If an error occurs while setting data, dismiss the progress dialog and show a toast with the error message
                progressDialog.dismiss();
                Toast.makeText(TakeAttendanceActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to get the current date
    private void getCurrentDate(){
        // Get the current date
        Date c = Calendar.getInstance().getTime();

        // Format the current date
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        getCurrentDate = df.format(c);
    }



}