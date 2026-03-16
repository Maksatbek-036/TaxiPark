package com.example.project;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project.API.Api;
import com.example.project.API.DriverEditRequest;
import com.example.project.API.RetrofitClient;
import com.example.project.databinding.FragmentProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentProfile extends Fragment {

    private FragmentProfileBinding binding;
    private Uri profileUri;
    private Uri licenseUri;


    private final ActivityResultLauncher<String> profilePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    profileUri = uri;
                    binding.imageProfile.setImageURI(uri);
                }
            }
    );


    private final ActivityResultLauncher<String> licensePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    licenseUri = uri;
                    binding.imageLicences.setImageURI(uri);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Установка слушателей клика
        binding.imageProfile.setOnClickListener(v -> profilePhotoLauncher.launch("image/*"));
        binding.imageLicences.setOnClickListener(v -> licensePhotoLauncher.launch("image/*"));
        binding.saveButton.setOnClickListener(v -> saveProfileData());

        return binding.getRoot();
    }

    private void saveProfileData() {
        Memory memory = new Memory(getContext());
        int id = memory.getDriver().getId();

        String name = binding.editName.getText().toString().trim();
        String phone = binding.editPhone.getText().toString().trim();
        String login = binding.editEmail.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || login.isEmpty()) {
            Toast.makeText(getContext(), "Заполните текстовые поля", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.saveButton.setEnabled(false);

        // Используем createFormData для всех текстовых полей (решает проблему ошибки 400)
        MultipartBody.Part idPart = MultipartBody.Part.createFormData("Id", String.valueOf(id));
        MultipartBody.Part namePart = MultipartBody.Part.createFormData("Name", name);
        MultipartBody.Part phonePart = MultipartBody.Part.createFormData("Phone", phone);
        MultipartBody.Part loginPart = MultipartBody.Part.createFormData("Login", login);

        // Подготовка файлов (если uri == null, переменная будет null)
        MultipartBody.Part profileFile = prepareFilePart("ProfilePhoto", profileUri);
        MultipartBody.Part licenseFile = prepareFilePart("Licenses", licenseUri);

        Api api = RetrofitClient.getInstance().getApi();

        // Передаем всё в метод. Retrofit корректно обработает null-части,
        // если они помечены в интерфейсе как MultipartBody.Part
        api.driverEdit(idPart, namePart, phonePart, loginPart, profileFile, licenseFile)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (isAdded()) {
                            binding.saveButton.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Данные обновлены!", Toast.LENGTH_SHORT).show();
                                // Сбрасываем URI после успешной отправки
                                profileUri = null;
                                licenseUri = null;
                            } else {
                                // Если 400 ошибка осталась, значит сервер требует файлы обязательно
                                Toast.makeText(getContext(), "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        if (isAdded()) {
                            binding.saveButton.setEnabled(true);
                            Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        if (fileUri == null)
            return null;
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
            byte[] bytes = getBytes(inputStream);
            String mimeType = requireContext().getContentResolver().getType(fileUri);

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType != null ? mimeType : "image/*"), bytes);
            return MultipartBody.Part.createFormData(partName, "image.jpg", requestFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}