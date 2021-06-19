package com.felipesotero.firebaseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class StorageActivity extends AppCompatActivity {
    // Referencia para o FirebaseStorage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ImageView imageView;
    private EditText editNome;
    private Button btnUpload, btnGaleria;
    private Uri imageUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        btnUpload = findViewById(R.id.storage_btn_upload);
        imageView = findViewById(R.id.storage_image_cel);
        editNome = findViewById(R.id.storage_edit_nome);
        btnGaleria = findViewById(R.id.storage_btn_galeria);

        btnUpload.setOnClickListener(v -> {
            if(editNome.getText().toString().isEmpty()){
                Toast.makeText(this, "Digite um nome para a imagem!", Toast.LENGTH_SHORT).show();
            }
            if(imageUri != null){
                uploadImagemUri();
            }else{
                uploadImagemByte();
            }
        });
        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent();
            // intent implicita -> pegar um arquivo do celular
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,1);
        });
    }

    private void uploadImagemUri() {
        String tipo = getFileExtension(imageUri);
        // Referencia do arquivo no Firebase
        String nome = editNome.getText().toString();
        StorageReference imagemRef = storage.getReference().child("imagens/"+nome+"."+tipo);

        imagemRef.putFile(imageUri)
        .addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this, "Upload feito com sucesso", Toast.LENGTH_SHORT).show();
        })
        .addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }

    // Retornar o tipo(.png, .jpn) da imagem
    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("RESULT", "requesteCode: "+ requestCode +", resultCode: "+ resultCode);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            // Caso o usuario selecinou uma imagem da galeria
            imageUri = data.getData();
            // Endereco da imagem selecionada
            imageView.setImageURI(imageUri);
        }
    }

    // Coverter ImageViel -> byte[]
    public byte[] conertImage2Byte(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable() ).getBitmap();
        // Objeto baos -> armazenar a imagem convertida
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    // Fazer o upload de uma imagem convertida para bytes
    public void uploadImagemByte(){
        byte[] data = conertImage2Byte(imageView);
        // Criar uma referencia para a imagem no storage
        StorageReference imagemRef = storage.getReference().child("imagens/02.jpeg");

        // Realiza o upload da imagem
        imagemRef.putBytes(data)
        .addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this, "Upload feito com sucesso", Toast.LENGTH_SHORT).show();
        })
        .addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }
}
