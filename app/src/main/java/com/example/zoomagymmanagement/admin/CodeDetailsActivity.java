package com.example.zoomagymmanagement.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.auth.LoginActivity;
import com.example.zoomagymmanagement.databinding.ActivityCodeDetailsBinding;
import com.example.zoomagymmanagement.databinding.ActivityFeedbackDetailsBinding;
import com.example.zoomagymmanagement.member.MemberMainActivity;
import com.example.zoomagymmanagement.member.ui.profile.EditProfileActivity;
import com.example.zoomagymmanagement.model.CodesModel;
import com.example.zoomagymmanagement.model.HelperClass;
import com.example.zoomagymmanagement.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

public class CodeDetailsActivity extends AppCompatActivity {
    ActivityCodeDetailsBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    DatabaseReference dbRefCodes;
    CodesModel codesModel;
    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCodeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        dbRefCodes = FirebaseDatabase.getInstance().getReference("QRCodes");

        if (getIntent().getExtras() != null){
            codesModel = (CodesModel) getIntent().getSerializableExtra("data");
            if (codesModel != null){
                if (codesModel.getStatus().equals("Booked") && !codesModel.getUserId().equals("")){
                    binding.cvUser.setVisibility(View.VISIBLE);
                    getUserData();
                }else{
                    binding.cvUser.setVisibility(View.GONE);
                }
                MultiFormatWriter mWriter = new MultiFormatWriter();
                try {
                    BitMatrix mMatrix = mWriter.encode(codesModel.getCode(), BarcodeFormat.QR_CODE, 400,400);
                    BarcodeEncoder mEncoder = new BarcodeEncoder();
                    mBitmap = mEncoder.createBitmap(mMatrix);
                    binding.ivQRCode.setImageBitmap(mBitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }

        binding.ivBack.setOnClickListener(view -> finish());

        binding.btnShare.setOnClickListener(view -> {
            if (mBitmap != null){
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                Uri imageUri = getImageUri(CodeDetailsActivity.this, mBitmap);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                Intent chooserIntent = Intent.createChooser(shareIntent, "Share post via");
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooserIntent);
                } else {
                    Toast.makeText(CodeDetailsActivity.this, "No apps available for sharing", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.ivDelete.setOnClickListener(view -> {
            if (codesModel != null){
                dbRefCodes.child(codesModel.getCodeId()).removeValue();
                Toast.makeText(CodeDetailsActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void getUserData() {
        progressDialog.show();
        dbRefUsers.child(codesModel.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel model = snapshot.getValue(UserModel.class);
                    Glide.with(CodeDetailsActivity.this).load(model.getImage()).into(binding.ivProfile);
                    binding.tvUserName.setText(model.getName());
                    binding.tvDate.setText("Assigned At: "+codesModel.getBookedDate());
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(CodeDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String title = "Title_" + System.currentTimeMillis();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, null);
        return Uri.parse(path);
    }

    // Helper method to check if the app is installed
    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}