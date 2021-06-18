package com.felipesotero.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.storage.FirebaseStorage;

public class StorageActivity extends AppCompatActivity {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        btnUpload = findViewById(R.id.storage_btn_upload);

        btnUpload.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://lh3.googleusercontent.com/proxy/KyF9IqPjVeoKiafWVwwkYPNAFrx7HULggo2q8S4vx8Q9N3HmjlBAWOh5iVVkW8JhGEea8gDQewirGk3JtNArLy2uZZT5ZV6ar545okAxOY5TVWU6deje5C2gni3qeOX5xCQ3");
            storage.getReference().putFile(uri);
        });
    }
}
