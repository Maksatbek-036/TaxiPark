package com.example.project;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project.databinding.FragmentProfileBinding;

public class FragmentProfile extends Fragment {

    private FragmentProfileBinding binding;


    private final ActivityResultLauncher<String> profilePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    binding.imageProfile.setImageURI(uri);
                    // Тут будет логика отправки аватарки на сервер
                }
            }
    );


    private final ActivityResultLauncher<String> licensePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    binding.imageLicences.setImageURI(uri);
                    // Тут будет логика отправки лицензии на сервер
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Заполнение данных (заглушки)
        binding.editPhone.setText("+996774250425");
        binding.editEmail.setText("maksatbek036@gmail.com");

        // Клики по фото
        binding.imageProfile.setOnClickListener(v -> {
            profilePhotoLauncher.launch("image/*");
        });

        binding.imageLicences.setOnClickListener(v -> {
            licensePhotoLauncher.launch("image/*");
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}