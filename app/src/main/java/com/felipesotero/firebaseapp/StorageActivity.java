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

import com.felipesotero.firebaseapp.model.Upload;
import com.felipesotero.firebaseapp.util.LoadingDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class StorageActivity extends AppCompatActivity {
    // Referencia para o FirebaseStorage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    // Referencia para um nó RealTimeDB
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    // Atributos
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
            // Inicia uma Activity, e espera um retorno(foto)
            startActivityForResult(intent,1);
        });
    }

    private void uploadImagemUri() {
        LoadingDialog dialog = new LoadingDialog(this, R.layout.custom_dialog);
        dialog.startLoadingDialog();

        String tipo = getFileExtension(imageUri);
        // Referencia do arquivo no Firebase
        Date d = new Date();
        String nome = editNome.getText().toString();
        // Criando uma referencia a imagem no Storage
        StorageReference imagemRef = storage.getReference().child("imagens/"+nome+"-"+d.getTime()+"."+tipo);

        imagemRef.putFile(imageUri)
            .addOnSuccessListener(taskSnapshot -> {
                // Inserir os dados da imagem no RealTimeDatabase -> Pegar a URL da imagem
                taskSnapshot.getStorage().getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        // Criando referencia(database) do upload
                        DatabaseReference refUpload = database.push();
                        String id = refUpload.getKey();

                        Upload upload = new Upload(id, nome, uri.toString());
                        // Salvando upload no database
                        refUpload.setValue(upload)
                            .addOnSuccessListener(aVoid -> {
                                dialog.dismissDialog();
                                Toast.makeText(getApplicationContext(), "Upload feito com sucesso", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                    });
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

    // Resultado do StartActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("RESULT", "requesteCode: "+ requestCode +", resultCode: "+ resultCode);

        // Caso o usuario selecinou uma imagem da galeria
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            // Endereco da imagem selecionada
            imageUri = data.getData();
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
        StorageReference imagemRef = storage.getReference().child("imagens/01.jpeg");

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
