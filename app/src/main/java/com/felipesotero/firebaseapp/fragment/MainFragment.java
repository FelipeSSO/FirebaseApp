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
                // Tirar o usuário solicitado
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
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaContato.clear();
                for(DataSnapshot filho : snapshot.getChildren()){
                    User u = filho.getValue(User.class);

                    // Comparar com o usuário logado
                    if(!userLogged.equals(u)){
                        /*if(cont%2 == 0){
                            u.setReceiveRequest(true);
                        } else {
                            u.setReceiveRequest(false);
                        }*/
                        listaContato.add(u);
                    }
                }

                // Verificar quais contatos foram adicionados
                requestRef.child(userLogged.getId()).child("send").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot no_filho : snapshot.getChildren()){
                            User usuarioSolicitado = no_filho.getValue(User.class);

                            for(int i=0; i<listaContato.size(); i++){
                                if(listaContato.get(i).equals(usuarioSolicitado)){
                                    listaContato.get(i).setReceiveRequest(true);
                                }
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
