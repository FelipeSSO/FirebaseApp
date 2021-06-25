package com.felipesotero.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.felipesotero.firebaseapp.Adapter.ImageAdapter;
import com.felipesotero.firebaseapp.model.Upload;
import com.felipesotero.firebaseapp.util.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView textEmail, textNome;
    private Button btnLogout, btnStorage;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    private ArrayList<Upload> listAUploads = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textEmail = findViewById(R.id.main_text_email);
        textNome = findViewById(R.id.main_text_user);
        btnLogout = findViewById(R.id.main_btn_logout);
        btnStorage = findViewById(R.id.main_btn_storage);
        recyclerView = findViewById(R.id.main_recycler);

        imageAdapter = new ImageAdapter(getApplicationContext(), listAUploads);
        imageAdapter.setListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Upload upload = listAUploads.get(position);
                deleteUpload(upload);
            }

            @Override
            public void onUploadClick(int position) {
                Upload upload = listAUploads.get(position);
                Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
                //envia o upload para outra activity
                intent.putExtra("upload", upload);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager( new LinearLayoutManager(getApplicationContext()) );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageAdapter);

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
        *    - Faz parte do ciclo de vida da Activity,
        *    - Depois do onCreate(),
        *    - É executado quando app inicia,
        *    - E quando volta ao background. */
        super.onStart();
        getData();
    }

    public void deleteUpload(Upload upload){
        LoadingDialog dialog = new LoadingDialog(this, R.layout.custom_dialog);
        dialog.startLoadingDialog();

        // Deletar imagem no Storage
        StorageReference imagemRef = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getUrl());
        imagemRef.delete()
            .addOnSuccessListener(aVoid -> {
                // Deletar imagem no Database
                database.child(upload.getId()).removeValue()
                    .addOnSuccessListener(aVoid1 -> {
                        Toast.makeText(getApplicationContext(), "Item Deletado!", Toast.LENGTH_SHORT).show();
                        dialog.dismissDialog();
                    });
            });
    }

    public void getData(){
        // Listener para um nó uploads
        database.addValueEventListener(new ValueEventListener() {
            // Caso ocorra alguma alteração -> retorna TODOS os dados
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAUploads.clear();
                for(DataSnapshot no_filho : snapshot.getChildren()){
                    Upload upload = no_filho.getValue(Upload.class);
                    listAUploads.add(upload);
                    Log.i("DATABASE", "id: " +upload.getId()+ ", nome: " +upload.getNomeImagem());
                }
                imageAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }

}
