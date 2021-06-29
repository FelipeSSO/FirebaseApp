package com.felipesotero.firebaseapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.felipesotero.firebaseapp.R;
import com.felipesotero.firebaseapp.adapter.ImageAdapter;

public class UploadFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;


    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_storage, container, false);
        recyclerView = layout.findViewById(R.id.main_recycler);

        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()) );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageAdapter);


        return layout;

    }

}
