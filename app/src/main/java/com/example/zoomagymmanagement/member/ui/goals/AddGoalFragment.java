package com.example.zoomagymmanagement.member.ui.goals;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.FragmentAddGoalBinding;
import com.example.zoomagymmanagement.model.GoalModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddGoalFragment extends Fragment {
    private FragmentAddGoalBinding binding;
    String[] workouts = {"Shoulders", "Triceps", "Biceps", "Chest", "Back", "Legs", "Cardio", "Core", "Olympic"};
    String workoutType = "", status = "";
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    String title, date, target, achieved;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefGoals;
    GoalModel previousModel;

    public AddGoalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            previousModel = (GoalModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddGoalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (previousModel != null) {
            workoutType = previousModel.getType();
            binding.tvHeading.setText("Update Goal");
            binding.btnSave.setText("Update");
            binding.llWorkout.setVisibility(View.GONE);
            binding.goalEt.setText(previousModel.getTitle());
            binding.targetDateEt.setText(previousModel.getTargetDate());
            binding.targetEt.setText(previousModel.getTarget());
            binding.achievedEt.setText(previousModel.getAchieved());
        }

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefGoals = FirebaseDatabase.getInstance().getReference("Goals");

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        });

        binding.targetDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(requireContext(), R.layout.item_workout, workouts);
        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        binding.spWorkout.setAdapter(adapter);

        binding.spWorkout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                workoutType = workouts[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.btnSave.setOnClickListener(view1 -> {
            if (isValidated()) {
                progressDialog.show();
                if (previousModel != null) {
                    if (Integer.parseInt(target) == Integer.parseInt(achieved)){
                        status = "Completed";
                    }else{
                        status = "Pending";
                    }
                    Map<String, Object> update = new HashMap<>();
                    update.put("title", title);
                    update.put("targetDate", date);
                    update.put("target", target);
                    update.put("achieved", achieved);
                    update.put("status", status);
                    dbRefGoals.child(previousModel.getGoalId()).updateChildren(update).addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        showMessage("Updated Successfully");
                        Navigation.findNavController(view1).navigateUp();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        showMessage(e.getLocalizedMessage());
                    });
                } else {
                    String pushId = dbRefGoals.push().getKey();
                    if (Integer.parseInt(target) == Integer.parseInt(achieved)){
                        status = "Completed";
                    }else{
                        status = "Pending";
                    }
                    GoalModel model = new GoalModel(pushId, auth.getCurrentUser().getUid(), workoutType, title, date, target, achieved, status);
                    dbRefGoals.child(pushId).setValue(model).addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        showMessage("Added Successfully");
                        Navigation.findNavController(view1).navigateUp();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        showMessage(e.getLocalizedMessage());
                    });
                }
            }
        });

    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(year, monthOfYear, dayOfMonth);
            String selectedDate = dateFormatter.format(calendar.getTime());
            binding.targetDateEt.setText(selectedDate);
        }
    };

    private Boolean isValidated() {
        title = binding.goalEt.getText().toString().trim();
        date = binding.targetDateEt.getText().toString().trim();
        target = binding.targetEt.getText().toString().trim();
        achieved = binding.achievedEt.getText().toString().trim();

        if (title.isEmpty()) {
            showMessage("Please enter goal title");
            return false;
        }
        if (date.isEmpty()) {
            showMessage("Please enter target date");
            return false;
        }

        if (target.isEmpty()) {
            showMessage("Please enter target duration in minutes");
            return false;
        }
        if (achieved.isEmpty()) {
            showMessage("Please enter achieved duration in minutes");
            return false;
        }

        if (Integer.parseInt(target) < Integer.parseInt(achieved)) {
            showMessage("Target duration should be greater or equal to the achieved duration");
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}