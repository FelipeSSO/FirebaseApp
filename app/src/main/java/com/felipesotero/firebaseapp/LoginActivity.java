package com.felipesotero.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private Button btnCadastrar;
    private Button btnLogin;
    private EditText editEmail, editSenha;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnCadastrar = findViewById(R.id.login_btn_cadastrar);
        btnLogin = findViewById(R.id.login_btn_logar);
        editEmail = findViewById(R.id.login_edit_email);
        editSenha = findViewById(R.id.login_edit_senha);

        // Caso o usuario estiver Logado
        if(auth.getCurrentUser() != null){
            String email = auth.getCurrentUser().getEmail();
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            // Passar o email para a MainActivity
            intent.putExtra("email", email);
            startActivity(intent);
        }

        btnCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CadastroActivity.class);
            startActivity(intent);
        });
        btnLogin.setOnClickListener(v -> {
            logar();
        });
    }

    public void logar(){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        if(email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Preencha os Campos", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, senha)
        .addOnSuccessListener(authResult -> {
            Toast.makeText(this, "Bem Vindo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        })
        .addOnFailureListener(e ->{
            try {
                // Disparando a Excessão
                throw e;
            } catch (FirebaseAuthInvalidUserException userException){
                // Excessão para Email Invalido
                Toast.makeText(this, "Email Inválido", Toast.LENGTH_SHORT).show();
            }catch (FirebaseAuthInvalidCredentialsException credException){
                // Excessão para Senha Incorreta
                Toast.makeText(this, "Senha Incorreta", Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                // Excessão Generica
                Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
