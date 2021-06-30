package com.felipesotero.firebaseapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.felipesotero.firebaseapp.R;

public class MainFragment extends Fragment {

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);

        TextView textView = layout.findViewById(R.id.frag_main_text);

        textView.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT).show();
        });

        return layout;
    }

}
