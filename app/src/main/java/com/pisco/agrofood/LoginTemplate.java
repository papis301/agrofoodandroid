package com.pisco.agrofood;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginTemplate extends AppCompatActivity {

    private EditText edtPhone, edtPassword;
    private Button btnLogin, btnRegister;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FIREBASE_ID = "firebase_id";
    private static final String TAG = "LoginTemplate";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_template);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnsinscrire);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connexion en cours...");
        progressDialog.setCancelable(false);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> attemptLogin());

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginTemplate.this, RegisterTemplate.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Numéro requis");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Mot de passe requis");
            return;
        }

        // 🔹 Afficher le message de chargement
        progressDialog.show();

        // 🔍 Vérification dans Firestore
        db.collection("usersagrofood")
                .whereEqualTo("phone", phone)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // 🔹 Afficher le message de chargement
                    progressDialog.dismiss();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // ✅ Connexion réussie
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String firebaseId = document.getId();
                            String phoneNumber = document.getString("phone");

                            // 🔹 Sauvegarde dans SharedPreferences
                            saveUserSession(phoneNumber, firebaseId);

                            Toast.makeText(this, "Connexion réussie ✅", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Utilisateur connecté : " + firebaseId);

                            Intent intent = new Intent(LoginTemplate.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    } else {
                        Toast.makeText(this, "Identifiants incorrects ❌", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Erreur de connexion : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erreur Firestore", e);
                });
    }

    private void saveUserSession(String phone, String firebaseId) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_FIREBASE_ID, firebaseId);
        editor.apply();
    }
}
