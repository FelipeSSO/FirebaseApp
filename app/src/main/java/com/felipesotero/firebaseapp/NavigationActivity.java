package com.felipesotero.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.felipesotero.firebaseapp.util.NotificationService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavigationActivity extends AppCompatActivity {
    private ImageView btnMenu;
    private DrawerLayout drawerLayout;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        btnMenu = findViewById(R.id.navigation_icon);
        drawerLayout = findViewById(R.id.nav_drawerLayout);

        btnMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        NavigationView navigationView = findViewById(R.id.navigationView);

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_nome))
                                                .setText(auth.getCurrentUser().getDisplayName());

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_email))
                                                .setText(auth.getCurrentUser().getEmail());

        // Recuperar o navControler -> realiza a troca de fragments
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Juntar o navControler com o navView
        NavigationUI.setupWithNavController(navigationView, navController);

        // Criar um serviço
        Intent service = new Intent(getApplicationContext(), NotificationService.class);
        getApplicationContext().startService(service);

        // Evento logout
        navigationView.getMenu().findItem(R.id.nav_menu_logout)
                .setOnMenuItemClickListener(item -> {
                    auth.signOut();
                    finish();
                    return false;
                });
    }

}
