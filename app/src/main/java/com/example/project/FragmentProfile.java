package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project.databinding.FragmentProfileBinding;

public class FragmentProfile extends Fragment {



    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Доступ к элементам напрямую
        binding.editPhone.setText("+996774250425");
        binding.editEmail.setText("maksatbek036@gmail.com");

        // Обработка клика по фото
        binding.imageProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        });

        // Сохранение данных по кнопке

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // освобождаем биндинг
    }
}