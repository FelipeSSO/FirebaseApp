package com.felipesotero.firebaseapp.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.felipesotero.firebaseapp.R;
import com.felipesotero.firebaseapp.model.Upload;
import com.felipesotero.firebaseapp.util.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class StorageFragment extends Fragment {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    private ImageView imageView;
    private EditText editNome;
    private Button btnUpload, btnGaleria;
    private Uri imageUri=null;

    public StorageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference("uploads").child(auth.getUid());
        View layout = inflater.inflate(R.layout.fragment_storage, container, false);

        btnUpload = layout.findViewById(R.id.update_btn_upload);
        imageView = layout.findViewById(R.id.storage_image_cel);
        editNome = layout.findViewById(R.id.update_edit_nome);
        btnGaleria = layout.findViewById(R.id.update_btn_galeria);

        btnUpload.setOnClickListener(v -> {
            if(editNome.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Digite um nome para a imagem!", Toast.LENGTH_SHORT).show();
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

        return layout;
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getActivity().getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }

    private void uploadImagemUri() {
        LoadingDialog dialog = new LoadingDialog(getActivity(), R.layout.custom_dialog);
        dialog.startLoadingDialog();

        String tipo = getFileExtension(imageUri);
        Date d = new Date();
        String nome = editNome.getText().toString();
        StorageReference imagemRef = storage.getReference().child("imagens/"+nome+"-"+d.getTime()+"."+tipo);

        imagemRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                DatabaseReference refUpload = database.push();
                                String id = refUpload.getKey();

                                Upload upload = new Upload(id, nome, uri.toString());
                                refUpload.setValue(upload)
                                        .addOnSuccessListener(aVoid -> {
                                            dialog.dismissDialog();
                                            Toast.makeText(getActivity(), "Upload feito com sucesso", Toast.LENGTH_SHORT).show();
                                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                            // Voutar para o fragment inicial
                                            navController.navigateUp();
                                        });

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("RESULT", "requesteCode: "+ requestCode +", resultCode: "+ resultCode);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    public byte[] conertImage2Byte(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable() ).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public void uploadImagemByte(){
        byte[] data = conertImage2Byte(imageView);
        StorageReference imagemRef = storage.getReference().child("imagens/01.jpeg");

        imagemRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getActivity(), "Upload feito com sucesso", Toast.LENGTH_SHORT).show();
                })

                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

}
