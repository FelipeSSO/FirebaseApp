package com.felipesotero.firebaseapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.felipesotero.firebaseapp.R;
import com.felipesotero.firebaseapp.UpdateActivity;
import com.felipesotero.firebaseapp.adapter.ImageAdapter;
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

public class UploadFragment extends Fragment {
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ArrayList<Upload> listAUploads = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_upload, container, false);
        recyclerView = layout.findViewById(R.id.main_recycler);

        imageAdapter = new ImageAdapter(getContext(), listAUploads);
        imageAdapter.setListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Upload upload = listAUploads.get(position);
                deleteUpload(upload);
            }
            @Override
            public void onUploadClick(int position) {
                Upload upload = listAUploads.get(position);
                Intent intent = new Intent(getActivity(), UpdateActivity.class);
                intent.putExtra("uploads", upload);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()) );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageAdapter);

        return layout;
    }

    public void deleteUpload(Upload upload){
        LoadingDialog dialog = new LoadingDialog(getActivity(), R.layout.custom_dialog);
        dialog.startLoadingDialog();

        StorageReference imagemRef = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getUrl());
        imagemRef.delete()
                .addOnSuccessListener(aVoid -> {
                    database.child(upload.getId()).removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), "Item Deletado!", Toast.LENGTH_SHORT).show();
                                dialog.dismissDialog();
                            });
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    public void getData(){
        database.addValueEventListener(new ValueEventListener() {
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
            }
        });
    }

}
