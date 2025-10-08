package com.pisco.agrofood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_PHONE = "phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView txtWelcome = findViewById(R.id.txtWelcome);
        Button btnLogout = findViewById(R.id.btnLogout);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button btnproduits = findViewById(R.id.btnproduits);
        Button btnaddprod = findViewById(R.id.btnaddprod);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String phone = prefs.getString(KEY_PHONE, "Utilisateur");

        txtWelcome.setText("Bienvenue, " + phone);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnproduits.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            startActivity(intent);
            finish();
        });

        btnaddprod.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, AddProductActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
