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
    private ArrayList<User> listaContatos;
    private ClickAdapterUser listener;
    private Context context;

    private static final int TIPO_ADICIONAR = 0;
    private static final int TIPO_SOLICITAR = 1;

    public void setListener(ClickAdapterUser listener){
        this.listener = listener;
    }

    public UserAdapter(ArrayList<User> lista, Context c){
        this.listaContatos = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_recycle, parent, false);

        if(viewType == TIPO_SOLICITAR){
            Button b = v.findViewById(R.id.user_recycler_btn_add);
            b.setText("SOLICITADO");
            b.setEnabled(false);
        }
        return new UserVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        User u = listaContatos.get(position);
        holder.textEmail.setText(u.getEmail());
        holder.textNome.setText(u.getNome());
        if(!u.getReceiveRequest()){
            // Caso o usuario não foi adicionado
            holder.onClick();
        }
    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    @Override
    public int getItemViewType(int position) {
        User contato = listaContatos.get(position);
        // Se o usuario ja foi solicitado
        if(contato.getReceiveRequest()){
            return TIPO_SOLICITAR;
        }
        return TIPO_ADICIONAR;
    }

    public class UserVH extends RecyclerView.ViewHolder{
        TextView textNome;
        TextView textEmail;
        RoundedImageView imgPhoto;
        Button btnAdicionar;

        public void onClick(){
            btnAdicionar.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    listener.adicionarContato(position);
                }
            });
        }

        public UserVH(@NonNull View itemView) {
            super(itemView);

            textNome = itemView.findViewById(R.id.user_recycler_nome);
            textEmail = itemView.findViewById(R.id.user_recycler_email);
            imgPhoto = itemView.findViewById(R.id.user_recycler_photo);
            btnAdicionar = itemView.findViewById(R.id.user_recycler_btn_add);
        }
    }

    public interface ClickAdapterUser{
        void adicionarContato(int position);
    }

}
