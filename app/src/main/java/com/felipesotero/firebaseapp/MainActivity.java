package com.felipesotero.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.felipesotero.firebaseapp.model.Upload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView textEmail, textNome;
    private Button btnLogout, btnStorage;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    private ArrayList<Upload> listAUploads = new ArrayList<>();

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
        btnStorage.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StorageActivity.class);
            startActivity(intent);
        });

        textEmail.setText(auth.getCurrentUser().getEmail());
        textNome.setText(auth.getCurrentUser().getDisplayName());
    }

    @Override
    protected void onStart() {
        /* onStart:
        *    - Faz parte do ciclo de vida da Activity, Depois do onCreate(),
        *    - É executado quando app inicia, e quando volta ao background. */
        super.onStart();
        getData();
    }

    public void getData(){
        // Listener para um nó uploads
        database.addValueEventListener(new ValueEventListener() {
            // Caso ocorra alguma alteração -> retorna TODOS os dados
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot no_filho : snapshot.getChildren()){
                    Upload upload = no_filho.getValue(Upload.class);
                    listAUploads.add(upload);
                    Log.i("DATABASE", "id: " +upload.getId()+ ", nome: " +upload.getNomeImagem());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }

}
