package com.felipesotero.firebaseapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.felipesotero.firebaseapp.R;
import com.felipesotero.firebaseapp.model.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH> {
    private ArrayList<User> listaUsuarios;
    private Context context;

    public UserAdapter(ArrayList<User> lista, Context c){
        this.listaUsuarios = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_recycle, parent, false);
        return new UserVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        User u = listaUsuarios.get(position);
        holder.textEmail.setText(u.getEmail());
        holder.textNome.setText(u.getNome());
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class UserVH extends RecyclerView.ViewHolder{
        TextView textNome;
        TextView textEmail;
        RoundedImageView imgPhoto;
        Button btnAdicionar;

        public UserVH(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.user_recycler_nome);
            textEmail = itemView.findViewById(R.id.user_recycler_email);
            imgPhoto = itemView.findViewById(R.id.user_recycler_photo);
            btnAdicionar = itemView.findViewById(R.id.user_recycler_btn_add);

        }

    }

}
