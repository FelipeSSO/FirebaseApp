package com.felipesotero.firebaseapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.felipesotero.firebaseapp.R;
import com.felipesotero.firebaseapp.adapter.UserAdapter;
import com.felipesotero.firebaseapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment {
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    private DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<User> listaContato = new ArrayList<>();
    private RecyclerView recyclerContatos;
    private UserAdapter userAdapter;
    private User userLogged;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);
        userLogged = new User(auth.getCurrentUser().getUid(),
                auth.getCurrentUser().getEmail(), auth.getCurrentUser().getDisplayName() );
        recyclerContatos = layout.findViewById(R.id.frag_main_recycler_user);

        userAdapter = new UserAdapter(listaContato, getContext());
        userAdapter.setListener(new UserAdapter.ClickAdapterUser() {

            @Override
            public void adicionarContato(int position) {
                User u = listaContato.get(position);
                // Request Send
                requestRef.child(userLogged.getId()).child("send").child(u.getId()).setValue(u);
                // Request Receive
                requestRef.child(u.getId()).child("receive").child(userLogged.getId()).setValue(userLogged);
                // Tirar o usu??rio solicitado
                listaContato.get(position).setReceiveRequest(true);
                userAdapter.notifyDataSetChanged();
            }
        });

        recyclerContatos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerContatos.setAdapter(userAdapter);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        getUsersDatabase();
    }

    private void getUsersDatabase(){
        // ??ra armazenar usu??rios que j?? foram solicitados
        Map<String, User> mapUsersRek = new HashMap<String, User>();

        requestRef.child(userLogged.getId()).child("send")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot u : snapshot.getChildren()){
                            User user = u.getValue(User.class);

                            // Adicionando o usuario no HashMap
                            mapUsersRek.put(user.getId(), user);
                        }

                        // ler o n?? usu??rio
                        usersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                listaContato.clear();

                                for(DataSnapshot u : snapshot.getChildren()) {
                                    User user = u.getValue(User.class);

                                    if(mapUsersRek.containsKey(user.getId())){
                                        user.setReceiveRequest(true);
                                    }
                                    if(!userLogged.equals(user)){
                                        listaContato.add(user);
                                    }
                                }
                                userAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //
                    }
                });
    }
}
