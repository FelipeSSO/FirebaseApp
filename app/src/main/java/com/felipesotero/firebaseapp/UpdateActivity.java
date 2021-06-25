package com.felipesotero.firebaseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.felipesotero.firebaseapp.model.Upload;
import com.felipesotero.firebaseapp.util.LoadingDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class UpdateActivity extends AppCompatActivity {
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ImageView imageView;
    private EditText editNome;
    private Button btnUpload, btnGaleria;
    private Uri imageUri=null;
    private Upload upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        btnUpload = findViewById(R.id.update_btn_upload);
        imageView = findViewById(R.id.update_image_cel);
        editNome = findViewById(R.id.update_edit_nome);
        btnGaleria = findViewById(R.id.update_btn_galeria);

        // Recuperar o upload selecionado
        upload = (Upload) getIntent().getSerializableExtra("upload");
        editNome.setText(upload.getNomeImagem());
        Glide.with(this).load(upload.getUrl()).into(imageView);

        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,1);
        });
        btnUpload.setOnClickListener(v -> {
            if(editNome.getText().toString().isEmpty()){
                Toast.makeText(this, "Sem Nome!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Caso a imagem não tenha sido atualizada
            if(imageUri == null){
                // Atualizar o nome da imagem
                String nome = editNome.getText().toString();
                upload.setNomeImagem(nome);
                database.child(upload.getId()).setValue(upload)
                    .addOnSuccessListener(aVoid -> {
                        finish();
                    });
                return;
            }
            atualizarImagem();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    public void atualizarImagem(){
        // Deletar a imagem antiga no Storage
        storage.getReferenceFromUrl(upload.getUrl()).delete();

        //Fazer upload da imagem atualizada no Storage
        uploadImagemUri();

        // Recuperar a URL da imagem no storage

        //Atualizar no Databese

    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }

    private void uploadImagemUri() {
        LoadingDialog dialog = new LoadingDialog(this, R.layout.custom_dialog);
        dialog.startLoadingDialog();

        String tipo = getFileExtension(imageUri);
        Date d = new Date();
        String nome = editNome.getText().toString();
        StorageReference imagemRef = storage.getReference().child("imagens/"+nome+"-"+d.getTime()+"."+tipo);

        imagemRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Atualizar o objeto upload
                                upload.setUrl(uri.toString());
                                upload.setNomeImagem( editNome.getText().toString() );
                                database.child(upload.getId()).setValue(upload)
                                    .addOnSuccessListener(aVoid -> {
                                        dialog.dismissDialog();
                                        finish();
                                    });
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

}
