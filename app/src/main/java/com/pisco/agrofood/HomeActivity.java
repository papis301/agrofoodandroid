package com.pisco.agrofood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_PHONE = "phone";
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 🔹 Récupère le téléphone depuis la session (stocké après connexion)
        phoneNumber = getSharedPreferences("UserSession", MODE_PRIVATE).getString("phone", null);

        if (phoneNumber == null) {
            Toast.makeText(this, "Session expirée, veuillez vous reconnecter", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginTemplate.class));
            finish();
            return;
        }

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

            Intent intent = new Intent(HomeActivity.this, LoginTemplate.class);
            startActivity(intent);
            finish();
        });

        btnproduits.setOnClickListener(v -> {


            Intent intent = new Intent(HomeActivity.this, MesProduitsActivity.class);
            startActivity(intent);

        });

        btnaddprod.setOnClickListener(v -> {


            Intent intent = new Intent(HomeActivity.this, AddProductActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
