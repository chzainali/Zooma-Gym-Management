package com.example.zoomagymmanagement.member.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.admin.AdminMainActivity;
import com.example.zoomagymmanagement.admin.FeedbackActivity;
import com.example.zoomagymmanagement.admin.FitnessPlansActivity;
import com.example.zoomagymmanagement.admin.StepCountsActivity;
import com.example.zoomagymmanagement.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    Animation rotate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rotate = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate);
        binding.ivLogo.setAnimation(rotate);

        binding.cvFitnessPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), FitnessPlansActivity.class);
                intent.putExtra("from", "user");
                startActivity(intent);
            }
        });

        binding.cvAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_attendanceFragment);
            }
        });

        binding.cvStepsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), StepCountsActivity.class);
                startActivity(intent);
            }
        });

        binding.cvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), FeedbackActivity.class);
                intent.putExtra("from", "user");
                startActivity(intent);
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}