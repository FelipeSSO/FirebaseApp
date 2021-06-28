package com.felipesotero.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationActivity extends AppCompatActivity {
    private ImageView btnMenu;
    private DrawerLayout drawerLayout;
    //private TextView nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        btnMenu = findViewById(R.id.navigation_icon);
        drawerLayout = findViewById(R.id.nav_drawerLayout);

        btnMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

    }

}
