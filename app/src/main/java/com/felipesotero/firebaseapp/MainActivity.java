package com.felipesotero.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TextView textEmail, textNome;
    private Button btnLogout, btnStorage;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textEmail = findViewById(R.id.main_text_email);
        textNome = findViewById(R.id.main_text_user);
        btnLogout = findViewById(R.id.main_btn_logout);
        btnStorage = findViewById(R.id.main_btn_storage);

        btnLogout.setOnClickListener(v -> {
            // Deslogar Usuario
            auth.signOut();
            finish();
        });

        textEmail.setText(auth.getCurrentUser().getEmail());
        textNome.setText(auth.getCurrentUser().getDisplayName());

    }

}
